package com.example.bilgo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class MyGroupFragment extends Fragment {
    private Button leaveGroupBtn;
    private RecyclerView recyclerView;
    private CollectionReference groupRef;
    private MyGroupAdapter groupAdapter;
    private List<UserModel> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        leaveGroupBtn = view.findViewById(R.id.leaveGroupBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setScrollbarFadingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        groupRef = firestore.collection("users");
        groupAdapter = new MyGroupAdapter(userList);

        groupRef.orderBy("points", Query.Direction.DESCENDING).addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                // TODO: Handle the error
                return;
            }

            if (querySnapshot != null) {
                userList = querySnapshot.toObjects(UserModel.class);
                groupAdapter.updateData(userList);
                recyclerView.setAdapter(groupAdapter);
            }
        });
    }
}
