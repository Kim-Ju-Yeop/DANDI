package org.techtown.schooler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.techtown.schooler.Account.AccountActivity;
import org.techtown.schooler.Channels.ChannelActivity;
import org.techtown.schooler.NavigationFragment.MainFragment;
import org.techtown.schooler.StartMemberActivity.LoginActivity;

import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;


import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // View
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ImageView profile;
    TextView userName;
    TextView school;
    TextView grade;
    Toolbar toolbar;
    View main_nav_header;

    // SharedPreferences
    SharedPreferences Login;

    // Fragment
    MainFragment main = new MainFragment();

    Boolean profile_check = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        main_nav_header = (View) navigationView.getHeaderView(0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        profile = main_nav_header.findViewById(R.id.profile);
        userName = main_nav_header.findViewById(R.id.userName);
        school = main_nav_header.findViewById(R.id.school);
        grade = main_nav_header.findViewById(R.id.grade);

        // SharedPreferences
        Login = getSharedPreferences("Login", MODE_PRIVATE);

        // Token Available
        if(Login.getString("token",null) == null){
            Intent StatLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(StatLogin);
            Log.e("[LoginCheck]", "로그인 X");
        }else{
            Log.e("[LoginCheck] ", "로그인 O");
        }

        // Profile Change
        try{
            Intent intent = getIntent();
            profile_check = intent.getExtras().getBoolean("profile",false);
        } catch (NullPointerException e){

        }
        if(profile_check == true){
            getSupportFragmentManager().beginTransaction().replace(R.id.LinearLayout, main).commit();
            drawerLayout.openDrawer(navigationView);
        } else{
            getSupportFragmentManager().beginTransaction().replace(R.id.LinearLayout, main).commit();
        }

        // Toolbar Setting
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp2);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5f5f5")));

        // NavigationView Click
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        // Profile Round
        profile.setBackground(new ShapeDrawable(new OvalShape()));
        profile.setClipToOutline(true);

        userInformation();
    }

    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }
        else {
            ActivityCompat.finishAffinity(this);
        }
    }

    // NavigationItemSelected Event
    public boolean onNavigationItemSelected(MenuItem item){

        Fragment main = new MainFragment();

        switch (item.getItemId()){

            case R.id.channel:

                Intent channel_intent = new Intent(MainActivity.this, ChannelActivity.class);
                startActivity(channel_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.main:

                getSupportFragmentManager().beginTransaction().replace(R.id.LinearLayout, main).commit();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.account:

                Intent account_intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(account_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.logout:
                LogoutMessage();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    public void LogoutMessage(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("로그아웃 하시겠습니까?");
        builder.setIcon(Integer.parseInt(String.valueOf(R.drawable.ic_exit_to_app_black_24dp)));

        drawerLayout.closeDrawer(GravityCompat.START);

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);


                SharedPreferences.Editor editor = Login.edit();

                editor.putString("token",null);
                editor.putString("id",null);

                editor.commit();

                startActivity(intent);
                overridePendingTransition(R.anim.loadfadein, R.anim.loadfadeout);
                Toast.makeText(MainActivity.this, "로그아웃을 정상적으로 수행하였습니다.", Toast.LENGTH_SHORT).show();

                // NavigationView 중 아이템을 선택 시 drawerLayout 이 종료됩니다.
                drawerLayout.closeDrawer(GravityCompat.START);

            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(MainActivity.this, "로그아웃을 취소하였습니다.", Toast.LENGTH_SHORT).show();

                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                drawerLayout.openDrawer(navigationView);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void userInformation(){

        Call<Response<Data>> res = NetRetrofit.getInstance().getProfile().GetProfile(Login.getString("token",""),"");
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {

                if(response.code() == 200)
                {
                    String name = response.body().getData().getUserInfo().getUser_name();
                    String schoolName = response.body().getData().getUserInfo().getSchool().getSchool_name();
                    String school_grade = response.body().getData().getUserInfo().getSchool_grade();
                    String school_class = response.body().getData().getUserInfo().getSchool_class();
                    String user_profile = response.body().getData().getUserInfo().getProfile_pic();

                    userName.setText(name);
                    school.setText(schoolName);

                    if(school_grade == null){
                        grade.setText("학반 정보 없음");
                    } else{
                        grade.setText(school_grade + "학년 " + school_class + "반");
                    }

                    Glide.with(MainActivity.this).load(user_profile).into(profile);


                } else if(response.code() == 410){

                    SharedPreferences.Editor editor = Login.edit();
                    editor.putString("token",null);
                    editor.commit();

                    Log.e("[status 410]","토큰 만료");
                    Toast.makeText(MainActivity.this, "토큰 만료, 로그인 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else if(response.code() == 500){

                    Log.e("[status 500]", "프로필 조회에 실패하였습니다.");
                    Toast.makeText(MainActivity.this, "프로필 조회에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {

                Toast.makeText(MainActivity.this, "서버 통신 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

