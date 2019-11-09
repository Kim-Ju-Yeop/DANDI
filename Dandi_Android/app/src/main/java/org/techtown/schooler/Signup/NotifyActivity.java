package org.techtown.schooler.Signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.techtown.schooler.ChannelEvents.CreateChannelEvents;
import org.techtown.schooler.Model.User;
import org.techtown.schooler.R;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author 이영은
 */

public class NotifyActivity extends AppCompatActivity {

    String Name;
    String Id;
    String Pw;
    String Email;
    String Phone;
    String SchoolId;
    String Grade;
    String Class;

    TextView ClearName;
    TextView ClearId;
    TextView ClearEmail;
    TextView ClearPhone;
    TextView ClearSchool;
    TextView ClearGrade;
    TextView ClearClass;
    TextView Finish;
    TextView noticeProfile;

    Switch isPublic;
    Switch isNotify;



    public static User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        ClearName =(TextView)findViewById(R.id.ClearName);
        ClearId =(TextView)findViewById(R.id.ClearId);
        ClearEmail =(TextView)findViewById(R.id.ClearEmail);
        ClearPhone = (TextView)findViewById(R.id.ClearPhoneNumber);
        ClearSchool = (TextView)findViewById(R.id.ClearSchool);
        ClearGrade = (TextView)findViewById(R.id.ClearGrade);
        ClearClass = (TextView)findViewById(R.id.ClearClass);
        isPublic = (Switch)findViewById(R.id.isPublic);
        isNotify = (Switch)findViewById(R.id.isNotify);
        Finish = (TextView)findViewById(R.id.Finish);
        noticeProfile = (TextView)findViewById(R.id.noticeProfile);

        user.setPushNotify("false");
        isPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    user.setPushNotify("true");
                    noticeProfile.setVisibility(View.VISIBLE);
                }else{
                    user.setPushNotify("false");
                    noticeProfile.setVisibility(View.INVISIBLE);
                }
            }
        });

        user.setIsPublic("false");
        isNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    user.setIsPublic("true");
                }else{
                    user.setIsPublic("false");
                }
            }
        });

        Name = getIntent().getStringExtra("Name");
        Id = getIntent().getStringExtra("Id");
        Pw = getIntent().getStringExtra("Password");
        Email = getIntent().getStringExtra("Email");
        Phone = getIntent().getStringExtra("PhoneNumber");
        SchoolId = getIntent().getStringExtra("SchoolId");
        Grade = getIntent().getStringExtra("Grade");
        Class = getIntent().getStringExtra("Class");

        user.setUser_name(Name);
        user.setUser_id(Id);
        user.setUser_pw(Pw);
        user.setUser_email(Email);
        user.setSchool(SchoolId);

        ClearName.setText(Name);
        ClearId.setText(Id);
        ClearEmail.setText(Email);
        if(Phone.equals("null")){
            ClearPhone.setText("없음");
            ClearPhone.setTextColor(Color.parseColor("#D10000"));
            user.setUser_phone(null);
        }else{
            ClearPhone.setText(Phone);
            user.setUser_phone(Phone);
        }
        ClearSchool.setText(getIntent().getStringExtra("SchoolName"));


        if(Grade.equals("null")&&Class.equals("null")){
            ClearGrade.setText("학년정보가 없습니다.");
            ClearClass.setText("학반정보가 없습니다.");
            user.setSchool_grade(null);
            user.setSchool_class(null);
            ClearGrade.setTextColor(Color.parseColor("#D10000"));
            ClearClass.setTextColor(Color.parseColor("#D10000"));
        }else{
            ClearGrade.setText(Grade+"학년");
            ClearClass.setText(Class+"반");
            user.setSchool_grade(Grade);
            user.setSchool_class(Class);
        }


        Finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                signup(user);
            }
        });

    }

    public void signup(User user) {
        final Call<Response<Data>> res = NetRetrofit.getInstance().getSignup().signupPost(user);
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if (response.code() == 200) {
                    Integer Status = response.body().getStatus();
                    String Message = response.body().getMessage();
                    Log.d("[SignUp] Status", Status + ":" + Message);

                    Intent intent = new Intent(NotifyActivity.this, FinishSignup.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else{
                    Toast.makeText(NotifyActivity.this, "서버에서 오류가 발생했습니다.\n문제가 지속되면 관리자에게 문의하세요", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(NotifyActivity.this, "네크워크 상태가 원할하지 않습니다.\n잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        });

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
