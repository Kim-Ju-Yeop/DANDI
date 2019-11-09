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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.techtown.schooler.R;
import org.techtown.schooler.network.SHA512;
import org.w3c.dom.Text;

/**
 * @author 이영은
 */

public class CheckPasswordActivity extends AppCompatActivity {

    String password;
    EditText inputCheckPassword;
    TextView GotoEmail;
    TextView NoticeCheckPassword;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_password);
        password = getIntent().getStringExtra("Password");
        inputCheckPassword = (EditText)findViewById(R.id.InputCheckPassword);
        GotoEmail = (TextView)findViewById(R.id.next_email);
        NoticeCheckPassword = (TextView)findViewById(R.id.NoticeCheckPasswrod);

        GotoEmail.setEnabled(false);
        NoticeCheckPassword.setText("");
        GotoEmail.setBackgroundResource(R.color.gray);

        layout = (LinearLayout) findViewById(R.id.signUpCheckPasswordLayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(),0);
            }
        });

        inputCheckPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputCheckPassword.getText().toString().length() == 0){
                    GotoEmail.setEnabled(false);
                    NoticeCheckPassword.setText("비밀번호를 입력하세요");
                    NoticeCheckPassword.setTextColor(Color.parseColor("#F80000"));
                    GotoEmail.setBackgroundResource(R.color.gray);
                } else if(inputCheckPassword.getText().toString().equals(password)){
                    GotoEmail.setEnabled(true);
                    NoticeCheckPassword.setText("비밀번호가 일치합니다");
                    NoticeCheckPassword.setTextColor(Color.parseColor("#0ec600"));
                    GotoEmail.setBackgroundResource(R.color.mainColor);
                }else{
                    GotoEmail.setEnabled(false);
                    NoticeCheckPassword.setText("비밀번호가 일치하지 않습니다.");
                    NoticeCheckPassword.setTextColor(Color.parseColor("#F80000"));
                    GotoEmail.setBackgroundResource(R.color.gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        Intent intent = new Intent(getApplicationContext(), EmailActivity.class);
        intent.putExtra("Name",getIntent().getStringExtra("Name"));
        intent.putExtra("Id",getIntent().getStringExtra("Id"));
        intent.putExtra("Password", SHA512.getSHA512(password));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
