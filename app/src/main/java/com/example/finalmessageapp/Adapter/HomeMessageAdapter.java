package com.example.finalmessageapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalmessageapp.Interface.MessageListener;
import com.example.finalmessageapp.Models.MessageData;
import com.example.finalmessageapp.R;

import java.util.ArrayList;

public class HomeMessageAdapter extends RecyclerView.Adapter<HomeMessageAdapter.MyViewHolder> {

    private ArrayList<MessageData> messageDataArrayList;
    private Context context;
    private MessageListener listener;

    public HomeMessageAdapter(ArrayList<MessageData> messageDataArrayList, Context context, MessageListener listener) {
        this.messageDataArrayList = messageDataArrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeMessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.homemessageitem,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMessageAdapter.MyViewHolder holder, int position) {
        holder.messagename.setText(messageDataArrayList.get(position).getMessagename());
        holder.messagedesc.setText(messageDataArrayList.get(position).getMessagedesc());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(messageDataArrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageDataArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView messagename,messagedesc;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.homemessageitem);
            messagename = itemView.findViewById(R.id.messagetitle);
            messagedesc = itemView.findViewById(R.id.messagedesc);
        }
    }
}
