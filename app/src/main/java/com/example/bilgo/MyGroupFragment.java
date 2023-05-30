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



    /* public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        m1 = view.findViewById(R.id.m1);
        m2 = view.findViewById(R.id.m2);
        m3 = view.findViewById(R.id.m3);
        m4 = view.findViewById(R.id.m4);
        m5 = view.findViewById(R.id.m5);
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
    }*/


        String groupID = "tfqVXFf74bPxld7J1LTD";
        //String groupID = userModel.getGroupId()

        //TripModel trip = FirebaseFirestore.getInstance().collection("trips").document(groupID);

        /*
        FirebaseFirestore.getInstance().collection("trips").document(groupID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DocumentSnapshot document = task.getResult();
            }
        });
        */

        /*
        FirebaseFirestore.getInstance().collection("trips").document(groupID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, retrieve its data
                        trip = document.toObject(TripModel.class);
                        Log.d("c", trip.members.toString());
                        for(int i = 0; i < trip.members.size(); i++){
                            Log.d("ed", "selamlar");
                            m1.setText("fdsx");
                        }

                        for(int i = 0; i < 5 - trip.memberNames.size(); i++){
                            memberNames.add("Waiting");
                        }
                        // set text

                        // Use the trip object as needed
                    } else {
                        // Document does not exist
                    }
                } else {
                    // Error occurred while fetching document
                }
            }
        });
        //Log.d("sw",trip.members.get(0)); --> null exception verriyo

        members = new ArrayList<String>();
        members.add("c");
        leaveGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("d","selamlar");

            }
        });

        */


    }
