package org.techtown.schooler.RecyclerView_main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.schooler.ChannelEvents.ChannelContent;
import org.techtown.schooler.Channels.ChannelsInfo;
import org.techtown.schooler.Channels.ChannelsInfo2;
import org.techtown.schooler.Model.Events;
import org.techtown.schooler.Model.JoinedChannel;
import org.techtown.schooler.R;

import java.io.InputStream;
import java.util.ArrayList;

public class JoinedChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView channel_name;
        ImageView channel_image;
        ImageView isPublic_image;
        CardView cardView;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public MyViewHolder(View view) {
            super(view);

            channel_name = view.findViewById(R.id.channel_name);
            channel_image = view.findViewById(R.id.channel_Image);
            isPublic_image = view.findViewById(R.id.isPublic);
            cardView = view.findViewById(R.id.ChannelCardView);

            // profile 즉 프로필 사진을 둥글게 만들어줍니다.
            channel_image.setBackground(new ShapeDrawable(new OvalShape()));
            channel_image.setClipToOutline(true);


            channel_name.setSingleLine(true);
            channel_name.setEllipsize(TextUtils.TruncateAt.END);
            channel_name.setSelected(true);

        }
    }

    private ArrayList<JoinedChannel> joinedChannelArrayList;

    public JoinedChannelAdapter(ArrayList<JoinedChannel> joinedChannelArrayList) {

        this.joinedChannelArrayList = joinedChannelArrayList;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_joined_channel, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;

        JoinedChannel item = joinedChannelArrayList.get(position);

        myViewHolder.channel_name.setText(joinedChannelArrayList.get(position).getName());

        Glide.with(((MyViewHolder) holder).channel_image.getContext()).load(item.getThumbnail()).into(myViewHolder.channel_image);


        if(item.getIsPublic().equals("0")){

            myViewHolder.isPublic_image.setImageResource(R.drawable.locked);
        } else {

            myViewHolder.isPublic_image.setImageResource(R.drawable.un_locked);
        }

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ChannelsInfo2.class);
                intent.putExtra("channel_id", joinedChannelArrayList.get(position).getId());
                intent.putExtra("userStatus", "2");
                v.getContext().startActivity(intent);

                Activity activity = (Activity) v.getContext();
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {

        return joinedChannelArrayList.size();
    }

}
