package org.techtown.schooler.Channels.ListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import org.techtown.schooler.Account.AccountActivity2;
import org.techtown.schooler.Model.User;
import org.techtown.schooler.R;
import org.techtown.schooler.StartMemberActivity.LoginActivity;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import static android.content.Context.MODE_PRIVATE;

/**
 * @author 이영은
 */

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder>{
    private final List<User> mDataList;
    private SharedPreferences login;
    private String channel_id;
    private String create_user;

    public void CatchChannelId(String channel_id, String Create_user){
        this.channel_id = channel_id;
        this.create_user = Create_user;
    }

    public MemberListAdapter(List<User> dataList){
        mDataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member,parent,false);
        //뷰홀더때문에 노가다했었음....
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User item = mDataList.get(position);
        String user_id;
        login = holder.Id.getContext().getSharedPreferences("Login", MODE_PRIVATE);

        holder.masterMark2.setVisibility(View.GONE);
        holder.masterMark1.setVisibility(View.GONE);

        holder.Name.setText(item.getUser_name());
        holder.Id.setText(item.getUser_id());
        user_id = item.getUser_id();

        if(create_user.equals(user_id)){
            holder.masterMark2.setVisibility(View.VISIBLE);
            holder.masterMark1.setVisibility(View.VISIBLE);
        }

        holder.Profile.setBackground(new ShapeDrawable(new OvalShape()));
        holder.Profile.setClipToOutline(true);
        Glide.with(holder.Name.getContext()).load(item.getProfile_pic()).into(holder.Profile);

        if(login.getString("id","").equals(create_user)){
            holder.MemberCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popup = new PopupMenu(holder.MemberCardView.getContext(), holder.MemberCardView);
                    if(!create_user.equals(user_id)) {
                        popup.inflate(R.menu.user_long_click);
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            kickUser(user_id,holder,position);
                            return true;
                        }
                    });

                    popup.show();
                    return true;
                }
            });
        }

        Activity activity = (Activity) holder.masterMark1.getContext();
        holder.MemberCardView.setOnClickListener(v ->{
            Intent intent = new Intent(holder.masterMark1.getContext(), AccountActivity2.class);
            intent.putExtra("user_id",item.getUser_id());
            holder.masterMark1.getContext().startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void kickUser(String user_id,ViewHolder holder,int position){
        Call<Response<Data>> res4 = NetRetrofit.getInstance().getChannel().ForcedExit(login.getString("token",""),channel_id,user_id);
        res4.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if(response.code() == 200){
                    Toast.makeText(holder.MemberCardView.getContext(), R.string.kickMessage_1, Toast.LENGTH_SHORT).show();
                    mDataList.remove(position);
                    notifyDataSetChanged();
                }else if(response.code() == 403){
                    //권한 없음
                    Toast.makeText(holder.MemberCardView.getContext(),R.string.permission_3,Toast.LENGTH_LONG).show();
                }else if(response.code() == 410){
                    //토큰만료
                    SharedPreferences.Editor editor = login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();
                    holder.MemberCardView.getContext().startActivity(new Intent(holder.MemberCardView.getContext(), LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(holder.MemberCardView.getContext(), R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }
                else{
                    //status : 500 : 서버오류
                    Log.e("","서버 오류 발생");
                    Toast.makeText(holder.MemberCardView.getContext(),R.string.serverErrorMessage_1,Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(holder.MemberCardView.getContext(), R.string.networkErrorMessage_1, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Id;
        ImageView Profile;
        CardView MemberCardView;
        ImageView masterMark1;
        ImageView masterMark2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            MemberCardView = itemView.findViewById(R.id.User_Info);
            Name = itemView.findViewById(R.id.Name_Info);
            Id = itemView.findViewById(R.id.ID_Info);
            Profile = itemView.findViewById(R.id.Profile_Info);
            masterMark1 = itemView.findViewById(R.id.masterMark1);
            masterMark2 = itemView.findViewById(R.id.masterMark2);
        }
    }
}
