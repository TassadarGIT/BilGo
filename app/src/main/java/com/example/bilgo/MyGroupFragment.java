package com.example.bilgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilgo.model.TripModel;
import com.example.bilgo.model.UserModel;
import com.example.bilgo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class MyGroupFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyGroupAdapter myGroupAdapter;
    private FirebaseFirestore firestore;
    private CollectionReference myGroupRef;
    private List<String> userList;

    private Button leaveGroupBtn;

    private Button chatBtn;

    UserModel user;
    String tripID;

    TripModel currentTrip;

    ArrayList <String> members = new ArrayList<String>();
    ArrayList <String> memberNames = new ArrayList<String>();
    private CollectionReference groupRef;
    private CollectionReference userRef;
    String currentUserName;

    void getUsername() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {
                    user = task.getResult().toObject(UserModel.class);
                    Log.d("whatare", user.getName());
                    tripID = user.getTripID();
                    Log.d("whatare", tripID.toString());
                    groupRef = FirebaseFirestore.getInstance().collection("trips");
                    userRef = FirebaseFirestore.getInstance().collection("users");
                    groupRef.document(tripID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            currentTrip = task.getResult().toObject(TripModel.class);
                            Log.d("heyyy", currentTrip.toString());

                            members = currentTrip.members;
                            Log.d("heyyy", currentTrip.members.toString());
                            for(int i = 0; i < members.size(); i++){
                                String userID = members.get(i);
                                userRef.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        UserModel currentUser = task.getResult().toObject(UserModel.class);
                                        //memberNames.add(index, currentUser.getName());
                                        currentUserName = currentUser.getName();
                                        memberNames.add(currentUserName);
                                        Log.d("rfcd", memberNames.toString());
                                        // Check if all users have been processed
                                        if (memberNames.size() == members.size()) {
                                            Log.d("membernames", memberNames.toString());
                                            userList = memberNames;
                                            myGroupAdapter.updateData(userList);
                                            recyclerView.setAdapter(myGroupAdapter);
                                        }
                                    }
                                });
                            }
                            Log.d("membernames", memberNames.toString());
                            //Log.d("members", memberNames.toString());
                            userList = memberNames;
                            myGroupAdapter.updateData(userList);
                            recyclerView.setAdapter(myGroupAdapter);
                        }
                    });

                }
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_group, container, false);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setScrollbarFadingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        myGroupRef = firestore.collection("users");
        myGroupAdapter = new MyGroupAdapter(userList);

        leaveGroupBtn = view.findViewById(R.id.leaveGroupBtn);
        chatBtn = view.findViewById(R.id.chatBtn);

        getUsername();

    }
}