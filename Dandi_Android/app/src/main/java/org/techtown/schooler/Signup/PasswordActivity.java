package org.techtown.schooler.Signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.techtown.schooler.R;
import org.techtown.schooler.network.SHA512;

import java.util.regex.Pattern;

/**
 * @author 이영은
 */

public class PasswordActivity extends AppCompatActivity {


    EditText InputPassword;
    String password;
    TextView NoticePassword;
    TextView GotoCheckPassword;
    LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        InputPassword = (EditText)findViewById(R.id.InputPassword);
        GotoCheckPassword = (TextView)findViewById(R.id.next_PasswordCheck);
        NoticePassword = (TextView)findViewById(R.id.NoticePasswrod);

        GotoCheckPassword.setEnabled(false);
        GotoCheckPassword.setBackgroundResource(R.color.gray);

        layout = (LinearLayout) findViewById(R.id.signUpPasswordLayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(),0);
            }
        });

        NoticePassword.setText("");

        InputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력 전
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Pattern ps = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$");
                if(ps.matcher(InputPassword.getText()).matches()){
                    NoticePassword.setText("사용할 수 있는 비밀번호 입니다.");
                    NoticePassword.setTextColor(Color.parseColor("#0ec600"));
                    GotoCheckPassword.setEnabled(true);
                    GotoCheckPassword.setBackgroundResource(R.color.mainColor);
                    password = InputPassword.getText().toString();
                }else{
                    NoticePassword.setText("비밀번호는 문자, 숫자를 포함하여 8자 이상으로 구성하여야 합니다");
                    NoticePassword.setTextColor(Color.parseColor("#F80000"));
                    NoticePassword.setVisibility(View.VISIBLE);//해당 뷰를 보여줌
                    GotoCheckPassword.setEnabled(false);
                    GotoCheckPassword.setBackgroundResource(R.color.gray);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력후
            }
        });
    }

    public void toGoBack (View view) {
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void toGoNext(View view){
        Intent intent = new Intent(getApplicationContext(), CheckPasswordActivity.class);
        intent.putExtra("Name",getIntent().getStringExtra("Name"));
        intent.putExtra("Id",getIntent().getStringExtra("Id"));
        intent.putExtra("Password",password);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
