package org.techtown.schooler.Channels;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import org.techtown.schooler.Channels.ListAdapter.MemberListAdapter;
import org.techtown.schooler.Channels.ChannelHandling.EditChannelAcitivty;
import org.techtown.schooler.Channels.ChannelHandling.MemberAllowActivity;
import org.techtown.schooler.Channels.ChannelHandling.UploadChannelsThumbnail;
import org.techtown.schooler.Model.User;
import org.techtown.schooler.R;
import org.techtown.schooler.StartMemberActivity.LoginActivity;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author 이영은
 */

public class ChannelsInfo2 extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SharedPreferences login;
    private String channel_id;
    private String channel_name;
    private String channel_content;
    private String channel_create_user;
    private String channel_thumbnail;
    private String channel_Color;
    private String usr_id;
    private String channel_check;
    private List<User> dataList= new ArrayList<>();
    private int activityBrequestCode = 0;

    private TextView name;
    private TextView content;
    private ImageView thumbnail;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Menu menu;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels_info);
        settingsToolbar();
        settingsActivity();
        settingsSwipeRefreshLayout();
        settingsRecyclerView();
        channelInfoRoad(channel_id);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        content.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        channelInfoRoad(channel_id);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);// 딜레이를 준 후 시작

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //or switch
        if (id == R.id.FixChannel) {
            Intent intent = new Intent(this, EditChannelAcitivty.class);
            intent.putExtra("channel_name", channel_name);
            intent.putExtra("channel_color",channel_Color);
            intent.putExtra("channel_explain",channel_content);
            intent.putExtra("channel_check",channel_check);
            intent.putExtra("channel_id",channel_id);

            startActivityForResult(intent, activityBrequestCode);

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        if (id == R.id.UploadThumbnail) {
            Intent intent = new Intent(this, UploadChannelsThumbnail.class);
            intent.putExtra("channel_name", channel_name);
            intent.putExtra("create_user", channel_create_user);
            intent.putExtra("channel_color", channel_Color);
            intent.putExtra("channel_explain", channel_content);
            intent.putExtra("channel_isPublic", channel_check);
            intent.putExtra("channel_id", channel_id);
            intent.putExtra("channel_thumbnail", channel_thumbnail);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        if (id == R.id.LeaveChannel) {
            channelLeave();
        }

        if (id == R.id.deleteChannel) {
            channelDelete();
        }

        if (id == R.id.FixMember) {
            Intent intent = new Intent(this, MemberAllowActivity.class);
            intent.putExtra("channel_id", channel_id);
            startActivityForResult(intent, activityBrequestCode);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        if (id == R.id.JoinChannel){
            joinChannel();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == activityBrequestCode && resultCode == RESULT_OK){
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        checkingAdmin();
        this.menu = menu;
        return true;
    }//메뉴 설정

    private void settingsToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.channelFixTitle);
    }//toolbar 관련설정

    private void settingsSwipeRefreshLayout(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout3);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void settingsActivity(){
        login = getSharedPreferences("Login", MODE_PRIVATE);//SharedPreferences 선언
        channel_id = getIntent().getStringExtra("channel_id");
        name = (TextView) findViewById(R.id.title_TextView2);
        content = (TextView) findViewById(R.id.content_TextView2);
        thumbnail = (ImageView) findViewById(R.id.backgroundImage);
    }//엑티비티 설정

    private void settingsRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Member);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(ChannelsInfo2.this);
        recyclerView.setLayoutManager(LinearLayoutManager);
    }//리사이클러뷰 설정

    public void channelLeave(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warningMessage_1);
        builder.setMessage(R.string.channelLeaveMessage_1);
        builder.setIcon(Integer.parseInt(String.valueOf(R.drawable.ic_exit_to_app_black_24dp)));

        builder.setPositiveButton(R.string.yesMessage_1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                leaveChannel(channel_id);
            }
        });

        builder.setNegativeButton(R.string.noMessage_1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ChannelsInfo2.this, R.string.cancelMessage_1, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }//채널 나가기 다이얼로그

    private void joinChannel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChannelsInfo2.this);
        builder.setTitle("채널가입");
        builder.setMessage("채널에 가입하시겠습니까?\n\n채널이름 : " + channel_name + "\n개설자 : " + channel_create_user);
        builder.setIcon(Integer.parseInt(String.valueOf(R.drawable.ic_exit_to_app_black_24dp)));

        builder.setPositiveButton("예", (dialogInterface, i) -> {
            Call<Response<Data>> res1 = NetRetrofit.getInstance().getChannel().JoinChannel(login.getString("token", ""), channel_id);//token불러오기
            res1.enqueue(new Callback<Response<Data>>() {
                @Override
                public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                    if (response.code() == 200) {

                        if (channel_check.equals("0")) {
                            Toast.makeText(ChannelsInfo2.this, "채널가입 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChannelsInfo2.this, "채널가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        onBackPressed();
                    }else if(response.code() == 409){
                        Toast.makeText(ChannelsInfo2.this, "이미 가입된 채널입니다.", Toast.LENGTH_SHORT).show();
                    }else if(response.code() == 410){
                        SharedPreferences.Editor editor = login.edit();
                        editor.putString("token",null);
                        editor.putString("id",null);
                        editor.commit();
                        startActivity(new Intent(ChannelsInfo2.this, LoginActivity.class));
                        Log.e("","토큰 만료");
                        Toast.makeText(ChannelsInfo2.this, "토큰이 만료되었습니다\n다시 로그인 해 주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.e("","오류 발생");
                        Toast.makeText(ChannelsInfo2.this, "서버에서 오류가 발생했습니다.\n문제가 지속되면 관리자에게 문의하세요", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<Data>> call, Throwable t) {
                    Log.e("","네트워크 오류");
                    Toast.makeText(ChannelsInfo2.this, "네크워크 상태가 원할하지 않습니다.\n잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("아니오", (dialogInterface, i) -> Log.e("아니오 버튼", "클릭됐어요"));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }//채널 가입 다이얼로그

    public void channelDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warningMessage_1);
        builder.setMessage(R.string.channelDeleteMessage_1);
        builder.setIcon(Integer.parseInt(String.valueOf(R.drawable.ic_exit_to_app_black_24dp)));

        builder.setPositiveButton(R.string.yesMessage_1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteChannel(channel_id);
            }
        });

        builder.setNegativeButton(R.string.noMessage_1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ChannelsInfo2.this, R.string.cancelMessage_1, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }//채널 삭제 다이얼로그

    private void channelInfoRoad(String input){
        Call<Response<Data>> res = NetRetrofit.getInstance().getChannel().ChannelInfo(login.getString("token",""),input);
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if(response.code() == 200) {
                    channel_name = response.body().getData().getChannelInfo().getName();

                    channel_content = response.body().getData().getChannelInfo().getExplain();
                    channel_create_user = response.body().getData().getChannelInfo().getCreate_user();
                    channel_thumbnail = response.body().getData().getChannelInfo().getThumbnail();
                    channel_Color = response.body().getData().getChannelInfo().getColor();
                    channel_check = response.body().getData().getChannelInfo().getIsPublic();

                    name.setText(channel_name);
                    name.setMaxLines(1);
                    name.setEllipsize(TextUtils.TruncateAt.END);
                    getSupportActionBar().setTitle(channel_name);

                    content.setText(channel_content);
                    content.setMaxLines(3);
                    content.setEllipsize(TextUtils.TruncateAt.END);

                    Glide.with(ChannelsInfo2.this).load(channel_thumbnail).into(thumbnail);

                    dataList = response.body().getData().getChannelInfo().getUsers();
                    MemberListAdapter adapter = new MemberListAdapter(dataList);
                    recyclerView.setAdapter(adapter);
                    adapter.CatchChannelId(channel_id,channel_create_user);

                }else if(response.code() == 404){
                    Toast.makeText(ChannelsInfo2.this, "존재하지 않는 채널입니다.", Toast.LENGTH_SHORT).show();
                    onBackPressed();

                }else if(response.code() == 410){
                    SharedPreferences.Editor editor = login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();
                    startActivity(new Intent(ChannelsInfo2.this, LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(ChannelsInfo2.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("","오류 발생");
                    Toast.makeText(ChannelsInfo2.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(ChannelsInfo2.this, R.string.serverErrorMessage_1, Toast.LENGTH_SHORT).show();
            }
        });
    }//채널정보 로딩 (서버통신)

    private void checkingAdmin(){
        Call<Response<Data>> res2 = NetRetrofit.getInstance().getProfile().GetProfile(login.getString("token",""),"");
        res2.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if (response.code() == 200) {
                    usr_id = response.body().getData().getUserInfo().getUser_id();;
                    if (channel_create_user != null&&channel_create_user.equals(usr_id))
                        getMenuInflater().inflate(R.menu.channels_admin_page_menu, menu);
                    else if (getIntent().getStringExtra("userStatus").equals("2"))
                        getMenuInflater().inflate(R.menu.channel_user_page_menu, menu);
                    else if (getIntent().getStringExtra("userStatus").equals("0"))
                        getMenuInflater().inflate(R.menu.join_channel, menu);

                }else if(response.code() == 410){
                    SharedPreferences.Editor editor = login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();
                    startActivity(new Intent(ChannelsInfo2.this, LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(ChannelsInfo2.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("","오류 발생");
                    Toast.makeText(ChannelsInfo2.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(ChannelsInfo2.this, R.string.serverErrorMessage_1, Toast.LENGTH_SHORT).show();
            }
        });
    }//유저정보 확인 -> menu 옵션 다르게 설정(서버통신)

    private void deleteChannel(String input){
        Call<Response<Data>> res4 = NetRetrofit.getInstance().getChannel().DeleteChannel(login.getString("token",""),input);
        res4.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if(response.code() == 200){
                    Toast.makeText(ChannelsInfo2.this, R.string.deleteMessage_1, Toast.LENGTH_SHORT).show();
//                    ChannelFragment channelFragment = new ChannelFragment();
//                    channelFragment.onSearch();
                    onBackPressed();
                }else if(response.code() == 403){
                    Toast.makeText(ChannelsInfo2.this, R.string.permission_3, Toast.LENGTH_SHORT).show();
                }else if(response.code() == 404){
                    Toast.makeText(ChannelsInfo2.this, R.string.channelMessage_1, Toast.LENGTH_SHORT).show();
                }else if(response.code() == 410){
                    SharedPreferences.Editor editor = login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();
                    startActivity(new Intent(ChannelsInfo2.this, LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(ChannelsInfo2.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("","오류 발생");
                    Toast.makeText(ChannelsInfo2.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(ChannelsInfo2.this, R.string.serverErrorMessage_1, Toast.LENGTH_SHORT).show();
            }
        });
    }//채널 삭제(서버통신)

    private void leaveChannel(String input){
        Call<Response<Data>> res3 = NetRetrofit.getInstance().getChannel().LeaveChannel(login.getString("token",""),input);
        res3.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if(response.code() == 200){
                    Toast.makeText(ChannelsInfo2.this, R.string.leaveMessage_1, Toast.LENGTH_SHORT).show();
//                    ChannelFragment channelFragment = new ChannelFragment();
//                    channelFragment.onSearch();
                    onBackPressed();
                }else if(response.code() == 403){
                    Toast.makeText(ChannelsInfo2.this, R.string.permission_3, Toast.LENGTH_SHORT).show();
                }else if(response.code() == 404){
                    Toast.makeText(ChannelsInfo2.this, R.string.channelMessage_1, Toast.LENGTH_SHORT).show();
                }else if(response.code() == 410){
                    SharedPreferences.Editor editor = login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();
                    startActivity(new Intent(ChannelsInfo2.this, LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(ChannelsInfo2.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("","오류 발생");
                    Toast.makeText(ChannelsInfo2.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(ChannelsInfo2.this, R.string.serverErrorMessage_1, Toast.LENGTH_SHORT).show();
            }
        });
    }//채널 나가기(서버통신)

    @Override
    public void onBackPressed () {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }//뒤로가기 버튼을 눌렀을 때

}
