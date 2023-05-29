package com.example.bilgo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bilgo.model.GroupModel;
import com.example.bilgo.model.TripModel;
import com.example.bilgo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;


public class CreateGroupFragment extends Fragment {

    private Button createBtn;
    private String hour;
    private String minute;

    private Spinner hourSpinner;
    private Spinner minuteSpinner;
    private EditText deptEdit;
    private EditText destEdit;
    private EditText slotsEdit;
    private CollectionReference groupsRef;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //hour selection
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_group, container, false);
        hourSpinner = rootView.findViewById(R.id.spinnerHours);
        minuteSpinner = rootView.findViewById(R.id.spinnerMinutes);
        inItSpinnerHours();
        inItSpinnerMinutes();
        return rootView;
    }

    private void inItSpinnerHours() {
        String[] items = new String[]{
                "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        hourSpinner.setAdapter(adapter);
        hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }
    private void inItSpinnerMinutes() {
        String[] items = new String[]{
                "00", "15", "30", "45"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        minuteSpinner.setAdapter(adapter);
        minuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createBtn = view.findViewById(R.id.createBtn);
        deptEdit = view.findViewById(R.id.deptEdit);
        destEdit = view.findViewById(R.id.destEdit);
        slotsEdit = view.findViewById(R.id.slotsEdit);

        hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hour = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO
            }
        });

        minuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                minute = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dept = deptEdit.getText().toString();
                String dest = destEdit.getText().toString();
                int seatsAvailable = 4; // by default
                seatsAvailable = Integer.parseInt(slotsEdit.getText().toString());
                TripModel trip = new TripModel(dept, dest, hour + minute, seatsAvailable, FirebaseUtil.currentUserID().toString());

                // Add the trip object to Firestore
                db.collection("trips").add(trip)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Trip added successfully

                                // Retrieve the auto-generated trip ID
                                String tripId = documentReference.getId();

                                // Check if currentUser is not null before accessing getUid()
                                if (currentUser != null) {
                                    // Create a new ChatScreen object
                                    //ChatScreen chatScreen = new ChatScreen(tripId, currentUser.getUid());

                                    // Add the group object to Firestore
                                    groupsRef = db.collection("groups");
                                    GroupModel group = new GroupModel(Arrays.asList(currentUser.getUid()));
                                    groupsRef.add(group)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    // The group was successfully added to Firestore
                                                    // Retrieve the auto-generated document ID
                                                    String groupId = documentReference.getId();
                                                    // Use the groupId as needed
                                                    // For example, you can store it in a variable or pass it to another function
                                                    Intent intent = new Intent(getActivity(), ChatScreen.class);
                                                    intent.putExtra("groupId", groupId);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // An error occurred while adding the group to Firestore
                                                    // Handle the failure and display an error message or perform appropriate actions
                                                }
                                            });
                                } else {
                                    // Handle the case when currentUser is null
                                    // Display an error message or perform appropriate actions
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // An error occurred while adding the trip to Firestore
                                // Handle the failure and display an error message or perform appropriate actions
                            }
                        });
            }
        });
    }}

