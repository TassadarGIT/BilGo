package com.example.bilgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    ProfileFragment profileFragment;
    LeaderboardFragment leaderboardFragment;
    //TaxiFragment taxiFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileFragment = new ProfileFragment();
        leaderboardFragment = new LeaderboardFragment();
        //taxiFragment = new TaxiFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
                } else if(item.getItemId() == R.id.menu_leaderboard) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, leaderboardFragment).commit();
                } //else if(item.getItemId() == R.id.menu_taxi) {
                    //getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, taxiFragment).commit();
                //}
                return false;
            }
        });
    }
}