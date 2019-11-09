package org.techtown.schooler.ChannelEvents;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.schooler.MainActivity;
import org.techtown.schooler.Model.UpdateChannelEvents;
import org.techtown.schooler.R;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;

public class UpdateEvents extends AppCompatActivity {

    // DatePicker
    int selectedYear;
    int selectedMonth;
    int selectedDay;

    // 부가 데이터
    String start_date;
    String end_date;
    String title;
    String content;
    String event_id;

    // XML View
    EditText events_title;
    EditText events_content;
    Button start_button;
    Button end_button;
    TextView update_schedule;

    // SharedPreferences 선언
    SharedPreferences Login;

    // Retrofit2 UpdateChannelEvents
    public static UpdateChannelEvents updateChannelEvents = new UpdateChannelEvents("","","","");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_events);

        // 부가 데이터 저장
        Intent intent = getIntent();

        // SharedPreferences
        Login = this.getSharedPreferences("Login", MODE_PRIVATE); //SharedPreferences 선언


        // 각각의 변수에 부가 데이터를 저장합니다.
        start_date = intent.getExtras().getString("start_date","");
        end_date = intent.getExtras().getString("end_date","");
        title = intent.getExtras().getString("title","");
        content = intent.getExtras().getString("content","");
        event_id = intent.getExtras().getString("event_id","");

        // XML View 를 초기화합니다.
        events_title = findViewById(R.id.title_editText);
        events_content = findViewById(R.id.content_editText);
        start_button = findViewById(R.id.start_button);
        end_button = findViewById(R.id.end_button);
        update_schedule = findViewById(R.id.move_channel);

        // XML View 에 전달받은 부가 데이터를 저장합니다.
        events_title.setText(title);
        events_content.setText(content);
        start_button.setText(start_date);
        end_button.setText(end_date);

        // updateChannelEvents (start_date, end_date)
        updateChannelEvents.setStart_date(start_date);
        updateChannelEvents.setEnd_date(end_date);

    }

    // start_button (시작 날짜 선택)
    public void startDatePicker() {

        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {

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

                updateChannelEvents.setStart_date(start_date);

            }

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setCalendarViewShown(false);

        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        datePickerDialog.show();
    }

    // end_button(종료 날짜 선택)
    public void endDatePicker() {

        Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {

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

                updateChannelEvents.setEnd_date(end_date);
            }

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        c.add(Calendar.YEAR,selectedYear-2019);

        datePickerDialog.getDatePicker().setCalendarViewShown(false);

        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        datePickerDialog.show();

    }

    // 일정 수정 (Retrofit2)
    public void updateChannelEvent(){

      Call<Response<Data>> res = NetRetrofit.getInstance().getChannelEvent().UpdateChannelEvent(Login.getString("token",""),event_id,updateChannelEvents);
      res.enqueue(new Callback<Response<Data>>() {
          @Override
          public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {

              if(response.code() == 200){

                  Log.e("[status 200]", response.message());
                  Toast.makeText(UpdateEvents.this, "일정을 정상적으로 수정하였습니다.", Toast.LENGTH_SHORT).show();

                  // MainActivity 로 화면 전환
                  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                  startActivity(intent);

                  overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
              } else if(response.code() == 400){

                  Log.e("[status 400]", response.message());
                  Toast.makeText(UpdateEvents.this, "입력하신 내용에 오류가 존재합니다, 수정을 해주십시오.", Toast.LENGTH_SHORT).show();
              } else if(response.code() == 404){

                  Log.e("[status 404]", response.message());
                  Toast.makeText(UpdateEvents.this, "일정 변경 권한이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
              } else if(response.code() == 500){

                  Log.e("[status 500]", response.message());
                  Toast.makeText(UpdateEvents.this, "일정을 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
              }
          }

          @Override
          public void onFailure(Call<Response<Data>> call, Throwable t) {

              Log.e("[서버 통신 X]", "서버 통신을 수행하지 못하였습니다.");
              Toast.makeText(UpdateEvents.this, "서버 통신 X", Toast.LENGTH_SHORT).show();
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

        updateChannelEvents.setTitle(events_title.getText().toString());
        updateChannelEvents.setContent(events_content.getText().toString());

        updateChannelEvent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
