package org.techtown.schooler.Signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.schooler.ChannelEvents.CreateChannelEvents;
import org.techtown.schooler.network.SchoolListAdapter;
import org.techtown.schooler.Model.SchoolList;
import org.techtown.schooler.R;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;
import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author 이영은
 */

public class SchoolActivity extends AppCompatActivity {

    private SchoolListAdapter adapter;
    TextView Search;
    EditText SearchSchoolName;
    private ListView listView;
    TextView DecideSchoolName;
    TextView GotoClass;
    String School;
    String SchoolId;
    String OfficeId;
    String SchoolKind;
    LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SchoolListAdapter();
        setContentView(R.layout.activity_school);
        listView = (ListView) findViewById(R.id.School_ListView);
        SearchSchoolName = (EditText)findViewById(R.id.InputSchoolName);
        listView.setAdapter(adapter);
        DecideSchoolName = (TextView)findViewById(R.id.decideSchoolName);
        DecideSchoolName.setVisibility(View.INVISIBLE);
        GotoClass = (TextView)findViewById(R.id.next_class);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        GotoClass.setEnabled(false);
        GotoClass.setBackgroundResource(R.color.gray);

        layout = (LinearLayout) findViewById(R.id.signUpSchoolLayout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(),0);
            }
        });

        SearchSchoolName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(SearchSchoolName.getText().toString().length() != 0)
                    onSearch();
                else
                    listView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void onSearch() {
        String SearchSchool = (SearchSchoolName.getText().toString());
        final Call<Response<Data>> res = NetRetrofit.getInstance().getSignup().SearchSchoolGet(SearchSchool);
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if (response.code() == 200) {
                    String[] contents = new String[response.body().getData().getSchoolInfo().size()];
                    String[] titles = new String[response.body().getData().getSchoolInfo().size()];

                    String SchoolName = "";
                    String SchoolLocal = "";

                    adapter.clear();
                    for (int A = 0; A < response.body().getData().getSchoolInfo().size(); A++) {
                        SchoolList dto = new SchoolList();
                        SchoolName = response.body().getData().getSchoolInfo().get(A).getSchool_name();
                        SchoolLocal = response.body().getData().getSchoolInfo().get(A).getSchool_locate();

                        contents[A] = SchoolLocal;
                        titles[A] = SchoolName;

                        dto.setTitle(titles[A]);
                        dto.setContent(contents[A]);
                        adapter.addItem(dto);
                    }

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick (AdapterView< ? > parent, View view, final int position, long id) {
                            School = response.body().getData().getSchoolInfo().get(position).getSchool_name();
                            SchoolId = response.body().getData().getSchoolInfo().get(position).getSchool_code();
                            OfficeId = response.body().getData().getSchoolInfo().get(position).getOffice_code();
                            SchoolKind = response.body().getData().getSchoolInfo().get(position).getSchool_type();
                            DecideSchoolName.setText(School);
                            InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(layout.getWindowToken(),0);
                            DecideSchoolName.setVisibility(View.VISIBLE);

                            GotoClass.setEnabled(true);
                            GotoClass.setBackgroundResource(R.color.mainColor);
                        }
                    });

                    listView.setVisibility(View.VISIBLE);


                }else if(response.code() == 404) {
                    listView.setVisibility(View.INVISIBLE);
                }else{
                    Toast.makeText(SchoolActivity.this, "서버에서 오류가 발생했습니다.\n문제가 지속되면 관리자에게 문의하세요", Toast.LENGTH_SHORT).show();
                    listView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(SchoolActivity.this, "네크워크 상태가 원할하지 않습니다.\n잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                listView.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void toGoBack (View view) {
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void toGoNext(View view){
        Intent intent = new Intent(getApplicationContext(), ClassActivity.class);
        intent.putExtra("Name",getIntent().getStringExtra("Name"));
        intent.putExtra("Id",getIntent().getStringExtra("Id"));
        intent.putExtra("Password",getIntent().getStringExtra("Password"));
        intent.putExtra("Email",getIntent().getStringExtra("Email"));
        intent.putExtra("PhoneNumber",getIntent().getStringExtra("PhoneNumber"));
        intent.putExtra("School", School);
        intent.putExtra("SchoolId", SchoolId);
        intent.putExtra("OfficeId", OfficeId);
        intent.putExtra("SchoolKind", SchoolKind);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed () {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
