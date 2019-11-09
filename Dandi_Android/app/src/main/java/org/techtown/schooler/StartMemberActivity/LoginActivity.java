package org.techtown.schooler.StartMemberActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.schooler.MainActivity;
import org.techtown.schooler.R;
import org.techtown.schooler.network.SHA512;
import org.techtown.schooler.Signup.SignupOneActivity;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.Model.LoginPostRequest;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity{

    // View
    EditText Id_EditText;
    EditText Pw_EditText;
    Button login_button;
    TextView join_textView;
    LinearLayout layout;
    CheckBox check_id;

    // SharedPreferences
    SharedPreferences Login;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // View
        Id_EditText = (EditText) findViewById(R.id.Id_EditText);
        Pw_EditText = (EditText) findViewById(R.id.Pw_EditText);
        login_button = (Button) findViewById(R.id.login_button);
        join_textView = (TextView) findViewById(R.id.join_TextView);
        layout = (LinearLayout) findViewById(R.id.LinearLayout);
        check_id = (CheckBox) findViewById(R.id.check_Id);

        // SharedPreferences
        SharedPreferences sf = getSharedPreferences("check_click", MODE_PRIVATE);

        int Number = sf.getInt("Number", 0);
        String Id = sf.getString("Id","");

        // Number == 1 경우
        if(Number == 1){
            Id_EditText.setText(Id);
            check_id.setChecked(true);
        }

        // KeyBoard Check
        Pw_EditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Pw_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    login(new LoginPostRequest(Id_EditText.getText().toString(), SHA512.getSHA512(Pw_EditText.getText().toString())));
                }
                return false;
            }
        });

        // CheckBox Check
        check_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {

                // Id 저장 클릭 (O)
                if(check == true){

                    SharedPreferences sharedPreferences = getSharedPreferences("check_click", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("Id", Id_EditText.getText().toString());
                    editor.putInt("Number", 1);
                    editor.commit();
                }

                // Id 저장 클릭 (X)
                else if(check == false){

                    SharedPreferences sharedPreferences = getSharedPreferences("check_click", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // editor 값을 모두 초기화합니다.
                    editor.clear();
                    editor.commit();
                }
            }
        });
    }


    // login 매서드
    private void login(LoginPostRequest loginPostRequest) {

        int id_length = Id_EditText.getText().toString().length();
        int pw_length = Pw_EditText.getText().toString().length();

        if(id_length == 0 || pw_length == 0){
            Toast.makeText(LoginActivity.this, "아이디 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
        }else{
            final Call<Response<Data>> res = NetRetrofit.getInstance().getLogin().loginPost(loginPostRequest);
            res.enqueue(new Callback<Response<Data>>() {
                @Override
                public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                    if (response.code() == 200) {
                        Login = getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = Login.edit();

                        editor.putString("token",response.body().getData().getToken());
                        editor.putString("id",Id_EditText.getText().toString());
                        editor.commit();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.loadfadein, R.anim.loadfadeout);

                    }else if(response.code()==403){
                        Toast.makeText(LoginActivity.this, "승인 대기중인 유저입니다.", Toast.LENGTH_SHORT).show();
                    }else if(response.code() == 401){
                        Toast.makeText(LoginActivity.this, "일치하는 회원정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "서버에서 오류가 발생했습니다.\n문제가 지속되면 관리자에게 문의하세요", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<Data>> call, Throwable t) {
                    Log.e("","네트워크 오류");
                    Toast.makeText(LoginActivity.this, "네크워크 상태가 원할하지 않습니다.\n잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onBackPressed(){
        ActivityCompat.finishAffinity(this);
    }

    // Login
    public void login_onclick(View view){
        login(new LoginPostRequest(Id_EditText.getText().toString(), SHA512.getSHA512(Pw_EditText.getText().toString())));
    }

    // Layout
    public void layout_onclick(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(Id_EditText.getWindowToken(), 0);
    }

    // Join
    public void join_onclick(View view){
        Intent StartSignup = new Intent(LoginActivity.this, SignupOneActivity.class);
        startActivity(StartSignup);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}

