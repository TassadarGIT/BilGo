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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
    UserModel currentUser;

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
                    tripID = user.getTripID();

                    groupRef = FirebaseFirestore.getInstance().collection("trips");
                    userRef = FirebaseFirestore.getInstance().collection("users");
                    groupRef.document(tripID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            currentTrip = task.getResult().toObject(TripModel.class);
                            members = currentTrip.members;

                            for(int i = 0; i < members.size(); i++){
                                String userID = members.get(i);
                                userRef.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        currentUser = task.getResult().toObject(UserModel.class);
                                        //memberNames.add(index, currentUser.getName());
                                        currentUserName = currentUser.getName();
                                        memberNames.add(currentUserName);
                                        // Check if all users have been processed
                                        if (memberNames.size() == members.size()) {
                                            userList = memberNames;
                                            myGroupAdapter.updateData(userList);
                                            recyclerView.setAdapter(myGroupAdapter);
                                        }
                                    }
                                });
                            }
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

        leaveGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            groupRef = FirebaseFirestore.getInstance().collection("trips");
                            userRef = FirebaseFirestore.getInstance().collection("users");

                            user = task.getResult().toObject(UserModel.class);

                            tripID = user.getTripID();
                            user.setTripID("");
                            user.setPoints(user.getPoints()-1);
                            FirebaseUtil.currentUserDetails().set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                            groupRef.document(tripID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    currentTrip = task.getResult().toObject(TripModel.class);

                                    currentTrip.removeMember(FirebaseUtil.currentUserID());
                                    //currentTrip.setSeatsAvailable(currentTrip.getSeatsAvailable() +1);

                                    groupRef.document(tripID).set(currentTrip).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                                }
                            });

                        }

                    }
                });

                /*
                currentUserName = user.getName();
                if(members.contains(user)) {
                    members.remove(user);
                }
                memberNames.remove(currentUserName);
                user.decreasePoints();
                user.setGroupIds(null);
                user.setTripID(null);
                 */
                changeFragment(new HitchhikerFragment());
            }
        });
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ChatScreen activity
                Intent intent = new Intent(getActivity(), ChatScreen.class);
                intent.putExtra("groupId", tripID);
                startActivity(intent);
            }
        });

    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }

}