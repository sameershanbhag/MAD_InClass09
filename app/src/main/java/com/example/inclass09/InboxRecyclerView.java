package com.example.inclass09;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class InboxRecyclerView extends RecyclerView.Adapter<InboxRecyclerView.MyViewHolder> {
    private ArrayList<Messages> messagesArrayList;

    private static DeleteListner deleteListner;

    public InboxRecyclerView(ArrayList<Messages> localMessagesArrayList, DeleteListner deleteListner) {
        this.messagesArrayList = localMessagesArrayList;
        this.deleteListner = deleteListner;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        Messages messages;
        TextView tv_subject;
        TextView tv_date;
        ImageView iv_delete;


        public MyViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();
            tv_subject = itemView.findViewById(R.id.tv_subject);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_delete = itemView.findViewById(R.id.iv_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("bagh", "Clicked "+ messages.toString());
                    deleteListner.displayMethod(messages);
                }
            });
        }
    }

    @NonNull
    @Override
    public InboxRecyclerView.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_recyclet, parent, false);
        MyViewHolder vh = new MyViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.messages = messagesArrayList.get(position);
        holder.tv_subject.setText(String.format("Subject: %s", messagesArrayList.get(position).getSubject()));
        holder.tv_date.setText(String.format("Date : %s",  messagesArrayList.get(position).getCreatedAt()));
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListner.deleteMethod(messagesArrayList.get(position).getID(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    public interface DeleteListner{
        void deleteMethod(String delId, Integer position);
        void displayMethod(Messages messages);
    }
}