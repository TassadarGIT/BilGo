package com.example.bilgo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.bilgo.model.UserModel;
import com.example.bilgo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    private EditText profileNameInput;
    private EditText profileSurnameInput;
    private EditText profilePhoneInput;
    private String phoneNumber;
    private String dateOfBirth;
    private Button profileLogoutBtn;
    private Button profileSaveBtn;
    private UserModel userModel;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileNameInput = view.findViewById(R.id.profile_name_edit);
        profileSurnameInput = view.findViewById(R.id.profile_surname_edit);
        profilePhoneInput = view.findViewById(R.id.profile_phone_edit);
        profileLogoutBtn = view.findViewById(R.id.profile_logout_btn);
        profileSaveBtn = view.findViewById(R.id.profile_save_btn);

        setupBDatePicker(view.getContext());
        dateButton = view.findViewById(R.id.datePickerButton);

        getUser();

        dateButton.setText(dateOfBirth);

        profileSaveBtn.setOnClickListener((v) -> {
            saveChanges();
        });
    }

    void getUser() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);

                    if(userModel != null) {
                        profileNameInput.setText(userModel.getName());
                        profileSurnameInput.setText(userModel.getSurname());
                        phoneNumber = userModel.getPhone();
                        profilePhoneInput.setText(phoneNumber);
                        dateButton.setText(userModel.getDateOfBirth());
                    }
                }
            }
        });
    }

    void saveChanges() {
        userModel.setName(profileNameInput.getText().toString());
        userModel.setSurname(profileSurnameInput.getText().toString());
        userModel.setDateOfBirth(dateOfBirth);

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                }
            }
        });
    }

    private void setupBDatePicker(Context context)
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateOfBirth = date;
                dateButton.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(context, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year)
    {
        return day + " / " + month + " / " + year;
    }

}
