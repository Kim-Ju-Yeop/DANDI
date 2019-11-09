package org.techtown.schooler.Channels.CreateChannel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.techtown.schooler.Model.CreateChannelRequest;
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

public class CreateChannel extends AppCompatActivity {

    private CardView colorView;
    private EditText name;
    private EditText explain;
    private Switch isPublic;
    private SharedPreferences Login;
    private TextView Next;
    private InputMethodManager imm;
    private Toolbar toolbar;

    private String channel_name = null;
    private String create_user = null;
    private String channel_explain = null;
    private String channel_isPublic = null;
    private String channel_id = null;
    private TextView displayIsPublicMessage;

    private boolean next1 = false;
    private boolean next2 = false;
    private boolean next3 = false;

    public static CreateChannelRequest createChannelRequest = new CreateChannelRequest("","","","");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        settingsToolbar();
        settingsActivity();
        setData();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        isPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //공개
                    createChannelRequest.setIsPublic("true");
                    displayIsPublicMessage.setTextColor(getResources().getColor(R.color.blue));
                    displayIsPublicMessage.setText(R.string.isPublic_2);
                }else{
                    //비공개
                    createChannelRequest.setIsPublic("false");
                    displayIsPublicMessage.setTextColor(getResources().getColor(R.color.red));
                    displayIsPublicMessage.setText(R.string.isPublic_1);
                }
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(name.getText().toString().equals("")){
                    next1 = false;
                }else{
                    next1 = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (next1 && next2 && next3) {
                    Next.setEnabled(true);
                    Next.setBackgroundResource(R.color.mainColor);
                }else{
                    Next.setEnabled(false);
                    Next.setBackgroundResource(R.color.gray);
                }
            }
        });

        explain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(explain.getText().toString().equals("")){
                    next2 = false;
                }else{
                    next2 = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (next1 && next2 && next3) {
                    Next.setEnabled(true);
                    Next.setBackgroundResource(R.color.mainColor);
                }else{
                    Next.setEnabled(false);
                    Next.setBackgroundResource(R.color.gray);
                }
            }
        });
    }

    public void pickColor(View view){
        openColorPicker();
        colorView.setCardBackgroundColor(0);
    }

    public void createChannel(View view){
        if(name.getText().equals("")||explain.getText().equals("")){
            //비워져있으면
        }else{
            createChannelRequest.setName(name.getText()+"");
            createChannelRequest.setExplain(explain.getText()+"");
            addChannel();
            //서버통신 시작
        }
    }//채널만들기

    private void settingsActivity(){
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        displayIsPublicMessage = findViewById(R.id.displayChannelIsPublic2);
        colorView = (CardView) findViewById(R.id.pickColor);
        name =(EditText)findViewById(R.id.InputChannelName);
        explain = (EditText)findViewById(R.id.InputChannelExplain);
        isPublic = (Switch)findViewById(R.id.isPublicForChannel);
        Next = (TextView)findViewById(R.id.Finish);
        Login = getSharedPreferences("Login", MODE_PRIVATE);
        Next.setEnabled(false);
        Next.setBackgroundResource(R.color.gray);
        displayIsPublicMessage.setTextColor(getResources().getColor(R.color.red));
        displayIsPublicMessage.setText(R.string.isPublic_1);

    }//엑티비티 설정

    private void settingsToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("채널 개설");
    }

    private void setData(){
        createChannelRequest.setIsPublic("true");
        createChannelRequest.setExplain("");
        createChannelRequest.setName("");
        createChannelRequest.setColor(null);
    }//변수초기화

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
                            Toast.makeText(CreateChannel.this,R.string.colorPickerMessage_1,Toast.LENGTH_LONG).show();
                            openColorPicker();
                        }else{
                            String strColor = String.format("#%06X", 0xFFFFFF & color);//Color int를 String 으로 변환하기
                            colorView.setCardBackgroundColor(color);
                            createChannelRequest.setColor(strColor);
                            next3 = true;

                            if (next1 && next2 && next3) {
                                Next.setEnabled(true);
                                Next.setBackgroundResource(R.color.mainColor);
                            }else{
                                Next.setEnabled(false);
                                Next.setBackgroundResource(R.color.gray);
                        }
                        }
                    }
                    @Override
                    public void onCancel() {
                        // Cancel 버튼 클릭 시 이벤트
                        next3 = false;
                        if (next1 && next2 && next3) {
                            Next.setEnabled(true);
                            Next.setBackgroundResource(R.color.mainColor);
                        }else{
                            Next.setEnabled(false);
                            Next.setBackgroundResource(R.color.gray);
                        }
                    }
                })
                .show();  // dialog 생성
    }//컬러피커

    private void addChannel(){
        Call<Response<Data>> res = NetRetrofit.getInstance().getChannel().AddChannel(Login.getString("token",""),createChannelRequest);
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if (response.code() == 200) {
                    Intent intent = new Intent(CreateChannel.this, FinishCreateChannels.class);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    channel_name = response.body().getData().getCreatedChannel().getName();
                    create_user = response.body().getData().getCreatedChannel().getCreate_user();
                    channel_explain = response.body().getData().getCreatedChannel().getExplain();
                    channel_isPublic = response.body().getData().getCreatedChannel().getIsPublic();
                    channel_id = response.body().getData().getCreatedChannel().getId();

                    intent.putExtra("channel_name", channel_name);
                    intent.putExtra("create_user", create_user);
                    intent.putExtra("channel_explain", channel_explain);
                    intent.putExtra("channel_isPublic", channel_isPublic);
                    intent.putExtra("channel_id", channel_id);

                    startActivity(intent);

                } else if (response.code() == 409) {
                    Toast.makeText(CreateChannel.this, R.string.createChannelMessage_1, Toast.LENGTH_LONG).show();
                    imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
                } else if (response.code() == 410) {
                    SharedPreferences.Editor editor = Login.edit();
                    editor.putString("token", null);
                    editor.putString("id", null);
                    editor.commit();

                    startActivity(new Intent(CreateChannel.this, LoginActivity.class));
                    Log.e("", "토큰 만료");
                    Toast.makeText(CreateChannel.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateChannel.this, R.string.serverErrorMessage_1, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(CreateChannel.this, R.string.networkErrorMessage_1, Toast.LENGTH_SHORT).show();
            }
        });

    }//채널추가(서버통신)

    @Override
    public void onBackPressed () {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}