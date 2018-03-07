package com.example.android_wifi;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by NOT on 11/17/17.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<ChatMessage> mMessages;
    private Context context;

    public CustomAdapter(Context context, List<ChatMessage> messages){
        this.context = context;
        mMessages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messageitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage message = mMessages.get(position);
        holder.messageTextView.setText(message.message);
//        Timestamp ts = new Timestamp(Long.parseLong(message.timeStamp));
        Date date = new Date(Long.parseLong(message.timeStamp));
        SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a");
        sdfTime.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String time= sdfTime.format(date);


        holder.userNameTextView.setText(message.username +" | " + time);
//        if(ChatManager.USERNAME.equals(message.username)){
//            holder.layout.setGravity(Gravity.RIGHT);
//            holder.userNameTextView.setVisibility(View.GONE);
//            holder.messageTextView.getBackground();
//        }else{
//            holder.layout.setGravity(Gravity.LEFT);
//            holder.userNameTextView.setVisibility(View.VISIBLE);
//            holder.messageTextView.setBackground(context.getDrawable(R.drawable.rounded_corner));
//        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void addNewDataToRecycler(ChatMessage message) {
        int i;
        for (i=0; i<mMessages.size(); i++) {
            if(Long.parseLong(message.timeStamp) < Long.parseLong(mMessages.get(i).timeStamp)){
                break;
            }
        }
        this.mMessages.add(i,message);
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

                notifyDataSetChanged();
//                ((ChatActivity) context).scrollToBottom();


            } // This is your code
        };
        mainHandler.post(myRunnable);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;
        public TextView messageTextView;
        public LinearLayout layout;

        public ViewHolder(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.bubbleLayout);
            userNameTextView = (TextView) view.findViewById(R.id.username);
            messageTextView = (TextView) view.findViewById(R.id.message);

        }
    }


}
