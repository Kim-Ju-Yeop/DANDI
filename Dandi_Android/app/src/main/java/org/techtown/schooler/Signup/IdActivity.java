package org.techtown.schooler.Signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.schooler.ChannelEvents.CreateChannelEvents;
import org.techtown.schooler.R;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.Model.IsOverlapped;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author 이영은
 */

public class IdActivity extends AppCompatActivity {

    TextView noticeIdError;
    String ID;
    EditText InputId;
    TextView GotoPassword;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id);

        noticeIdError = (TextView) findViewById(R.id.noticeIdError);
        InputId = (EditText) findViewById(R.id.InputId);
        GotoPassword = (TextView)findViewById(R.id.next_password);

        GotoPassword.setBackgroundResource(R.color.gray);
        GotoPassword.setEnabled(false);

        layout = (LinearLayout) findViewById(R.id.signUpIdLayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(),0);
            }
        });

        InputId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                idCheck(new IsOverlapped(InputId.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    private void idCheck(IsOverlapped isOverlapped) {
        Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
        if (InputId.getText().toString().length() == 0) {
            noticeIdError.setText("아이디를 입력해주세요.");

            noticeIdError.setTextColor(Color.parseColor("#bc0000"));
            GotoPassword.setBackgroundResource(R.color.gray);
            GotoPassword.setEnabled(false);
        }else if(InputId.getText().toString().length() < 6) {
            noticeIdError.setText("아이디는 6자 이상으로 구성하여야 합니다");
            noticeIdError.setTextColor(Color.parseColor("#bc0000"));
            GotoPassword.setBackgroundResource(R.color.gray);
            GotoPassword.setEnabled(false);
        }else if(!ps.matcher(InputId.getText()).matches()){
            noticeIdError.setText("아이디는 영문,숫자조합만 가능합니다.");
            noticeIdError.setTextColor(Color.parseColor("#bc0000"));
            GotoPassword.setBackgroundResource(R.color.gray);
            GotoPassword.setEnabled(false);
        }else{
            final Call<Response<Data>> res = NetRetrofit.getInstance().getSignup().isoverlapped(isOverlapped);
            res.enqueue(new Callback<Response<Data>>() {
                @Override
                public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                    if (response.code() == 200) {
                        // 사용자가 입력한 id 를 저장합니다.
                        ID = InputId.getText().toString();
                        Integer Status = response.body().getStatus(); // Status 값
                        String Message = response.body().getMessage(); // Message 값
                        noticeIdError.setText("사용 가능한 아이디 입니다.");
                        noticeIdError.setTextColor(Color.parseColor("#0ec600"));
                        GotoPassword.setBackgroundResource(R.color.mainColor);
                        GotoPassword.setEnabled(true);

                    }else if(response.code() == 409){
                        noticeIdError.setText("중복한 아이디가 존재합니다.");
                        noticeIdError.setTextColor(Color.parseColor("#bc0000"));
                        GotoPassword.setBackgroundResource(R.color.gray);
                        GotoPassword.setEnabled(false);
                    }else{
                        Toast.makeText(IdActivity.this, "서버에서 오류가 발생했습니다.\n문제가 지속되면 관리자에게 문의하세요", Toast.LENGTH_SHORT).show();
                        GotoPassword.setEnabled(false);
                    }
                }

                @Override
                public void onFailure(Call<Response<Data>> call, Throwable t) {
                    Log.e("","네트워크 오류");
                    Toast.makeText(IdActivity.this, "네크워크 상태가 원할하지 않습니다.\n잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                    GotoPassword.setEnabled(false);

                }
            });
        }
    }

    public void toGoNext(View view){
        Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
        intent.putExtra("Name",getIntent().getStringExtra("Name"));
        intent.putExtra("Id",ID);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void toGoBack (View view) {
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

        @Override
        public void onBackPressed () {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
}
