package org.techtown.schooler.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.techtown.schooler.ChannelEvents.CreateChannelEvents;
import org.techtown.schooler.MainActivity;
import org.techtown.schooler.NavigationFragment.AccountFragment;
import org.techtown.schooler.NavigationFragment.MainFragment;
import org.techtown.schooler.R;
import org.techtown.schooler.RecyclerView_main.JoinedChannelAdapter;
import org.techtown.schooler.RecyclerView_main.NoScheduleAdapter;
import org.techtown.schooler.StartMemberActivity.LoginActivity;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class JoinedChannel extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    SharedPreferences Login;
    Toolbar toolbar;

    ArrayList<org.techtown.schooler.Model.JoinedChannel> joinedChannel = new ArrayList<>();

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(JoinedChannel.this);
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_channel);

        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.toolbar);

        // Toolbar Setting
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_black_24dp2);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5f5f5")));

        Login = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);//SharedPreferences 선언

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        showChannel();

        settingsSwipeRefreshLayout();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void showChannel(){

        Call<Response<Data>> res = NetRetrofit.getInstance().getChannel().GetChannel(Login.getString("token",""));
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {

                if(response.code() == 200){

                    joinedChannel = (ArrayList<org.techtown.schooler.Model.JoinedChannel>) response.body().getData().getJoinedChannel();

                    JoinedChannelAdapter myAdapter = new JoinedChannelAdapter(joinedChannel);
                    recyclerView.setAdapter(myAdapter);

                    recyclerView.setVisibility(View.VISIBLE);

                } else if(response.code() == 204){
                    Log.e("", "가입 채널이 존재하지 않습니다.");
                    Toast.makeText(JoinedChannel.this, "가입 채널이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.GONE);
                }
                else if(response.code() == 410){//만약 Status값이 400이면 check에 false를 주고, 로그인 엑티비티로 이동
                    SharedPreferences.Editor editor = Login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();

                    startActivity(new Intent(JoinedChannel.this,LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(JoinedChannel.this, "토큰이 만료되었습니다\n다시 로그인 해 주세요", Toast.LENGTH_SHORT).show();
                }else if(response.code() == 500){
                    Log.e("500", "가입 채널 조회 실패");
                }
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(JoinedChannel.this, "네크워크 상태가 원할하지 않습니다.\n잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void settingsSwipeRefreshLayout(){

        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {


        showChannel();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);// 딜레이를 준 후 시작
    }
}
