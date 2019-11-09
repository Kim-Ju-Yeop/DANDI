package org.techtown.schooler.RecyclerView_main;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.schooler.ChannelEvents.CreateChannelEvents;
import org.techtown.schooler.MainActivity;
import org.techtown.schooler.Model.Events;
import org.techtown.schooler.R;

import java.util.ArrayList;

public class NoScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout button_layout;
        ImageButton imageButton;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public MyViewHolder(View view){
                super(view);

                button_layout = view.findViewById(R.id.button_layout);
                imageButton = view.findViewById(R.id.imageButton);
        }
    }

    private ArrayList<String> selectedEventsArrayList;

    public NoScheduleAdapter(ArrayList<String> selectedEventsArrayList){

        this.selectedEventsArrayList = selectedEventsArrayList;
    }

    public NoScheduleAdapter(){

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_no_schedule, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        NoScheduleAdapter.MyViewHolder myViewHolder = (NoScheduleAdapter.MyViewHolder) holder;
        myViewHolder.button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), CreateChannelEvents.class);
                intent.putExtra("start_date", selectedEventsArrayList.get(position));

                view.getContext().startActivity(intent);

                Activity activity = (Activity) view.getContext();

                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        myViewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), CreateChannelEvents.class);
                intent.putExtra("start_date", selectedEventsArrayList.get(position));

                view.getContext().startActivity(intent);

                Activity activity = (Activity) view.getContext();

                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


    }

    @Override
    public int getItemCount() {

        return selectedEventsArrayList.size();
    }

}
