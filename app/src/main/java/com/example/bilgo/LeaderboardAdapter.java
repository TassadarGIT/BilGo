package com.example.bilgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilgo.model.UserModel;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private List<UserModel> userList;

    public LeaderboardAdapter(List<UserModel> userList) {
        this.userList = userList;
    }

    public void updateData(List<UserModel> updatedUserList) {
        userList = updatedUserList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = userList.get(position);
        user.setRank(position + 1);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView pointsTextView;
        private TextView rankTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            pointsTextView = itemView.findViewById(R.id.points);
            rankTextView = itemView.findViewById(R.id.rank);
        }

        public void bind(UserModel user) {
            nameTextView.setText(user.getName() + " " + user.getSurname());
            pointsTextView.setText(String.valueOf(user.getPoints()));
            rankTextView.setText(String.valueOf(user.getRank()));
        }
    }
}
