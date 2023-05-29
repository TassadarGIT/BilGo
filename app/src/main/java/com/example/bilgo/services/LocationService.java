package com.example.bilgo.services;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.webkit.PermissionRequest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.bilgo.PermissionRequestActivity;
import com.example.bilgo.R;
import com.example.bilgo.utils.FirebaseUtil;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LocationService extends Service {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int ONGOING_NOTIFICATION_ID = 1;


    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationChannel serviceChannel = new NotificationChannel(
                "ChannelID",
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);

        Notification notification = new Notification.Builder(this,"ChannelID")
                .setSmallIcon(R.drawable.baseline_ring_24)
                .setContentTitle("Location Service")
                .setContentText("This service is running in the foreground...")
                .build();
        startForeground(ONGOING_NOTIFICATION_ID, notification);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    Log.e(TAG,"ERROR LOCATION RESULT IS NULL");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.e(TAG, "Location Update: " + location.getLatitude() + ", " + location.getLongitude());
                    updateLocationInFirestore(location.getLatitude(), location.getLongitude());
                }
            }
        };

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        else{
            Intent permissionIntent = new Intent(this, PermissionRequestActivity.class);
            permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(permissionIntent);
        }


        return START_STICKY;

    }
    private void updateLocationInFirestore(double latitude,double longitude){
        Log.e(TAG, "Attempting to update location in Firestore");
        Map<String, Object> data = new HashMap<>();
        data.put("latitude", latitude);
        data.put("longitude", longitude);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseUtil.currentUserID();

        db.collection("drivers").document(currentUserId).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Location updated successfully");
                } else {
                    Log.w(TAG, "Failed to update location", task.getException());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}