package org.techtown.schooler.Channels;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.techtown.schooler.Channels.CreateChannel.CreateChannel;
import org.techtown.schooler.Channels.ListAdapter.ChannelListAdapter;
import org.techtown.schooler.Model.ChannelInfo;
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

public class ChannelActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    static RecyclerView recyclerView;
    static List<ChannelInfo> DataList= new ArrayList<>();
    SwipeRefreshLayout mSwipeRefreshLayout;
    static SharedPreferences Login;
    static EditText search;
    static Spinner field;
    String user_id;
    static LinearLayout NoChannelMessage;
    LinearLayout createChannel;
    private Toolbar toolbar;
    private Menu menu;
    ChannelsInfo channelsInfo = new ChannelsInfo();

    static String keyword ="";
    static Integer pick = null;


    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.createChannel)
            makeChannel();
        return super.onOptionsItemSelected(item);
    }

    private void makeChannel(){
        startActivity((new Intent(ChannelActivity.this, CreateChannel.class)));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onRefresh() {
        onSearch();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);// 딜레이를 준 후 시작

    }

    public void onSearch(){
        user_id = Login.getString("id", "");
        final Call<Response<Data>> res = NetRetrofit.getInstance().getChannel().SearchChannel(Login.getString("token",""),"");//token불러오기
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                Log.d("Retrofit", response.toString());
                if(response.code() == 200) {//만약 스테터스 값이 200이면
                    if (response.body().getData().getChannels().size() != 0) {

                        recyclerView.setVisibility(View.VISIBLE);
                        NoChannelMessage.setVisibility(GONE);

                        DataList = response.body().getData().getChannels();
                        ChannelListAdapter adapter = new ChannelListAdapter(DataList);
                        recyclerView.setAdapter(adapter);
                        adapter.filter(keyword, pick, user_id);

                        search.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                //에딧텍스트 만들고, 그 값을 필터에 매개변수로 넣는 작업.
                                keyword = charSequence.toString();
                                adapter.filter(keyword, pick, user_id);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                //입력후
                            }
                        });

                        field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {

                                if (position == 0) {
                                    pick = null;//전체
                                } else if (position == 1) {
                                    pick = 2;//가입한채널
                                } else if (position == 2) {
                                    pick = 3;//내가만든채널
                                } else if (position == 3) {
                                    pick = 0;//가입안된 채널
                                } else if (position == 4) {
                                    pick = 1;//승인대기중인 채널
                                }

                                adapter.filter(keyword, pick, user_id);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                }else if(response.code() == 204){
                    recyclerView.setVisibility(View.GONE);
                    NoChannelMessage.setVisibility(View.VISIBLE);
                } else if(response.code() == 410){
                    SharedPreferences.Editor editor = Login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();

                    startActivity(new Intent(ChannelActivity.this, LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(ChannelActivity.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(ChannelActivity.this, R.string.serverErrorMessage_1, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(ChannelActivity.this, R.string.networkErrorMessage_1, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        this.menu = menu;
        return true;
    }//메뉴 설정

    private void settingsToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.channelListTitle);
    }//toolbar 관련설정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        settingsToolbar();

        recyclerView = findViewById(R.id.ChannelRecyclerView);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(ChannelActivity.this);
        recyclerView.setLayoutManager(LinearLayoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        search = (EditText)findViewById(R.id.searchChannel);
        field = (Spinner)findViewById(R.id.channelField);
        NoChannelMessage = (LinearLayout)findViewById(R.id.NoChannelMessage);
        createChannel = (LinearLayout)findViewById(R.id.createChannel);
        pick = null;
        keyword ="";


        mSwipeRefreshLayout.setOnRefreshListener(this);
        

        Login = getSharedPreferences("Login", MODE_PRIVATE);//SharedPreferences 선언

        createChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeChannel();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        onRefresh();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
