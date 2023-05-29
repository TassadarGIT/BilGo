package com.example.bilgo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bilgo.model.RingDriverModel;
import com.example.bilgo.model.UserModel;
import com.example.bilgo.services.LocationService;
import com.example.bilgo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.Array;

public class DriverRegisterFragment extends Fragment {

    Button driverGoBackButton;
    String licensePlate;

    String route;
    Button driverRegisterButton;
    EditText licensePlateInputText;

    RingDriverModel driverModel;
    public DriverRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        licensePlateInputText = view.findViewById(R.id.licensePlate_editText);
        driverGoBackButton = view.findViewById(R.id.driver_go_back_button);
        driverGoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new MapFragment());
            }
        });
        driverRegisterButton = view.findViewById(R.id.driver_register_button);
        driverRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDriverData();
            }
        });
        Spinner routeSelector = view.findViewById(R.id.route_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.route_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSelector.setAdapter(adapter);
        routeSelector.setSelection(0);

        routeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position != 0){
                    route = routeSelector.getItemAtPosition(position).toString();
                    Toast.makeText(routeSelector.getContext(),"Selected Route: "+route, Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Another interface callback
            }
        });
    }

    void setDriverData(){

        licensePlate = licensePlateInputText.getText().toString();
        if(licensePlate.isEmpty() || licensePlate.length()<7 || licensePlate.length()>12){
            licensePlateInputText.setError("Invalid License Plate");
            return;
        }
        if(driverModel != null){
            driverModel.setLicensePlate(licensePlate);
            driverModel.setRingRoute(route.toString());
            driverModel.setLatitude(0.0);
            driverModel.setLongitude(0.0);
            driverModel.setTimestamp(System.currentTimeMillis());
        } else{
            driverModel = new RingDriverModel(licensePlate,route,0.0,0.0,System.currentTimeMillis());
        }

        FirebaseUtil.currentRingDriverDetails().set(driverModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(getContext(), LocationService.class);
                    getContext().startForegroundService(intent);
                    Toast.makeText(getContext(), "You have successfully registered as a driver.", Toast.LENGTH_SHORT).show();
                    changeFragment(new MapFragment());
                }
            }
        });
    }
}