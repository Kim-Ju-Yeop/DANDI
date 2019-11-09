package org.techtown.schooler.ChannelEvents;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.schooler.MainActivity;
import org.techtown.schooler.Model.AddChannelEvents;
import org.techtown.schooler.R;
import org.techtown.schooler.StartMemberActivity.LoginActivity;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;

public class CreateChannelEvents extends AppCompatActivity {

    // DatePicker
    int selectedYear;
    int selectedMonth;
    int selectedDay;

    // Today Date
    Date today = new Date();
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

    // SharedPreferences 선언
    SharedPreferences Login;

    // XML View
    Button start_button;
    Button end_button;
    EditText title_editText;
    EditText content_editText;
    Spinner spinner;
    TextView add_schedule;

    // 부가 데이터
    String channelId;
    String start_date;
    String end_date;

    // Retrofit2 AddChannelEvents
    public static AddChannelEvents addChannelEvents = new AddChannelEvents("","","","");


    Boolean next1 = false;
    Boolean next2 = false;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel_events);

        Login = this.getSharedPreferences("Login", MODE_PRIVATE); //SharedPreferences 선언

        // XML View 를 초기화합니다.
        spinner = (Spinner)findViewById(R.id.spinner);
        start_button = findViewById(R.id.start_button);
        end_button = findViewById(R.id.end_button);
        title_editText = findViewById(R.id.title_editText);
        content_editText = findViewById(R.id.content_editText);
        add_schedule = findViewById(R.id.move_channel);

        // Channel Search
        searchChannel();

        // Actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_black_24dp2);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();

        if(intent.getExtras() == null){

            // start_button, end_button (text 변경)
            start_button.setText(date.format(today));
            end_button.setText(date.format(today));

            // start_date, end_date (text 변경)
            start_date = date.format(today);
            end_date = date.format(today);

        } else {

            String start = intent.getExtras().getString("start_date");

            // start_button, end_button (text 변경)
            start_button.setText(start);
            end_button.setText(start);

            // start_date, end_date (text 변경)
            start_date = start;
            end_date = start;
        }

        // addChannelEvents (start_date, end_date)
        addChannelEvents.setStart_date(start_date);
        addChannelEvents.setEnd_date(end_date);

        title_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){

                if(title_editText.length() != 0){
                    next1 = true;
                } else{
                    next1 = false;
                }

                if(next1 && next2){
                    add_schedule.setBackgroundResource(R.color.mainColor);
                    add_schedule.setEnabled(true);
                }else{
                    add_schedule.setBackgroundResource(R.color.gray);
                    add_schedule.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        content_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(content_editText.length() != 0){
                    next2 = true;
                } else{
                    next2 = false;
                }

                if(next2 && next1){
                    add_schedule.setBackgroundResource(R.color.mainColor);
                    add_schedule.setEnabled(true);
                }else{
                    add_schedule.setBackgroundResource(R.color.gray);
                    add_schedule.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    // ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    // ActionBar Events
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();//뒤로가기(이영은)
        }

        return super.onOptionsItemSelected(item);
    }

    // 채널 검색 (Retrofit2)
    public void searchChannel(){

        Call<Response<Data>> res = NetRetrofit.getInstance().getChannel().GetChannel(Login.getString("token",""));

        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {

                if(response.code() == 200){

                    Log.e("[status]", response.message());
                    ArrayList<String> arrayList = new ArrayList();
                    ArrayList<String> idList = new ArrayList();

                    for(int i = 0; i < response.body().getData().getJoinedChannel().size(); i++){

                        arrayList.add(response.body().getData().getJoinedChannel().get(i).getName());
                        idList.add(response.body().getData().getJoinedChannel().get(i).getId());
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, arrayList);
                    spinner.setAdapter(arrayAdapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            channelId = idList.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else if(response.code() == 204){
                    Log.e("[status]","채널 존재하지않음");
                    Toast.makeText(CreateChannelEvents.this, "채널 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if(response.code() == 410){
                    SharedPreferences.Editor editor = Login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();
                    startActivity(new Intent(CreateChannelEvents.this, LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(CreateChannelEvents.this, "토큰이 만료되었습니다\n다시 로그인 해 주세요", Toast.LENGTH_SHORT).show();
                } else{
                    Log.e("","오류 발생");
                    Toast.makeText(CreateChannelEvents.this, "서버에서 오류가 발생했습니다.\n문제가 지속되면 관리자에게 문의하세요", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(CreateChannelEvents.this, "네크워크 상태가 원할하지 않습니다.\n잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // start_button (시작 날짜 선택)
    public void startDatePicker() {

        Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateChannelEvents.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                if(monthOfYear+1<10){

                    if(dayOfMonth < 10){
                        start_button.setText(year + "-0" + (monthOfYear+1) + "-0" + dayOfMonth);
                    } else{

                        start_button.setText(year + "-0" + (monthOfYear+1) + "-" + dayOfMonth);
                    }
                } else{

                    if(dayOfMonth < 10){
                        start_button.setText(year + "-" + (monthOfYear+1) + "-0" + dayOfMonth);
                    }else{

                        start_button.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                    }
                }

                 selectedYear = year;
                 selectedMonth = monthOfYear;
                 selectedDay = dayOfMonth;

                 start_date = start_button.getText().toString();

                addChannelEvents.setStart_date(start_date);

            }

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setCalendarViewShown(false);

        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        datePickerDialog.show();

    }

    // end_button(종료 날짜 선택)
    public void endDatePicker() {

        Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateChannelEvents.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                if(monthOfYear+1 < 10){

                    if(dayOfMonth < 10){
                        end_button.setText(year + "-0" + (monthOfYear+1) + "-0" + dayOfMonth);
                    } else{

                        end_button.setText(year + "-0" + (monthOfYear+1) + "-" + dayOfMonth);
                    }

                } else{

                    if(dayOfMonth < 10){

                        end_button.setText(year + "-" + (monthOfYear+1) + "-0" + dayOfMonth);
                    } else{

                        end_button.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                    }

                }

                end_date = end_button.getText().toString();

                addChannelEvents.setEnd_date(end_date);
            }

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        c.add(Calendar.YEAR,selectedYear-2019);

        datePickerDialog.getDatePicker().setCalendarViewShown(false);

        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        datePickerDialog.show();

    }

    // 일정 추가 (Retrofit2)
    public void addChannelEvent(){

        Call<Response<Data>> res = NetRetrofit.getInstance().getChannelEvent().AddChannelEvent(Login.getString("token",""),channelId, addChannelEvents);
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {

                if(response.code() == 200)
                {
                    Log.e("[status 200]", "일정 추가에 성공하였습니다.");
                    Toast.makeText(CreateChannelEvents.this, "일정 추가에 성공하였습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CreateChannelEvents.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    add_schedule.setEnabled(false);//추가버튼 막기.(영은)

                } else if(response.code() == 400){

                    Log.e("[status 400]", "검증 오류입니다.");
                    Toast.makeText(CreateChannelEvents.this, "일정 종료 날짜를 바르게 설정해주십시오.", Toast.LENGTH_SHORT).show();
                } else if(response.code() == 403){

                    Log.e("[status 403]", "일정 추가 권한이 없습니다.");
                    Toast.makeText(CreateChannelEvents.this, "일정 추가 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                } else if(response.code() == 500){

                    Log.e("[status 500]", "일정 추가에 실패하였습니다.");
                    Toast.makeText(CreateChannelEvents.this, "일정 추가에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {

                Toast.makeText(CreateChannelEvents.this, "서버통신 x", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // start_button (Onclick)
    public void start(View view){
        startDatePicker();
    }

    // end_button (Onclick)
    public void end(View view){
        endDatePicker();
    }

    // editLayout (Onclick)
    public void edit(View view){

        addChannelEvents.setTitle(title_editText.getText().toString());
        addChannelEvents.setContent(content_editText.getText().toString());

        addChannelEvent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
