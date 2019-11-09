package org.techtown.schooler.Channels.ListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import org.techtown.schooler.Account.AccountActivity2;
import org.techtown.schooler.Model.UserInfo;
import org.techtown.schooler.R;
import org.techtown.schooler.StartMemberActivity.LoginActivity;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import static android.content.Context.MODE_PRIVATE;

/**
 * @author 이영은
 */

public class AwaitUserAdapter extends RecyclerView.Adapter<AwaitUserAdapter.ViewHolder>{
    private final List<UserInfo> mDataList;
    private final List<UserInfo> arrayList;
    private SharedPreferences login;
    private String channel_id;

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_await_user_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public AwaitUserAdapter(List<UserInfo> dataList){
        mDataList = dataList;
        arrayList = new ArrayList<UserInfo>();
        arrayList.addAll(mDataList);
    }

    public void CatchChannelId(String channel_id){
        this.channel_id = channel_id;
    }
    //클래스->어뎁터 channel_id 전달.

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mDataList.clear();//mDataList 초기화
        if (charText.length() == 0) {
            //검색어가 없을 경우
            mDataList.addAll(arrayList);
        } else {
            //검색어가 있을 경우
            for (UserInfo userinfo : arrayList) {
                //Array list길이만큼 반복, UserInfo에 데이터 넣기.
                String name = userinfo.getUser_name();
                if (name.toLowerCase().contains(charText)) {
                    //입력한값과, userName이 동일 할 경우
                    mDataList.add(userinfo);
                }
            }
        }
        notifyDataSetChanged(); //리스트 초기화
    }
    //사용자이름을 검색하는 filter

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Activity activity = (Activity) holder.school.getContext();
        login = activity.getSharedPreferences("Login", MODE_PRIVATE);//SharedPreferences 선언
        UserInfo item = mDataList.get(position);

        holder.school.setText(item.getSchool().getSchool_name());
        //유저 학교 정보 로딩

        holder.gradeClass.setText(item.getSchool_grade()+"학년"+item.getSchool_class()+"학년");
        //유저 학년/반 정보 로딩

        holder.name.setText(item.getUser_name());
        //유저 이름 로딩

        holder.profile.setBackground(new ShapeDrawable(new OvalShape()));
        holder.profile.setClipToOutline(true);

        Glide.with(holder.gradeClass.getContext()).load(item.getProfile_pic()).into(holder.profile);
        //유저 프로필 로딩

        holder.allowbtn.setOnClickListener(v -> {
            Allow(holder, item);
        });
        //승인버튼 클릭 시

        holder.rejectbtn.setOnClickListener(v -> {
            Reject(holder, item);
        });
        //거절버튼 클릭 시

        holder.cardView.setOnClickListener(v ->{
            Intent intent = new Intent(activity, AccountActivity2.class);
            intent.putExtra("user_id",item.getUser_id());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView school;//유저 학교
        TextView gradeClass;//유저 학년/반
        TextView name;//유저이름
        ImageView profile;//유저 프로필
        TextView allowbtn;//승인버튼
        TextView rejectbtn;//거절버튼
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            school = (TextView)itemView.findViewById(R.id.school);
            gradeClass = (TextView)itemView.findViewById(R.id.gradeClass);
            name = (TextView)itemView.findViewById(R.id.name);
            profile = (ImageView)itemView.findViewById(R.id.profile);
            allowbtn= (TextView)itemView.findViewById(R.id.Allow);
            rejectbtn = (TextView)itemView.findViewById(R.id.Reject);
            cardView = (CardView)itemView.findViewById(R.id.ChannelCardView);
        }
    }

    private void Allow(ViewHolder holder,UserInfo item){
        Call<Response<Data>> res = NetRetrofit.getInstance().getChannel().AllowUser(login.getString("token",""),channel_id,item.getUser_id());//token불러오기
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if(response.code() == 200){
                    Toast.makeText(holder.profile.getContext(),R.string.allowMessage_1, Toast.LENGTH_SHORT).show();
                    mDataList.remove(item);
                    notifyDataSetChanged();
                }else if(response.code() == 400){
                    Toast.makeText(holder.profile.getContext(), R.string.allowMessage_2, Toast.LENGTH_SHORT).show();
                    //이미 승인된 유저
                }else if(response.code() == 403){
                    Toast.makeText(holder.profile.getContext(), R.string.permission_3, Toast.LENGTH_SHORT).show();
                    //권한 없음
                }else if(response.code() == 410){
                    //토큰만료
                    SharedPreferences.Editor editor = login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();
                    holder.profile.getContext().startActivity(new Intent(holder.profile.getContext(), LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(holder.profile.getContext(), R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }else{
                    //status : 500 : 서버오류
                    Log.e("","서버 오류 발생");
                    Toast.makeText(holder.profile.getContext(), R.string.serverErrorMessage_1, Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(holder.profile.getContext(), R.string.networkErrorMessage_1, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void Reject(ViewHolder holder,UserInfo item){
        final Call<Response<Data>> res = NetRetrofit.getInstance().getChannel().RejectUser(login.getString("token",""),channel_id,item.getUser_id());//token불러오기
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if(response.code() == 200){
                    Toast.makeText(holder.profile.getContext(), R.string.rejectMessage_1, Toast.LENGTH_SHORT).show();
                    mDataList.remove(item);
                    notifyDataSetChanged();
                }else if(response.code() == 400){
                    Toast.makeText(holder.profile.getContext(), R.string.rejectMessage_2, Toast.LENGTH_SHORT).show();
                    //이미 거절된 유저
                }else if(response.code() == 403){
                    Toast.makeText(holder.profile.getContext(), R.string.permission_3, Toast.LENGTH_SHORT).show();
                    //권한 없음
                }else if(response.code() == 410){
                    SharedPreferences.Editor editor = login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();
                    holder.profile.getContext().startActivity(new Intent(holder.profile.getContext(), LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(holder.profile.getContext(), R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("","서버 오류 발생");
                    Toast.makeText(holder.profile.getContext(), R.string.serverErrorMessage_1, Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(holder.profile.getContext(), R.string.networkErrorMessage_1, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
