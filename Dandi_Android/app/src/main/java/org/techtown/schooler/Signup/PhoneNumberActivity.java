package org.techtown.schooler.Signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.techtown.schooler.R;

/**
 * @author 이영은
 */

public class PhoneNumberActivity extends AppCompatActivity {

    String PhoneNumber;
    EditText InputPhoneNumber;
    TextView noticePhoneNumberError;
    TextView GotoSchool;
    LinearLayout layout;
    CheckBox noPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        InputPhoneNumber = (EditText)findViewById(R.id.InputPhoneNumber);
        noticePhoneNumberError = (TextView)findViewById(R.id.noticePhoneNumberError);
        GotoSchool = (TextView)findViewById(R.id.next_school);
        noticePhoneNumberError.setText("");

        noPhone = (CheckBox)findViewById(R.id.noPhone);

        GotoSchool.setEnabled(false);
        GotoSchool.setBackgroundResource(R.color.gray);

        layout = (LinearLayout) findViewById(R.id.signUpPhoneNumberLayout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(),0);
            }
        });
        noPhone.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    GotoSchool.setEnabled(true);
                    GotoSchool.setBackgroundResource(R.color.mainColor);
                    InputPhoneNumber.setEnabled(false);
                    PhoneNumber = null;
                } else {
                    GotoSchool.setEnabled(false);
                    GotoSchool.setBackgroundResource(R.color.gray);
                    InputPhoneNumber.setEnabled(true);
                }
            }
        }) ;

        InputPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        InputPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력전
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력중
                if(!android.util.Patterns.PHONE.matcher(InputPhoneNumber.getText().toString()).matches()){
                        GotoSchool.setEnabled(false);
                        GotoSchool.setBackgroundResource(R.color.gray);

                        noticePhoneNumberError.setTextColor(Color.parseColor("#F80000"));
                        noticePhoneNumberError.setText("올바른 형식의 전화번호를 입력하세요");
                }else{
                    GotoSchool.setEnabled(true);
                    GotoSchool.setBackgroundResource(R.color.mainColor);

                    noticePhoneNumberError.setTextColor(Color.parseColor("#0078F8"));
                    noticePhoneNumberError.setText("올바른 형식의 전화번호가 입력되었습니다");
                    PhoneNumber = InputPhoneNumber.getText().toString();
                    Log.e("",PhoneNumber+"");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                //입력후

            }
        });
    }

    public void toGoNext(View view){
        Intent intent = new Intent(getApplicationContext(), SchoolActivity.class);
        intent.putExtra("Name",getIntent().getStringExtra("Name"));
        intent.putExtra("Id",getIntent().getStringExtra("Id"));
        intent.putExtra("Password",getIntent().getStringExtra("Password"));
        intent.putExtra("Email",getIntent().getStringExtra("Email"));
        intent.putExtra("PhoneNumber",PhoneNumber+"");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void toGoBack(View view){
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
