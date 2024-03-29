package com.example.bilgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    ProfileFragment profileFragment;
    LeaderboardFragment leaderboardFragment;
    TaxiFragment taxiFragment;
    MapFragment mapFragment;
    HitchhikingFragment hitchhikingFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileFragment = new ProfileFragment();
        leaderboardFragment = new LeaderboardFragment();
        taxiFragment = new TaxiFragment();
        mapFragment = new MapFragment();
        hitchhikingFragment = new HitchhikingFragment();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, mapFragment).commit(); // starts the app with ring tracker page


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
                } else if(item.getItemId() == R.id.menu_leaderboard) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, leaderboardFragment).commit();
                } else if(item.getItemId() == R.id.menu_taxi) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, taxiFragment).commit();
                } else if(item.getItemId() == R.id.menu_ring) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, mapFragment).commit();
                } else if(item.getItemId() == R.id.menu_hitch) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, hitchhikingFragment).commit();
                }

                return false;
            }
        });
    }
}