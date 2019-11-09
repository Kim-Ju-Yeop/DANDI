package org.techtown.schooler.Channels.ChannelHandling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.techtown.schooler.Channels.ListAdapter.AwaitUserAdapter;
import org.techtown.schooler.Model.UserInfo;
import org.techtown.schooler.R;
import org.techtown.schooler.StartMemberActivity.LoginActivity;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.GONE;

/**
 * @author 이영은
 */

public class MemberAllowActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerView;
    List<UserInfo> DataList = new ArrayList<>();
    SwipeRefreshLayout mSwipeRefreshLayout;
    SharedPreferences Login;
    EditText search;
    String keyword = "";
    String Channel_id;
    Toolbar toolbar;
    LinearLayout NoAwaitUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_allow);
        settingsToolbar();
        settingsActivity();
        settingsSwipeRefreshLayout();
        settingsRecyclerView();
        Road();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void settingsToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("채널가입 승인하기");

    }

    private void settingsSwipeRefreshLayout(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout2);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void settingsActivity(){
        search = (EditText) findViewById(R.id.searchMember);
        recyclerView = (RecyclerView) findViewById(R.id.AwaitRecyclerView);
        Login = getSharedPreferences("Login", MODE_PRIVATE);//SharedPreferences 선언
        Channel_id = getIntent().getStringExtra("channel_id");
        NoAwaitUser = (LinearLayout) findViewById(R.id.NoAwaitUserMessage);
    }

    private void settingsRecyclerView(){
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(LinearLayoutManager);
    }

    @Override
    public void onRefresh() {
        Road();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);// 딜레이를 준 후 시작

    }

    public void Road(){
        Call<Response<Data>> res = NetRetrofit.getInstance().getChannel().ShowAwaitUser(Login.getString("token",""),Channel_id);
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if(response.code() == 200){
                    recyclerView.setVisibility(View.VISIBLE);
                    NoAwaitUser.setVisibility(GONE);
                    DataList = response.body().getData().getAwaitUsers();
                    AwaitUserAdapter adapter = new AwaitUserAdapter(DataList);
                    recyclerView.setAdapter(adapter);
                    adapter.filter(keyword);
                    adapter.CatchChannelId(Channel_id);
                    search.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //에딧텍스트 만들고, 그 값을 필터에 매개변수로 넣는 작업.
                            keyword = charSequence.toString();
                            adapter.filter(keyword);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            //입력후
                        }
                    });
                }else if(response.code() == 204){
                    recyclerView.setVisibility(View.GONE);
                    NoAwaitUser.setVisibility(View.VISIBLE);
                }else if(response.code() == 403) {
                    Toast.makeText(MemberAllowActivity.this, R.string.permission_3, Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else if(response.code() == 410){
                    SharedPreferences.Editor editor = Login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();
                    startActivity(new Intent(MemberAllowActivity.this, LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(MemberAllowActivity.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                } else{
                    Log.e("","오류 발생");
                    Toast.makeText(MemberAllowActivity.this, R.string.serverErrorMessage_1, Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(MemberAllowActivity.this, R.string.networkErrorMessage_1, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed () {
        setResult(RESULT_OK);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}