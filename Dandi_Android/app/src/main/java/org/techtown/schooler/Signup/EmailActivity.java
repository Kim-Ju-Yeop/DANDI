package org.techtown.schooler.Signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.techtown.schooler.ChannelEvents.CreateChannelEvents;
import org.techtown.schooler.R;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.Model.Email;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author 이영은
 */

public class EmailActivity extends AppCompatActivity {

    EditText InputEmail;
    TextView SendAuthCode;
    TextView ErrorEmail;
    String AuthCode = null;
    LinearLayout Auth;
    LinearLayout Email;
    LinearLayout layout;
    TextView showUserEmail;
    EditText InputAuthCode;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        InputEmail = (EditText)findViewById(R.id.InputEmail);
        SendAuthCode = (TextView)findViewById(R.id.SendAuthCode);
        ErrorEmail = (TextView)findViewById(R.id.ErrorEmail);
        Auth =(LinearLayout)findViewById(R.id.InputAuthCodeLayout);
        Email = (LinearLayout)findViewById(R.id.InputEmailLayout);
        showUserEmail =(TextView)findViewById(R.id.showUserEmail);
        InputAuthCode = (EditText)findViewById(R.id.InputAuthCode);
        layout = (LinearLayout) findViewById(R.id.signUpEmailLayout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(),0);
            }
        });
        InputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(InputEmail.getText().toString()).matches()){
                   SendAuthCode.setEnabled(false);
                   SendAuthCode.setBackgroundResource(R.color.gray);
                }else{
                    SendAuthCode.setEnabled(true);
                    SendAuthCode.setBackgroundResource(R.color.mainColor);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                //입력후
            }
        });
        InputAuthCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(InputAuthCode.getText().toString().equals(AuthCode)){
                    SendAuthCode.setText("다음");
                    SendAuthCode.setEnabled(true);
                    SendAuthCode.setBackgroundResource(R.color.mainColor);
                }else{
                    SendAuthCode.setEnabled(false);
                    SendAuthCode.setBackgroundResource(R.color.gray);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void toClickSendAuth(View view){
        if(AuthCode == null){
            SendAuthCode.setEnabled(false);
            SendAuthCode.setText("이메일 전송중..");
            ErrorEmail.setVisibility(View.VISIBLE);

            Email email = new Email(InputEmail.getText().toString());

            final Call<Response<Data>> res = NetRetrofit.getInstance().getSignup().eamilPost(email);
            res.enqueue(new Callback<Response<Data>>() {
                @Override
                public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                    if(response.code() == 200){
                        AuthCode = response.body().getData().getAuthCode(); // authCode(이메일 코드)
                        Log.d("[SandEmail] AuthCode", AuthCode);
                        SendEmailSuccess();
                    }else if(response.code() == 500){
                        SendEmailFail();
                    }
                }

                @Override
                public void onFailure(Call<Response<Data>> call, Throwable t) {
                    Log.e("","네트워크 오류");
                    Toast.makeText(EmailActivity.this, "네크워크 상태가 원할하지 않습니다.\n잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                    SendEmailFail();
                }
            });
        }else{
            toGoNext();
        }
    }

    public void SendEmailSuccess() {
        ErrorEmail.setTextColor(Color.parseColor("#0ec600"));
        ErrorEmail.setVisibility(View.VISIBLE);
        InputEmail.setInputType(InputType.TYPE_NULL);
        showUserEmail.setText(InputEmail.getText().toString()+"으로\n인증번호를 전송하였습니다.");
        SendAuthCode.setBackgroundResource(R.color.gray);
        InputAuthCode.setEnabled(true);
        InputAuthCode.setText("");

        countDownTimer();
        Email.setVisibility(View.GONE);
        Auth.setVisibility(View.VISIBLE);
    }

    public void countDownTimer(){
        countDownTimer = new CountDownTimer(180000, 1000) { //30초 동안 1초의 간격으로 onTick 메소드를 호출합니다.
            @Override
            public void onTick(long millisUntilFinished) {
                long time = millisUntilFinished/1000;
                if(!InputAuthCode.getText().toString().equals(AuthCode))
                    SendAuthCode.setText("남은시간 : "+(time % 3600 / 60)+":"+(time % 3600 % 60));
            } //1초마다 호출되면서 남은 시간을 초 단위로 보여 줍니다. 30, 29, 28.. 이런식으로 나타나게 됩니다.

            @Override
            public void onFinish() {
                AuthCode = null;
                SendAuthCode.setText("시간만료\n재전송");
                InputAuthCode.setEnabled(false);
                InputAuthCode.setText("");
                SendAuthCode.setBackgroundResource(R.color.mainColor);
                SendAuthCode.setEnabled(true);
            } //종료 되었을 때, "done!" 이라는 문자열을 보여줍니다.
        }.start(); //카운트 시작
    }

    public void SendEmailFail() {
        ErrorEmail.setVisibility(View.VISIBLE);
        ErrorEmail.setTextColor(Color.parseColor("#bc0000"));
        ErrorEmail.setText("이메일 전송에 실패했습니다.\n이 문제가 계속되면 관리자에게 문의하십시오");
    }

    public void toGoBack(View view){
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void toGoNext(){
        Intent intent = new Intent(getApplicationContext(), PhoneNumberActivity.class);
        intent.putExtra("Name",getIntent().getStringExtra("Name"));
        intent.putExtra("Id",getIntent().getStringExtra("Id"));
        intent.putExtra("Password",getIntent().getStringExtra("Password"));
        intent.putExtra("Email",InputEmail.getText().toString());
        countDownTimer.cancel();
        InputAuthCode.setEnabled(false);
        showUserEmail.setText(InputEmail.getText().toString() +"으로\n이메일 인증이 완료되었습니다.");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
