package com.example.bilgo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.bilgo.model.TripModel;
import com.example.bilgo.model.UserModel;
import com.example.bilgo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HitchhikerFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaxiAdapter tripAdapter;
    private FirebaseFirestore firestore;
    private CollectionReference tripRef;
    private List<TripModel> tripList;
    private Button myGroupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hitchhiker, container, false);
        myGroupButton = view.findViewById(R.id.myGroupBtn);
        myGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            UserModel userModel = task.getResult().toObject(UserModel.class);
                            Log.d("tripID", userModel.getTripID());
                            if((userModel.getTripID().equals(null) == false) && (userModel.getTripID().equals("") == false) ){
                                changeFragment(new MyGroupFragment());
                            }
                            else{
                                Toast.makeText(getContext(), "You are not in a group!", Toast.LENGTH_SHORT).show();
                                Log.d("tripID","it is invalid tripID");
                            }
                        }
                    }
                });
            }
        });

        return view;
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setScrollbarFadingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        tripRef = firestore.collection("trips");
        tripAdapter = new TaxiAdapter(tripList);

        tripRef.addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                // TODO: Handle the error
                return;
            }

            if (querySnapshot != null) {
                tripList = new ArrayList<>();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    TripModel trip = document.toObject(TripModel.class);
                    if (trip.getSeatsAvailable() > 0) {
                        tripList.add(trip);
                    }
                }
                tripAdapter.updateData(tripList);
                recyclerView.setAdapter(tripAdapter);
            }
        });
    }
}