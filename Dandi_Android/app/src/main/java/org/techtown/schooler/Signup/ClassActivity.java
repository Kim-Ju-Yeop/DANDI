package org.techtown.schooler.Signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.schooler.R;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author 이영은
 */

public class ClassActivity extends AppCompatActivity {

    Integer PickGrade;
    Integer PickClass;
    Integer Class;
    String OfficeId;
    String SchoolId;
    String SchoolKind;

    TextView SchoolName;
    ArrayList<String> arrayListClass;
    ArrayAdapter<String> arrayAdapterClass;
    ArrayList<String> arrayListGrade;
    ArrayAdapter<String> arrayAdapterGrade;
    Spinner classPick;
    Spinner gradePick;
    CheckBox isTeacher;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        SchoolName = findViewById(R.id.SchoolName);
        SchoolName.setText(getIntent().getStringExtra("School"));

        OfficeId = getIntent().getStringExtra("OfficeId");
        SchoolId = getIntent().getStringExtra("SchoolId");
        SchoolKind = getIntent().getStringExtra("SchoolKind");
        classPick = findViewById(R.id.classSpinner);
        gradePick = findViewById(R.id.gradeSpinner);
        isTeacher = findViewById(R.id.isTeacher);

        arrayListClass = new ArrayList<>();
        arrayListGrade = new ArrayList<>();

        if (SchoolKind.equals("중학교") || SchoolKind.equals("고등학교")) {
            for(int i = 1;i<=3;i++)
                arrayListGrade.add(i+"학년");
        } else if (SchoolKind.equals("초등학교")) {
            for(int i = 1;i<=6;i++)
                arrayListGrade.add(i+"학년");
        } else {
            for(int i = 1;i<=6;i++)
                arrayListGrade.add(i+"학년");
        }

        arrayAdapterGrade = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayListGrade);
        gradePick.setAdapter(arrayAdapterGrade);



        classPick.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PickClass = i+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        gradePick.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PickGrade = i+1;
                SearchClass();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });



        isTeacher.setOnClickListener(new CheckBox.OnClickListener() {
            int PickClass_static;
            int PickGrade_static;

            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    gradePick.setEnabled(false);
                    classPick.setEnabled(false);

                    PickClass_static = PickClass;
                    PickGrade_static = PickGrade;

                    PickClass = null;
                    PickGrade = null;
                    Log.e("CLASS",PickClass+"");
                    Log.e("GRADE",PickGrade+"");
                } else {

                    PickClass = PickClass_static;
                    PickGrade = PickGrade_static;

                    gradePick.setEnabled(true);
                    classPick.setEnabled(true);

                    Log.e("CLASS",PickClass+"");
                    Log.e("GRADE",PickGrade+"");
                }
            }
        }) ;
    }

    public void SearchClass(){
        arrayListClass.clear();
        Call<Response<Data>> res;
        res = NetRetrofit.getInstance().getSignup().SearchClassGet(SchoolId, OfficeId, PickGrade + "");
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                Log.d("Retrofit", response.toString());
                if (response.code() == 200) {
                    Class = response.body().getData().getClassCount();
                    Log.d("", Class + "");
                    for(int i = 1;i<=Class;i++)
                        arrayListClass.add(i+"반");
                } else if(response.code() == 404){
                    for(int i = 1;i<=50;i++)
                        arrayListClass.add(i+"반");

                }else{
                    Toast.makeText(ClassActivity.this, "서버에서 오류가 발생했습니다.\n문제가 지속되면 관리자에게 문의하세요", Toast.LENGTH_SHORT).show();
                }
                arrayAdapterClass = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayListClass);
                classPick.setAdapter(arrayAdapterClass);
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(ClassActivity.this, "네크워크 상태가 원할하지 않습니다.\n잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void toGoNext(View view){
        Intent intent = new Intent(getApplicationContext(), NotifyActivity.class);
        intent.putExtra("Name",getIntent().getStringExtra("Name"));
        intent.putExtra("Id",getIntent().getStringExtra("Id"));
        intent.putExtra("Password",getIntent().getStringExtra("Password"));
        intent.putExtra("Email",getIntent().getStringExtra("Email"));
        intent.putExtra("PhoneNumber",getIntent().getStringExtra("PhoneNumber"));
        intent.putExtra("SchoolId", getIntent().getStringExtra("SchoolId"));
        intent.putExtra("SchoolName", getIntent().getStringExtra("School"));
        intent.putExtra("Grade",PickGrade+"");
        intent.putExtra("Class",PickClass+"");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void toGoBack (View view) {
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed () {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
