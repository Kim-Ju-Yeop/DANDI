package org.techtown.schooler.NavigationFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.techtown.schooler.ChannelEvents.CreateChannelEvents;
import org.techtown.schooler.ChannelEvents.EventDecorator;
import org.techtown.schooler.MainActivity;
import org.techtown.schooler.Model.Author;
import org.techtown.schooler.Model.Channel;
import org.techtown.schooler.Model.Events;
import org.techtown.schooler.RecyclerView_main.NoScheduleAdapter;
import org.techtown.schooler.RecyclerView_main.ScheduleAdapter;
import org.techtown.schooler.R;
import org.techtown.schooler.StartMemberActivity.LoginActivity;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;


import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    MaterialCalendarView materialCalendarView; // 캘린더

    RecyclerView recyclerView; // 리사이클러뷰

    // RecyclerView 속성인 LayoutManager 클래스를 사용해서 layoutManager 인스턴스를 생성하였습니다.
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

    // ArrayList 배열인 channelEventsData 배열 <Channel Events>
    ArrayList<Events> channelEventsData = new ArrayList<>();

    // ArrayList 배열인 schoolEventsData 배열 <School Events>
    ArrayList<Events> schoolEventsData = new ArrayList<>();

    SharedPreferences Login; // SharedPreferences 선언

    // 캘린더 날짜
    String selectedYear;
    int selectedMonth;
    String selectedDay;

    String month;

    boolean check = false;

    // ArrayList 배열인 EventsArrayList 배열 생성
    ArrayList<Events> EventsArrayList = new ArrayList<>();

    View view;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat s_year = new SimpleDateFormat("yyyy");
    SimpleDateFormat s_month = new SimpleDateFormat("MM");
    SimpleDateFormat s_day = new SimpleDateFormat("dd");

    ArrayList<CalendarDay> dates = new ArrayList<>();
    SwipeRefreshLayout mSwipeRefreshLayout;

    Date today = new Date();

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.schedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.add:

                Intent addEvents = new Intent(getActivity(), CreateChannelEvents.class);
                startActivity(addEvents);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            default:
                break;

        }

        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        materialCalendarView = rootView.findViewById(R.id.materialCalendarView); // 캘린더
        recyclerView = rootView.findViewById(R.id.recyclerView); // 리사이클러뷰
        Login = getActivity().getSharedPreferences("Login", MODE_PRIVATE); //SharedPreferences 선언
        view = rootView.findViewById(R.id.view);

//        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh);
//        settingsSwipeRefreshLayout();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 메인 프레그먼트의 툴바 색상을 설정하는 것입니다.
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

        checkChannelEvent();

        materialCalendarView.setSelectedDate(CalendarDay.today());
        selectedYear = s_year.format(today);
        selectedMonth = Integer.parseInt(s_month.format(today));
        selectedDay = s_day.format(today);

        onChannelEvent();
        onSchoolEvent();

        // 캘린더 클릭 시 발생하는 이벤트를 수행합니다.
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);

                EventsArrayList.clear();

                // 각각의 변수에 캘린더의 값을 저장하고있습니다.
                selectedYear = String.valueOf(date.getYear());
                month = String.valueOf(date.getMonth());
                selectedDay = String.valueOf(date.getDay());

                // selectedMonth 변수에 month 변수에 담겨있는 문자열의 값을 정수로 변환하고있습니다.
                selectedMonth = Integer.parseInt(month);

                // selectedMonth 값을 1 증가시킵니다.
                // 이유는 라이브러리 상 문제로 월의 단위가 1 씩 앞으로 당겨졌기 때문입니다.
                selectedMonth += 1;

                onChannelEvent();
                onSchoolEvent();

            }
        });

        setHasOptionsMenu(true);

        materialCalendarView.setOnTitleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog();
            }
        });

        return rootView;
    }


    public void onChannelEvent() {

            Call<Response<Data>> res = NetRetrofit.getInstance().getChannelEvent().GetChannelEvent(Login.getString("token", ""), "");
            res.enqueue(new Callback<Response<Data>>() {

                @Override
                public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {

                    if (response.code() == 200) {

                        Log.e("[status 200]", response.body().getMessage());

                        // channelEventsData 배열에 Events 데이터를 모두 저장합니다.
                        channelEventsData = (ArrayList<Events>) response.body().getData().getEvents();

                        boolean isEmpty = true;

                        // channelEventsData 의 size 만큼 반복문을 동작시킵니다.
                        for (int i = 0; i < channelEventsData.size(); i++) {

                            // start_date 변수에 channelEventsData 배열에 존재하는 i 번째 start_date 값을 저장합니다.
                            String start_date = channelEventsData.get(i).getStart_date();

                            String channelYear = start_date.substring(0, 4); // Year
                            String channelMonth = start_date.substring(5, 7); // Month
                            String channelDay = start_date.substring(8, 10); // Day

                            // 만약 사용자가 캘린더에서 선택한 날짜와 start_date 값이 일치하다면 조건을 실행합니다.
                            if (Integer.parseInt(selectedYear) == Integer.parseInt(channelYear) && selectedMonth == Integer.parseInt(channelMonth) && Integer.parseInt(selectedDay) == Integer.parseInt(channelDay)) {

                                isEmpty = false;

                                // channelEventsArrayList 배열에 channelEventsArrayList2 배열의 i 번째 데이터를 전달합니다.
                                EventsArrayList.add(channelEventsData.get(i));

                            }
                        }

                        if (isEmpty) {

                            Log.e("[채널 일정 X]", "채널 일정이 존재하지 않습니다.");
                            check = true;

                        } else {

                            ScheduleAdapter myAdapter = new ScheduleAdapter(EventsArrayList);
                            recyclerView.setAdapter(myAdapter);
                            check = false;


                        }
                    } else if (response.code() == 400) {

                        Log.e("[status 400]","error");
//                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 403) {

                        Log.e("[status 403]", response.body().getMessage());
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 410) {

                        SharedPreferences.Editor editor = Login.edit();
                        editor.putString("token", null);
                        editor.commit();

                        Log.e("[status 410]", "토큰 만료");
                        Toast.makeText(getActivity(), "토큰 만료, 로그인 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    } else if (response.code() == 500) {

                        Log.e("[status 500]", "오류");
                        Toast.makeText(getActivity(), "오류", Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<Response<Data>> call, Throwable t) {

                    Log.e("[ChannelEvent]", "네트워크 연결 오류");
                    Toast.makeText(getActivity(), "네트워크 연결 오류", Toast.LENGTH_SHORT).show();
                }

            });
    }

    public void onSchoolEvent() {

        Call<Response<Data>> res = NetRetrofit.getInstance().getSchoolEvent().GetSchoolEvent(Login.getString("token", ""), selectedYear, selectedMonth);
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {

                // ArrayList 배열인 schoolEventsArrayList 배열 생성
                ArrayList<Events> schoolEventsArrayList = new ArrayList<>();
                ArrayList<String> selectedEventsArrayList = new ArrayList<>();
                String date;

                if(selectedMonth < 10){

                    if(Integer.parseInt(selectedDay) < 10){
                        date = selectedYear + "-0" + selectedMonth + "-0" + selectedDay;
                    } else {

                        date = selectedYear + "-0" + selectedMonth + "-" + selectedDay;
                    }
                } else {

                    if(Integer.parseInt(selectedDay) < 10){

                        date = selectedYear + "-"+ selectedMonth + "-0" + selectedDay;
                    } else {

                        date = selectedYear + "-" + selectedMonth +"-" + selectedDay;
                    }
                }

                if (response.code() == 200) {

                    schoolEventsData = (ArrayList<Events>) response.body().getData().getEvents();

                    boolean isEmpty = true;

                    // channelEventsData 의 size 만큼 반복문을 동작시킵니다.
                    for (int i = 0; i < schoolEventsData.size(); i++) {

                        // start_date 변수에 channelEventsData 배열에 존재하는 i 번째 start_date 값을 저장합니다.
                        String start_date = schoolEventsData.get(i).getStart_date();

                        String channelYear = start_date.substring(0, 4); // Year
                        String channelMonth = start_date.substring(5, 7); // Month
                        String channelDay = start_date.substring(8, 10); // Day

                        // 만약 사용자가 캘린더에서 선택한 날짜와 start_date 값이 일치하다면 조건을 실행합니다.
                        if (Integer.parseInt(selectedYear) == Integer.parseInt(channelYear) && selectedMonth == Integer.parseInt(channelMonth) && Integer.parseInt(selectedDay) == Integer.parseInt(channelDay)) {

                            isEmpty = false;

                            schoolEventsData.get(i).setAuthor(new Author("school", "관리자"));
                            schoolEventsData.get(i).setChannel(new Channel("학사 일정", null,"#72BF44",null));

                            EventsArrayList.add(schoolEventsData.get(i));
                        }
                    }

                    if (isEmpty) {

                        Log.e("[학사 일정 X]", "학사 일정이 존재하지 않습니다.");

                        if (check == true) {

                            // schoolEventsArrayList.add(schoolEventsData.get(0));

                            selectedEventsArrayList.add(date);
                            NoScheduleAdapter noScheduleAdapter = new NoScheduleAdapter(selectedEventsArrayList);
                            recyclerView.setAdapter(noScheduleAdapter);
                        }

                    } else {

                        ScheduleAdapter myAdapter = new ScheduleAdapter(EventsArrayList);
                        recyclerView.setAdapter(myAdapter);
                    }

                } else if (response.code() == 500) {

                    Log.e("[status 500]", "학사 일정 조회에 실패하였습니다.");

                    Toast.makeText(getActivity(), "학사 일정 조회에 실패하였습니다.", Toast.LENGTH_SHORT).show();

                } else if(response.code() == 204){

                    Log.e("[status 204]", "학사일정 API 가 존재하지않습니다.");

                    if(check == true){

                        selectedEventsArrayList.add(date);
                        NoScheduleAdapter noScheduleAdapter = new NoScheduleAdapter(selectedEventsArrayList);
                        recyclerView.setAdapter(noScheduleAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {

            }
        });
    }

    public void datePickerDialog(){

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), 0, (view, year, month, dayOfMonth) -> {

            materialCalendarView.setCurrentDate(CalendarDay.from(year, month, dayOfMonth), true);
            materialCalendarView.setSelectedDate(CalendarDay.from(year,month,dayOfMonth));

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        pickerDialog.show();
    }

    public void checkChannelEvent(){
        Call<Response<Data>> res = NetRetrofit.getInstance().getChannelEvent().GetChannelEvent(Login.getString("token",""),"");
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {

                if(response.code() == 200){

                    ArrayList<Events> checkEventsData = new ArrayList<>();
                    checkEventsData = (ArrayList<Events>) response.body().getData().getEvents();


                    for(int i = 0; i < response.body().getData().getEvents().size(); i++){

                        try {
                            dates.add(CalendarDay.from(simpleDateFormat.parse(checkEventsData.get(i).getStart_date())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    materialCalendarView.addDecorator(new EventDecorator(Color.RED, dates));
                    Log.e("test", "체크함");
                }
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {

            }
        });
    }

    private void settingsSwipeRefreshLayout(){
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {

//        EventsArrayList.clear();
//
//        onChannelEvent();
//        onSchoolEvent();

        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);

        getActivity().overridePendingTransition(R.anim.loadfadein, R.anim.loadfadeout);

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


