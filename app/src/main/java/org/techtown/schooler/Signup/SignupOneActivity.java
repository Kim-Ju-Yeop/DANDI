package org.techtown.schooler.Signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.schooler.MainActivity;
import org.techtown.schooler.R;
import org.techtown.schooler.StartMemberActivity.LoginActivity;
import org.w3c.dom.Text;

import java.util.regex.Pattern;

/**
 * @author 이영은
 */

public class SignupOneActivity extends AppCompatActivity {

    EditText InputName;
    TextView NoticeName;
    TextView NextButton;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_one);

        InputName = (EditText)findViewById(R.id.InputName);
        NoticeName = (TextView)findViewById(R.id.NoticeNameError);
        NextButton = (TextView)findViewById(R.id.next_id);
        NoticeName.setVisibility(View.INVISIBLE);

        layout = (LinearLayout) findViewById(R.id.signUpNameLayout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(),0);
            }
        });

        NextButton.setEnabled(false);
        NextButton.setBackgroundResource(R.color.gray);

        InputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Pattern ps = Pattern.compile("^[ㄱ-ㅎ가-힣a-zA-z]*$");
                if(!ps.matcher(InputName.getText()).matches()){
                    NoticeName.setText("이름에는 한글 및 영문만 입력 가능합니다.");
                    NoticeName.setTextColor(Color.parseColor("#F80000"));
                    NoticeName.setVisibility(View.VISIBLE);//해당 뷰를 보여줌
                    NextButton.setEnabled(false);
                    NextButton.setBackgroundResource(R.color.gray);
                }else if(InputName.getText().toString().length() == 0){
                    NoticeName.setText("이름을 입력해주세요.");
                    NoticeName.setTextColor(Color.parseColor("#F80000"));
                    NoticeName.setVisibility(View.VISIBLE);//해당 뷰를 보여줌
                    NextButton.setEnabled(false);
                    NextButton.setBackgroundResource(R.color.gray);
                }
                else{
                    NoticeName.setText("올바른 형식의 이름이 입력되었습니다.");
                    NoticeName.setTextColor(Color.parseColor("#0078F8"));
                    NoticeName.setVisibility(View.VISIBLE);
                    NextButton.setEnabled(true);
                    NextButton.setBackgroundResource(R.color.mainColor);
                }
                //입력하는 중
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력후
            }
        });


    }

    public void toGoNext(View view){
        Intent intent = new Intent(getApplicationContext(), IdActivity.class);
        intent.putExtra("Name",InputName.getText().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void toGoBack(View view){

        Intent intent = new Intent(SignupOneActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SignupOneActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
