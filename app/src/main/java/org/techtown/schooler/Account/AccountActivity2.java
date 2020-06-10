package org.techtown.schooler.Account;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.techtown.schooler.MainActivity;
import org.techtown.schooler.Model.UserInfo;
import org.techtown.schooler.R;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class AccountActivity2 extends AppCompatActivity {

    SharedPreferences Login;
    UserInfo userData;

    // View
    ImageView profile;
    TextView user_name;
    TextView user_id;
    TextView user_phone;
    TextView user_email;
    TextView user_school;
    TextView user_class;
    Toolbar toolbar;

    String no_user_phone;

    // 조회할 사람의 new_user_id
    String new_user_id;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account2);

        Login = getSharedPreferences("Login", MODE_PRIVATE);//SharedPreferences 선언

        Intent intent = getIntent();
        new_user_id = intent.getExtras().getString("user_id", "");

        userProfile();

        // View
        profile = findViewById(R.id.profile);
        user_name = findViewById(R.id.user_name);
        user_id = findViewById(R.id.user_id);
        user_phone = findViewById(R.id.user_phone);
        user_email = findViewById(R.id.user_email);
        user_school = findViewById(R.id.user_school);
        user_class = findViewById(R.id.user_class);
        toolbar = findViewById(R.id.toolbar);

        // profile 즉 프로필 사진을 둥글게 만들어줍니다.
        profile.setBackground(new ShapeDrawable(new OvalShape()));
        profile.setClipToOutline(true);

        // Toolbar Setting
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_black_24dp2);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5f5f5")));
    }

    public void userProfile(){
        Call<Response<Data>> res = NetRetrofit.getInstance().getProfile().GetProfile(Login.getString("token",""),new_user_id);
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {

                if(response.code() == 200){

                    userData = response.body().getData().getUserInfo();

                    int isPublic = Integer.parseInt(userData.getIsPublic());

                    if(isPublic == 0){
                        user_email.setText("비공개");
                        user_phone.setText("비공개");

                        // View 텍스트 삽입
                        user_name.setText(userData.getUser_name());
                        user_id.setText("(" + userData.getUser_id() + ")");

                        user_school.setText(userData.getSchool().getSchool_name());

                        if(userData.getSchool_grade() == null){
                            user_class.setText("학반 정보 없음");
                        } else{
                            user_class.setText(userData.getSchool_grade() + "학년 " + userData.getSchool_class() + "반");
                        }

                        Glide.with(AccountActivity2.this).load(userData.getProfile_pic()).into(profile);
                    } else{

                        user_name.setText(userData.getUser_name());
                        user_id.setText("(" + userData.getUser_id() + ")");

                        if(userData.getUser_phone() == null){
                            user_phone.setText("전화번호 없음");
                        } else{
                            user_phone.setText(userData.getUser_phone());
                        }

                        user_email.setText(userData.getUser_email());

                        user_school.setText(userData.getSchool().getSchool_name());

                        if(userData.getSchool_grade() == null){
                            user_class.setText("학반 정보 없음");
                        } else{
                            user_class.setText(userData.getSchool_grade() + "학년 " + userData.getSchool_class() + "반");
                        }

                        Glide.with(AccountActivity2.this).load(userData.getProfile_pic()).into(profile);
                    }

                    Log.e("[status 200]",response.message());

                } else if(response.code() == 403){
                    Log.e("[status 403]",response.message());
                } else if(response.code() == 404){

                    Log.e("[status 404]",response.message());
                } else if(response.code() == 500){

                    Log.e("[status 500]",response.message());
                }

            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Toast.makeText(AccountActivity2.this, "서버 통신 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                Log.e("[서버 통신 X]","서버 통신 오류");
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
