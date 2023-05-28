package com.example.bilgo.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static String currentUserID() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn() {
        if(currentUserID() != null) {
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserID());
    }
    public static DocumentReference currentRingDriverDetails(){
        return FirebaseFirestore.getInstance().collection("drivers").document(currentUserID());
    }
    public static Task<DocumentSnapshot> currentDriverDetails() {
        return FirebaseFirestore.getInstance().collection("drivers").document(currentUserID()).get();
    }
}
