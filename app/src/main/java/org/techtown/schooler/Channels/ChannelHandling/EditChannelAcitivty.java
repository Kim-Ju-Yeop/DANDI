package org.techtown.schooler.Channels.ChannelHandling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.schooler.Channels.CreateChannel.CreateChannel;
import org.techtown.schooler.Model.ChannelEditRequest;
import org.techtown.schooler.R;
import org.techtown.schooler.StartMemberActivity.LoginActivity;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;
import petrov.kristiyan.colorpicker.ColorPicker;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author 이영은
 */

public class EditChannelAcitivty extends AppCompatActivity {

    private Toolbar toolbar;
    private String channel_name;
    private String channel_color;
    private String channel_explain;
    private String channel_check;
    private String channel_id;

    private EditText inputName;
    private EditText inputExplain;
    private Switch inputCheck;
    private TextView displayIsPublicMessage;
    private CardView inputColor;
    public static ChannelEditRequest channelEditRequest  = new ChannelEditRequest("","","");
    private SharedPreferences login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_channel);

        settingsToolbar();//툴바설정
        settingsActivity();//엑티비티 로드

        inputCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    channelEditRequest.setIsPublic("1");
                    displayIsPublicMessage.setTextColor(getResources().getColor(R.color.blue));
                    displayIsPublicMessage.setText(R.string.isPublic_2);
                }else{
                    channelEditRequest.setIsPublic("0");
                    displayIsPublicMessage.setTextColor(getResources().getColor(R.color.red));
                    displayIsPublicMessage.setText(R.string.isPublic_1);
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void pickColor(View view){
        openColorPicker();
    }//색상선택 다이얼로그 호출

    public void channelUpdate(View view) {
        channelEditRequest.setExplain(inputExplain.getText().toString());
        Call<Response<Data>> res = NetRetrofit.getInstance().getChannel().UpdateChannelInfo(login.getString("token",""),channel_id,channelEditRequest);
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if(response.code() == 200) {
                    onBackPressed();
                } else if(response.code() == 403){
                    //권한 없음
                    Toast.makeText(EditChannelAcitivty.this,R.string.permission_3,Toast.LENGTH_LONG).show();
                } else if(response.code() == 404){
                    //채널정보 없음
                    Toast.makeText(EditChannelAcitivty.this,R.string.channelMessage_1,Toast.LENGTH_LONG).show();
                }else if(response.code() == 410){
                    //토큰만료
                    SharedPreferences.Editor editor = login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();
                    startActivity(new Intent(EditChannelAcitivty.this, LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(EditChannelAcitivty.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }
                else{
                    //status : 500 : 서버오류
                    Log.e("","서버 오류 발생");
                    Toast.makeText(EditChannelAcitivty.this,R.string.serverErrorMessage_1,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(EditChannelAcitivty.this,R.string.networkErrorMessage_1,Toast.LENGTH_LONG).show();
            }
        });
    }//채널정보 업데이트(서버통신)

    private void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(this);  // ColorPicker 객체 생성
        colorPicker.setTitle("채널색상 선택");//타이틀 설정
        colorPicker.setColors(R.array.colorPicker)//colorPicker color 색상
                .setColumns(4)
                .setRoundColorButton(true)  // 원형 버튼으로 설정//
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {// OK 버튼 클릭 시 이벤트
                        if(color == 0){
                            Toast.makeText(EditChannelAcitivty.this,R.string.colorPickerMessage_1,Toast.LENGTH_LONG).show();
                            openColorPicker();
                        }else{
                            String strColor = String.format("#%06X", 0xFFFFFF & color);//Color int를 String 으로 변환하기
                            inputColor.setCardBackgroundColor(color);
                            channelEditRequest.setColor(strColor);
                        }
                    }
                    @Override
                    public void onCancel() {

                    }
                })
                .show();  // dialog 생성
    }//컬러피커

    private void settingsActivity(){
        login = getSharedPreferences("Login", MODE_PRIVATE);//SharedPreferences 선언
        inputName = (EditText)findViewById(R.id.EditChannelName);
        inputExplain = (EditText)findViewById(R.id.EditChannelExplain);
        inputCheck = (Switch)findViewById(R.id.EditChannelIsPublic);
        inputColor = (CardView)findViewById(R.id.EditChannelColor);
        displayIsPublicMessage = (TextView)findViewById(R.id.displayChannelIsPublic);
        getExtra();
        setData();
    }//엑티비티 설정

    private void settingsToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.channelFixTitle);
    }//toolbar 관련설정

    private void getExtra(){
        channel_id = getIntent().getStringExtra("channel_id");
        channel_name = getIntent().getStringExtra("channel_name");
        channel_color = getIntent().getStringExtra("channel_color");
        channel_explain = getIntent().getStringExtra("channel_explain");
        channel_check = getIntent().getStringExtra("channel_check");
    }//intent로 전달된 값들을 불러옵니다.(엑티비티 데이터전송 중, get)

    private void setData(){
        if(channel_check.equals("1")){
            inputCheck.setChecked(true);
            displayIsPublicMessage.setTextColor(getResources().getColor(R.color.blue));
            displayIsPublicMessage.setText(R.string.isPublic_2);
        }else if(channel_check.equals("0")){
            inputCheck.setChecked(false);
            displayIsPublicMessage.setTextColor(getResources().getColor(R.color.red));
            displayIsPublicMessage.setText(R.string.isPublic_1);
        }

        inputName.setText(channel_name);
        inputExplain.setText(channel_explain);
        inputColor.setCardBackgroundColor(Color.parseColor(channel_color));
        channelEditRequest.setExplain(channel_explain);
        channelEditRequest.setColor(channel_color);
        channelEditRequest.setIsPublic(channel_check);
    }//데이터 불러오기

    @Override
    public void onBackPressed () {
        setResult(RESULT_OK);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }//뒤로가기 버튼을 눌렀을 때
}
