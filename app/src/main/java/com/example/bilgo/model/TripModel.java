package com.example.bilgo.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class TripModel {

    private String departurePoint;
    private String destinationPoint;
    private String date;
    private int seatsAvailable = 4;
    public ArrayList<String> members;
    public ArrayList<String> memberNames;

    String name;
    UserModel user;

    public TripModel() {

    }

    public TripModel(String departurePoint, String destinationPoint, String date, int seatsAvailable, String userReference) {
        this.departurePoint = departurePoint;
        this.destinationPoint = destinationPoint;
        this.date = date;
        this.seatsAvailable = seatsAvailable;
        members = new ArrayList<String>();
        members.add(userReference);
        memberNames = new ArrayList<String>();

        FirebaseFirestore.getInstance().collection("users").document(userReference).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Document exists, retrieve its data
                        user = documentSnapshot.toObject(UserModel.class);
                        name = user.getName();
                        memberNames.add(name);

                        // Use the trip object as needed
                    } else {
                        // Document does not exist
                    }
                } else {
                    // Error occurred while fetching document
                }
            }
        });

    }

    public String getDeparturePoint() {
        return departurePoint;
    }

    public void setDeparturePoint(String departurePoint) {
        this.departurePoint = departurePoint;
    }

    public String getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(String destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public ArrayList<String> addMembers(String userReference){
        if(seatsAvailable > 0){
            members.add(userReference);

        }
        return members;
    }
    public void removeMember(String userReference){
        members.remove(userReference);
        seatsAvailable++;
    }


}
