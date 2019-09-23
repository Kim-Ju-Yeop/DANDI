package org.techtown.schooler.network;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.schooler.Model.ChannelInfo;
import org.techtown.schooler.R;
import org.techtown.schooler.network.response.Response;

import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ViewHolder>{

    private final List<ChannelInfo> mDataList;
    SharedPreferences Login;

    public ChannelListAdapter(List<ChannelInfo> dataList){
        mDataList = dataList;
    }

    ////
    public void setFilter(List<ChannelInfo> items) {
        mDataList.clear();
        mDataList.addAll(items);
        notifyDataSetChanged();
    }
    ////

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChannelInfo item = mDataList.get(position);
        holder.title.setText(item.getName());
        holder.content.setText(item.getExplain());
        holder.create_User.setText("| Master : " + item.getCreate_user());

        Login = holder.joinButton.getContext().getSharedPreferences("Login", MODE_PRIVATE);//SharedPreferences 선언

        if(item.getColor() != null){
            holder.colorBar.setVisibility(View.VISIBLE);
            holder.colorBar.setBackgroundColor(Color.parseColor(item.getColor()));
        }else{
            holder.colorBar.setVisibility(View.GONE);
        }

        new DownloadImageFromInternet(holder.BackGroundImage)
                .execute(item.getThumbnail());

        if(item.getIsPublic().equals("0")) {
            holder.isPublicImage.setImageResource(R.drawable.locked);
        }else{
            holder.isPublicImage.setImageResource(R.drawable.un_locked);
        }
        holder.joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("가입버튼","클릭됐어요");

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.joinButton.getContext());
                builder.setTitle("채널가입");
                builder.setMessage("채널에 가입하시겠습니까?\n\n채널이름 : "+item.getName()+"\n개설자 : "+item.getCreate_user());
                builder.setIcon(Integer.parseInt(String.valueOf(R.drawable.ic_exit_to_app_black_24dp)));

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final Call<Response<Data>> res1 = NetRetrofit.getInstance().getChannel().JoinChannel(Login.getString("token",""),item.getId());//token불러오기
                        res1.enqueue(new Callback<Response<Data>>() {
                            @Override
                            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                                if(response.isSuccessful()){
                                    if(item.getIsPublic() == "0" || item.getIsPublic() == "true"){
                                        Toast.makeText(holder.joinButton.getContext(),"채널가입 신청이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(holder.joinButton.getContext(), "채널가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    }

                                }else if(response.code() == 400){
                                    Toast.makeText(holder.joinButton.getContext(),"이미 가입된 채널이거나, 가입승인 대기중에 있습니다.",Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Response<Data>> call, Throwable t) {
                                Log.e("Err", "네트워크 연결오류");
                            }
                        });
                        Log.e("예 버튼","클릭됐어요");
                    }
                });

                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e("아니오 버튼","클릭됐어요");
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
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
        Button joinButton;
        View colorBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Title_TextView);
            title.setSingleLine(true);
            title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            title.setSelected(true);
            create_User = itemView.findViewById(R.id.MakeUser_TextView);
            content = itemView.findViewById(R.id.Content_TextView);
            BackGroundImage = itemView.findViewById(R.id.Background_ImageView);
            isPublicImage = itemView.findViewById(R.id.isPublic_ImageView);
            channelCardView = itemView.findViewById(R.id.ChannelCardView);
            joinButton = itemView.findViewById(R.id.JoinChannel_Button);
            colorBar = itemView.findViewById(R.id.colorBar);
        }
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("[ImageDownLoad][Error]", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
