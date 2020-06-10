package org.techtown.schooler.ChannelEvents;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.techtown.schooler.Channels.ChannelsInfo;
import org.techtown.schooler.Channels.ChannelsInfo2;
import org.techtown.schooler.MainActivity;
import org.techtown.schooler.R;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;

import retrofit2.Call;
import retrofit2.Callback;

public class ChannelContent extends AppCompatActivity {

    // SharedPreferences 선언
    SharedPreferences Login;

    // 부가 데이터
    String start_date;
    String end_date;
    String user_name;
    String user_id;
    String title;
    String content;
    String channel_name;
    String channel_color;
    String channel_image;
    String event_id;
    String channel_id;

    // XML View
    TextView event_title;
    TextView name;
    TextView event_content;
    TextView event_channel_name;
    ImageButton delete;
    ImageButton edit;
    TextView move_channel;
    TextView event_date;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_content2);

        Login = this.getSharedPreferences("Login", MODE_PRIVATE); //SharedPreferences 선언

        // 부가 데이터 저장
        Intent intent = getIntent();

        // 각각의 변수에 부가 데이터를 저장합니다.
        start_date = intent.getExtras().getString("start_date");
        end_date = intent.getExtras().getString("end_date");
        user_name = intent.getExtras().getString("user_name");
        user_id = intent.getExtras().getString("user_id");
        title = intent.getExtras().getString("title");
        content = intent.getExtras().getString("content");
        channel_name = intent.getExtras().getString("channel_name");
        channel_color = intent.getExtras().getString("channel_color");
        channel_image = intent.getExtras().getString("channel_image");
        event_id = intent.getExtras().getString("event_id");
        channel_id = intent.getExtras().getString("channel_id");

        // XML View 를 초기화합니다.
        // profile = findViewById(R.id.profile);
        event_title = findViewById(R.id.event_title);
        name = findViewById(R.id.user_name);
        event_content = findViewById(R.id.event_content);
        event_channel_name = findViewById(R.id.channel_name);
        delete = findViewById(R.id.delete);
        edit = findViewById(R.id.edit);
        move_channel = findViewById(R.id.move_channel);
        event_date = findViewById(R.id.event_date);

        if(channel_color != null){

            move_channel.setBackgroundColor(Color.parseColor(channel_color));
        } else {
            move_channel.setBackgroundColor(Color.parseColor("#F1B71C"));
        }

        // XML View 에 전달받은 부가 데이터를 저장합니다.
        event_title.setText(title);
        event_title.setTextColor(Color.parseColor(channel_color));
        name.setText(user_name + "(" + user_id + ")");
        event_date.setText(start_date + " ~ " + end_date);

        if(content == null){
            event_content.setText("학사 일정은 상세 내용이 존재하지 않습니다.");
            move_channel.setText("학사 일정");
        }else {
            event_content.setText(content);
        }

        event_channel_name.setText(channel_name);

        // schedule_title 타이틀 내용이 만약 글자 수를 초과할 시 흐르게 보여줍니다.
        event_title.setSingleLine(true);
        event_title.setEllipsize(TextUtils.TruncateAt.END);
        event_title.setSelected(true);

        move_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(content == null){
                    Toast.makeText(ChannelContent.this, "학사 일정은 채널이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(ChannelContent.this, ChannelsInfo2.class);
                    intent.putExtra("channel_id", channel_id);
                    intent.putExtra("userStatus", "2");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        // Actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_black_24dp2);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    // 일정 삭제 (Retrofit2)
    public void eventsDelete(){

        Call<Response<Data>> res = NetRetrofit.getInstance().getChannelEvent().DeleteChannelEvent(Login.getString("token",""),event_id);
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {

                Toast.makeText(ChannelContent.this, "일정을 삭제하였습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ChannelContent.this, MainActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {

                Toast.makeText(ChannelContent.this, "오류", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // 일정 수정 (Retrofit2)
    public void updateEvents(){

        Intent intent = new Intent(this, UpdateEvents.class);

        intent.putExtra("start_date",start_date);
        intent.putExtra("end_date", end_date);
        intent.putExtra("user_name", user_name);
        intent.putExtra("user_id", user_id);
        intent.putExtra("channel_name", user_name);
        intent.putExtra("title", title);
        intent.putExtra("content",content);
        intent.putExtra("event_id", event_id);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // delete (Onclick)
    public void delete(View view){

        if(content == null){

            Toast.makeText(ChannelContent.this, "학사 일정은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
        } else{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("안내");
            builder.setMessage("일정을 삭제하시겠습니까?");
            builder.setIcon(Integer.parseInt(String.valueOf(R.drawable.ic_delete_black_24dp)));

            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    eventsDelete();
                }
            });

            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(ChannelContent.this, "일정 삭제를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    // edit (Onclick)
    public void edit(View view){

        if(content == null){

            Toast.makeText(ChannelContent.this, "학사 일정은 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
        } else {

            updateEvents();
        }
    }

    // ActionBar Events
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
