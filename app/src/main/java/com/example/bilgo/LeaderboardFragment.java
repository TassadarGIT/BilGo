package com.example.bilgo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilgo.model.UserModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

public class LeaderboardFragment extends Fragment {
    private RecyclerView recyclerView;
    private LeaderboardAdapter leaderboardAdapter;
    private FirebaseFirestore firestore;
    private CollectionReference leaderboardRef;
    private List<UserModel> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setScrollbarFadingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        leaderboardRef = firestore.collection("users");
        leaderboardAdapter = new LeaderboardAdapter(userList);

        leaderboardRef.orderBy("points", Query.Direction.DESCENDING).addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                // TODO: Handle the error
                return;
            }

            if (querySnapshot != null) {
                userList = querySnapshot.toObjects(UserModel.class);
                leaderboardAdapter.updateData(userList);
                recyclerView.setAdapter(leaderboardAdapter);
            }
        });
    }
}
