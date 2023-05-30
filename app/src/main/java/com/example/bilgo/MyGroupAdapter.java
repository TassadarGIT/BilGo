package com.example.bilgo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.AbstractResolvableFuture;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilgo.model.UserModel;
import com.example.bilgo.utils.FirebaseUtil;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyGroupAdapter extends RecyclerView.Adapter<MyGroupAdapter.ViewHolder> {
    private List<UserModel> userList;

    public MyGroupAdapter(List<UserModel> userList) {
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
        private ImageView profileImgView;
        private String profileImgLink;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            pointsTextView = itemView.findViewById(R.id.points);
            rankTextView = itemView.findViewById(R.id.rank);
            profileImgView = itemView.findViewById(R.id.profilePhoto);
        }

        public void bind(UserModel user) {
            nameTextView.setText(user.getName() + " " + user.getSurname());
            pointsTextView.setText(String.valueOf(user.getPoints()));
            rankTextView.setText(String.valueOf(user.getRank()));
            profileImgLink = user.getProfilePictureLink();
            if(profileImgLink != null && profileImgLink.isEmpty() == false) {
                Picasso.get().load(profileImgLink).into(profileImgView);
            }
        }
    }
}
