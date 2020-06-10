package org.techtown.schooler.Channels.ListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.schooler.Channels.ChannelsInfo;
import org.techtown.schooler.Model.ChannelInfo;
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

public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ViewHolder>{

    private final List<ChannelInfo> mDataList;
    private final List<ChannelInfo> arrayList;
    SharedPreferences Login;



    public ChannelListAdapter(List<ChannelInfo> dataList){
        mDataList = dataList;
        arrayList = new ArrayList<ChannelInfo>();
        arrayList.addAll(mDataList);
    }

    public void filter(String charText,Integer pickUserStatus, String user_id) {
        charText = charText.toLowerCase(Locale.getDefault());

        mDataList.clear();
        for (ChannelInfo channelInfo : arrayList) {
            String name = channelInfo.getName();
            String master = channelInfo.getCreate_user();

            if (pickUserStatus == null) {
                if (name.toLowerCase().contains(charText) || master.toLowerCase().contains(charText)) {
                    mDataList.add(channelInfo);
                }
                //전체

            } else {

                if (charText.length() == 0) {
                    if (channelInfo.getUserStatus() == pickUserStatus) {
                        mDataList.add(channelInfo);
                    }else if (pickUserStatus == 3 && channelInfo.getCreate_user().equals(user_id)) {
                        if (channelInfo.getId() != null) {
                            mDataList.add(channelInfo);

                        }
                    }
                    //가입안된 채널

                } else if (pickUserStatus == 3 && (name.toLowerCase().contains(charText) || master.toLowerCase().contains(charText))) {
                    if (channelInfo.getCreate_user().equals(Login.getString("id", ""))) {
                        if(channelInfo.getId() != null){
                            mDataList.add(channelInfo);
                        }
                    }

                } else if ((channelInfo.getUserStatus() == pickUserStatus) && (name.toLowerCase().contains(charText) || master.toLowerCase().contains(charText))) {
                    if(channelInfo.getId() != null){
                        mDataList.add(channelInfo);
                    }
                }


            }
            //리펙토링 필요.
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Login = holder.joinButton.getContext().getSharedPreferences("Login", MODE_PRIVATE);//SharedPreferences 선언
        Activity activity = (Activity) holder.joinButton.getContext();

        ChannelInfo item = mDataList.get(position);
        holder.title.setText(item.getName());
        holder.content.setText(item.getExplain());
        holder.content.setMaxLines(2);
        holder.content.setEllipsize(TextUtils.TruncateAt.END);
        holder.create_User.setText("Master : " + item.getCreateUser().getUser_name()+"("+item.getCreateUser().getUser_id()+")");

        holder.BackGroundImage.setBackground(new ShapeDrawable(new OvalShape()));
        holder.BackGroundImage.setClipToOutline(true);
        Glide.with(holder.joinButton.getContext()).load(item.getThumbnail()).into(holder.BackGroundImage);

        if (item.getIsPublic().equals("0")) {
            holder.isPublicImage.setImageResource(R.drawable.locked);
        } else {
            holder.isPublicImage.setImageResource(R.drawable.un_locked);
        }

        if (item.getUserStatus() == 1) {
            holder.joinButton.setText("WAIT");
            holder.joinButton.setTextColor(Color.parseColor("#FFFF5722"));

        } else if (item.getUserStatus() == 2 && !(Login.getString("id", "").equals(item.getCreate_user()))) {
            holder.joinButton.setText("JOINED");
            holder.joinButton.setTextColor(Color.parseColor("#FF2196F3"));

        } else if (item.getUserStatus() == 2 && Login.getString("id", "").equals(item.getCreate_user())) {
            holder.joinButton.setText("MASTER");
            holder.joinButton.setTextColor(Color.parseColor("#F1B71C"));

        }else if (item.getUserStatus() == 0) {
            holder.joinButton.setText("JOIN");
            holder.joinButton.setTextColor(Color.parseColor("#7C000000"));
            holder.joinButton.setEnabled(true);
        }

        holder.channelCardView.setOnClickListener(v -> {

            Intent intent = new Intent(holder.joinButton.getContext(), ChannelsInfo.class);
            intent.putExtra("channel_id",item.getId());
            intent.putExtra("userStatus",item.getUserStatus()+"");
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        holder.joinButton.setOnClickListener(v -> {
            Log.e("가입버튼", "클릭됐어요");

            if (item.getUserStatus() == 1) {
                Toast.makeText(holder.joinButton.getContext(), "가입승인 대기중인 채널입니다.", Toast.LENGTH_SHORT).show();
            }
            else if (item.getUserStatus() == 2 && !(Login.getString("id", "").equals(item.getCreate_user()))) {
                Toast.makeText(holder.joinButton.getContext(), "이미 가입된 채널입니다.", Toast.LENGTH_SHORT).show();
            }else if(item.getUserStatus() == 2 && (Login.getString("id", "").equals(item.getCreate_user()))){
                Toast.makeText(holder.joinButton.getContext(), "채널의 관리자 입니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                joinChannel(item,activity);
            }
        });

    }

    private void joinChannel(ChannelInfo item,Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("채널가입");
        builder.setMessage("채널에 가입하시겠습니까?\n\n채널이름 : " + item.getName() + "\n개설자 : " + item.getCreate_user());
        builder.setIcon(Integer.parseInt(String.valueOf(R.drawable.ic_exit_to_app_black_24dp)));

        builder.setPositiveButton("예", (dialogInterface, i) -> {

            final Call<Response<Data>> res1 = NetRetrofit.getInstance().getChannel().JoinChannel(Login.getString("token", ""), item.getId());//token불러오기
            res1.enqueue(new Callback<Response<Data>>() {
                @Override
                public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                    if (response.code() == 200) {

                        if (item.getIsPublic().equals("0")) {
                            Toast.makeText(activity, "채널가입 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            item.setUserStatus(1);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(activity, "채널가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            item.setUserStatus(2);
                            notifyDataSetChanged();
                        }
                    }else if(response.code() == 409){
                        Toast.makeText(activity, "이미 가입된 채널입니다.", Toast.LENGTH_SHORT).show();
                    }else if(response.code() == 410){
                        SharedPreferences.Editor editor = Login.edit();
                        editor.putString("token",null);
                        editor.putString("id",null);
                        editor.commit();
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        Log.e("","토큰 만료");
                        Toast.makeText(activity, "토큰이 만료되었습니다\n다시 로그인 해 주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.e("","오류 발생");
                        Toast.makeText(activity, "서버에서 오류가 발생했습니다.\n문제가 지속되면 관리자에게 문의하세요", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<Response<Data>> call, Throwable t) {
                    Log.e("","네트워크 오류");
                    Toast.makeText(activity, "네크워크 상태가 원할하지 않습니다.\n잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("아니오", (dialogInterface, i) -> Log.e("아니오 버튼", "클릭됐어요"));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        ImageView BackGroundImage;
        ImageView isPublicImage;
        TextView create_User;
        CardView channelCardView;
        TextView joinButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.schedule_title);
            title.setSingleLine(true);
            title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            title.setSelected(true);
            create_User = itemView.findViewById(R.id.date);
            content = itemView.findViewById(R.id.Content_TextView);
            BackGroundImage = itemView.findViewById(R.id.channel_Image);
            isPublicImage = itemView.findViewById(R.id.isPublic_ImageView);
            channelCardView = itemView.findViewById(R.id.ChannelCardView);
            joinButton = itemView.findViewById(R.id.JoinChannel_Button);
        }
    }

}
