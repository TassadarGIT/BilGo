package com.example.bilgo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilgo.model.TripModel;
import com.example.bilgo.model.UserModel;
import com.example.bilgo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class TaxiAdapter extends RecyclerView.Adapter<TaxiAdapter.ViewHolder> {

    private FirebaseFirestore db;

    private List<TripModel> tripList;
    UserModel userModel;

    public TaxiAdapter(List<TripModel> tripList) {
        this.tripList = tripList;
    }

    public void updateData(List<TripModel> updatedTripList) {
        tripList = updatedTripList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int newPosition = tripList.size()- position-1;
        TripModel trip = tripList.get(newPosition);

        holder.bind(trip);

        holder.joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("rf","works");

                CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("trips");
                //String newDocID = collectionReference.document().getId();
                //Log.d("s",newDocID);
                collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String ID = queryDocumentSnapshots.getDocuments().get(newPosition).getId();
                        List<TripModel> trips1 = queryDocumentSnapshots.toObjects(TripModel.class);
                        for(int i = 0; i < 1; i++){
                            Log.d("s", String.valueOf(newPosition));
                            Log.d("s",ID);
                        }
                        trip.members.add(FirebaseUtil.currentUserDetails().getId().toString());
                        Log.d("d", trip.members.toString());

                        FirebaseFirestore.getInstance().collection("trips").document(ID).set(trip);
                        // Changes...

                        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if(task.isSuccessful()) {
                                    userModel = task.getResult().toObject(UserModel.class);
                                    userModel.setTripID(ID);
                                    FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {

                                            }
                                        }
                                    });
                                    if(userModel != null) {

                                    }
                                }
                            }
                        });

                    }
                });

            }
        });

    }


    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //public TripModel trip;
        private TextView tripTimeText;
        private TextView seatsAvailableText;
        private TextView deptText;
        private TextView destText;
        private Button joinBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            tripTimeText = itemView.findViewById(R.id.tripTime);
            seatsAvailableText = itemView.findViewById(R.id.seatsAvailable);
            deptText = itemView.findViewById(R.id.deptText);
            destText = itemView.findViewById(R.id.destText);
            joinBtn = itemView.findViewById(R.id.joinBtn);
        }

        public void bind(TripModel trip) {
            tripTimeText.setText(trip.getDate().toString());
            seatsAvailableText.setText(trip.getSeatsAvailable() + " slots");
            deptText.setText(trip.getDeparturePoint());
            destText.setText(trip.getDestinationPoint());
        }
    }
}