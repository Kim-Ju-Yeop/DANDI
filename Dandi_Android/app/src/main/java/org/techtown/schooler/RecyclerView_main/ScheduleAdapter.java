package org.techtown.schooler.RecyclerView_main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.schooler.ChannelEvents.ChannelContent;
import org.techtown.schooler.Model.Events;
import org.techtown.schooler.R;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView channel_name;
        TextView user_name;
        TextView schedule_title;
        TextView date;
        ImageView channel_image;
        LinearLayout top_layout;
        LinearLayout layout;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public MyViewHolder(View view){
            super(view);

            channel_name = view.findViewById(R.id.channel_name);
            user_name = view.findViewById(R.id.user_name);
            schedule_title = view.findViewById(R.id.schedule_title);
            channel_image = view.findViewById(R.id.channel_Image);
            top_layout = view.findViewById(R.id.top_layout);
            date = view.findViewById(R.id.date);
            layout = view.findViewById(R.id.ChannelCardView);

            // schedule_title 타이틀 내용이 만약 글자 수를 초과할 시 흐르게 보여줍니다.
            schedule_title.setSingleLine(true);
            schedule_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            schedule_title.setSelected(true);

            // Profile Round
            channel_image.setBackground(new ShapeDrawable(new OvalShape()));
            channel_image.setClipToOutline(true);

        }
    }

    private ArrayList<Events> channelEventsArrayList;

    public ScheduleAdapter(ArrayList<Events> channelEventsArrayList){

        this.channelEventsArrayList = channelEventsArrayList;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule2, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Events item = channelEventsArrayList.get(position);

        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.channel_name.setText("channel : " + channelEventsArrayList.get(position).getChannel().getName());

        if(channelEventsArrayList.get(position).getAuthor().getUser_id() == null){

            myViewHolder.user_name.setText("user : " + channelEventsArrayList.get(position).getAuthor().getUser_name() + "(school)");
        } else{
            myViewHolder.user_name.setText("user : " + channelEventsArrayList.get(position).getAuthor().getUser_name() + "(" + channelEventsArrayList.get(position).getAuthor().getUser_id() + ")");
        }

        myViewHolder.date.setText(channelEventsArrayList.get(position).getStart_date()+" ~ " + channelEventsArrayList.get(position).getEnd_date());

        myViewHolder.schedule_title.setText(channelEventsArrayList.get(position).getTitle());

        if(channelEventsArrayList.get(position).getChannel().getColor() == null) {

            myViewHolder.top_layout.setBackgroundColor(Color.parseColor("#F1B71C"));
            myViewHolder.schedule_title.setTextColor(Color.parseColor("#F1B71C"));
        } else{

            myViewHolder.top_layout.setBackgroundColor(Color.parseColor(channelEventsArrayList.get(position).getChannel().getColor()));
            myViewHolder.schedule_title.setTextColor(Color.parseColor(channelEventsArrayList.get(position).getChannel().getColor()));
        }

        if(item.getChannel().getThumbnail() != null){

            Glide.with(((MyViewHolder) holder).user_name.getContext()).load(item.getChannel().getThumbnail()).into(((MyViewHolder) holder).channel_image);
        } else{

            myViewHolder.channel_image.setImageResource(R.drawable.channel_school);
        }

        myViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChannelContent.class);

                intent.putExtra("start_date",channelEventsArrayList.get(position).getStart_date());
                intent.putExtra("end_date", channelEventsArrayList.get(position).getEnd_date());
                intent.putExtra("user_name", channelEventsArrayList.get(position).getAuthor().getUser_name());
                intent.putExtra("user_id", channelEventsArrayList.get(position).getAuthor().getUser_id());
                intent.putExtra("channel_name", channelEventsArrayList.get(position).getChannel().getName());
                intent.putExtra("channel_color", channelEventsArrayList.get(position).getChannel().getColor());
                intent.putExtra("title", channelEventsArrayList.get(position).getTitle());
                intent.putExtra("channel_image", channelEventsArrayList.get(position).getChannel().getThumbnail());
                intent.putExtra("content",channelEventsArrayList.get(position).getContent());
                intent.putExtra("event_id", channelEventsArrayList.get(position).getId());
                intent.putExtra("channel_id", channelEventsArrayList.get(position).getChannel().getId());

                v.getContext().startActivity(intent);

                Activity activity = (Activity) v.getContext();
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {

        return channelEventsArrayList.size();
    }

}
