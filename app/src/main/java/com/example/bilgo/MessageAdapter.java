package com.example.bilgo;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bilgo.model.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<MessageModel> messages;

    public MessageAdapter(Context context, List<MessageModel> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null && message.getSenderId().equals(currentUser.getUid())) {
            // Message from the current user
            holder.layoutMessageContainer.setGravity(Gravity.END);
            holder.textViewMessageContent.setBackgroundResource(R.drawable.round_rectangle_primary);
            holder.textViewUserName.setText(message.getSenderName());
            holder.textViewUserName.setGravity(Gravity.END);
        } else {
            // Message from another user
            holder.layoutMessageContainer.setGravity(Gravity.START);
            holder.textViewMessageContent.setBackgroundResource(R.drawable.round_rectangle_secondary);
            holder.textViewUserName.setText(message.getSenderName());
            holder.textViewUserName.setGravity(Gravity.START);
        }

        // Bind the message data to the views
        holder.textViewMessageContent.setText(message.getContent());
        holder.textViewMessageTime.setText(message.getFormattedTimestamp());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        TextView textViewMessageContent;
        LinearLayout layoutMessageContainer;
        TextView textViewMessageTime;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.text_view_user_name);
            textViewMessageContent = itemView.findViewById(R.id.text_view_message_content);
            layoutMessageContainer = itemView.findViewById(R.id.layout_message_container);
            textViewMessageTime = itemView.findViewById(R.id.text_view_message_time);
        }
    }
}
