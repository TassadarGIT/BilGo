package com.example.bilgo;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;


import com.example.bilgo.model.RingDriverModel;
import com.example.bilgo.model.UserModel;
import com.example.bilgo.services.LocationService;
import com.example.bilgo.utils.FirebaseUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class MapFragment extends Fragment{

    SupportMapFragment mapFragment;
    UserModel userModel;
    Button driverUserButton;
    Button driverResignButton;
    GoogleMap googleMap;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentUserId = FirebaseUtil.currentUserID();
    private Map<Marker, String> markerDriverMap = new HashMap<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 31;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    public void enableLocation(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

        }
        else{
            ActivityCompat.requestPermissions(requireActivity(), new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                googleMap = map;
                googleMap.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(39.861267,32.741059),new LatLng(39.940512,32.851970)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.868311,32.748944), 15));
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                });
                UiSettings uiManager = googleMap.getUiSettings();
                uiManager.setAllGesturesEnabled(true);
                uiManager.setCompassEnabled(true);
                uiManager.setZoomControlsEnabled(true);
                enableLocation();
                uiManager.setMapToolbarEnabled(true);
                uiManager.setMyLocationButtonEnabled(true);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String currentUserId = FirebaseUtil.currentUserID();
                db.collection("drivers").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot ds = task.getResult();
                            if(ds.exists()){
                                RingDriverModel ringDriver = ds.toObject(RingDriverModel.class);
                                if(ringDriver != null){
                                    Intent intent = new Intent(getContext(), LocationService.class);
                                    getContext().startForegroundService(intent);
                                }
                            }
                            else{
                                Log.e(TAG,"Document does not exist!");
                            }
                        }
                        else{
                            Log.e(TAG,"Task failed with " + task.getException());
                        }
                    }
                });
                startListening();
            }
        });
        return rootView;
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
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    checkIfUserIsDriver();
                }
            }
        });

        driverUserButton = view.findViewById(R.id.driverUserButton);
        driverResignButton = view.findViewById(R.id.driverResignButton);
        driverUserButton.setEnabled(false);
        driverUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new DriverRegisterFragment());
            }
        });
        driverResignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtil.currentRingDriverDetails().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "You have successfully resigned as a driver.", Toast.LENGTH_SHORT).show();
                        driverResignButton.setVisibility(View.GONE);
                        driverResignButton.setEnabled(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Something went wrong when removing the user from the "drivers" collection
                        Log.w(TAG, "Error deleting document", e);
                        Toast.makeText(getContext(), "Failed to resign as a driver. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void checkIfUserIsDriver(){
        FirebaseUtil.currentRingDriverDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        driverUserButton.setVisibility(View.VISIBLE);
                        driverUserButton.setEnabled(true);
                        driverResignButton.setEnabled(false);
                        driverResignButton.setVisibility(View.INVISIBLE);
                    }
                    else {
                        driverUserButton.setVisibility(View.INVISIBLE);
                        driverUserButton.setEnabled(false);
                        driverResignButton.setEnabled(true);
                        driverResignButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
    public void startListening(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("drivers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                for(DocumentChange dc : snapshots.getDocumentChanges()){
                    RingDriverModel driver = dc.getDocument().toObject(RingDriverModel.class);
                    switch(dc.getType()){
                        case ADDED:
                            addMarker(driver);
                            Log.e(TAG,"location added!");
                            break;
                        case MODIFIED:
                            updateMarker(driver);
                            Log.e(TAG,"location modified!");
                            break;
                        case REMOVED:
                            removeMarker(driver);
                            Log.e(TAG,"location removed!");
                            break;
                    }
                }
            }
        });
    }
    private void addMarker(RingDriverModel driver){
        Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(driver.getLatitude(),driver.getLongitude())).title((driver.getLicensePlate()+ " | " +driver.getRingRoute())).icon(drawableToBitmap(getContext(),R.drawable.bus_logo)));
        markerDriverMap.put(marker,driver.getLicensePlate());
    }
    private void updateMarker(RingDriverModel driver){
        Log.e(TAG, "updateMarker is called successfully");
        for(Map.Entry<Marker,String> entry : markerDriverMap.entrySet()){
            Log.e(TAG,"for each is entered");
            if(entry.getValue().equals(driver.getLicensePlate())){
                Log.e(TAG,"setPosition is called successfully!");
                entry.getKey().setPosition(new LatLng(driver.getLatitude(),driver.getLongitude()));
                break;
            }
        }
    }
    private void removeMarker(RingDriverModel driver){
        for(Map.Entry<Marker,String> entry : markerDriverMap.entrySet()){
            if(entry.getValue().equals(driver.getLicensePlate())){
                entry.getKey().remove();
                markerDriverMap.remove(entry.getKey());
                break;
            }
        }
    }
    private BitmapDescriptor drawableToBitmap(Context context, int vectorDrawableID){
        Drawable drawableResource = ContextCompat.getDrawable(context, vectorDrawableID);
        drawableResource.setBounds(0,0,drawableResource.getIntrinsicWidth(),drawableResource.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawableResource.getIntrinsicWidth(),drawableResource.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawableResource.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);



    }

}