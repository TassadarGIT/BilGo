package com.example.bilgo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bilgo.model.TripModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class TaxiFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaxiAdapter tripAdapter;
    private FirebaseFirestore firestore;
    private CollectionReference tripRef;
    private List<TripModel> tripList;
    private Button createBtn;
    private Button myGroupBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_taxi, container, false);
        createBtn = view.findViewById(R.id.createBtn);
        myGroupBtn = view.findViewById(R.id.myGroupBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new CreateGroupFragment());
            }
        });

        myGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new MyGroupFragment());
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

        createBtn = view.findViewById(R.id.createBtn);

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