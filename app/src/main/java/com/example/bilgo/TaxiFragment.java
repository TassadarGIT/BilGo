package com.example.bilgo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bilgo.model.TripModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class TaxiFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaxiAdapter tripAdapter;
    private FirebaseFirestore firestore;
    private CollectionReference tripRef;
    private List<TripModel> tripList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_taxi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        tripRef = firestore.collection("users"); // TODO: "trips"
        tripAdapter = new TaxiAdapter(tripList); // TODO: TaxiAdapter
        final Button[] joinBtn = new Button[1];

        tripRef.orderBy("points", Query.Direction.DESCENDING).addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                // TODO: Handle the error
                return;
            }

            if (querySnapshot != null) {
                tripList = querySnapshot.toObjects(TripModel.class);
                tripAdapter.updateData(tripList);
                recyclerView.setAdapter(tripAdapter);
            }
        });
    }
}