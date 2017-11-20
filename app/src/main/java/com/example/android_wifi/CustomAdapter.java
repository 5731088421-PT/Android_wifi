package com.example.android_wifi;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

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

        View view = LayoutInflater.from(context).inflate(R.layout.messageitem,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage message = mMessages.get(position);
        holder.messageTextView.setText(message.message);
        holder.userNameTextView.setText(message.username);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void addNewDataOnTop(ChatMessage chatList) {
        this.mMessages.add((mMessages.size() == 0 ? 0:mMessages.size()),chatList);
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {notifyDataSetChanged();
            } // This is your code
        };
        mainHandler.post(myRunnable);


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;
        public TextView messageTextView;

        public ViewHolder(View view) {
            super(view);

            userNameTextView = (TextView) view.findViewById(R.id.username);
            messageTextView = (TextView) view.findViewById(R.id.message);

        }
    }


}
