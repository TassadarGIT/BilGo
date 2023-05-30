package com.example.bilgo;

import android.util.Base64;
import android.util.Log;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.net.Uri;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bilgo.model.UserModel;
import com.example.bilgo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import static android.content.ContentValues.TAG;


public class LoginUserActivity extends AppCompatActivity {

    EditText nameInput;
    EditText surnameInput;
    ImageButton ppSelectionButton;
    Button letMeInBtn;
    ProgressBar progressBar;
    String name;
    String surname;
    String phoneNumber;
    String gender;
    String dateOfBirth;
    UserModel userModel;
    private ActivityResultLauncher<String> mGetContent;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_username);

        nameInput = findViewById(R.id.login_name);
        surnameInput = findViewById(R.id.login_surname_editText);
        letMeInBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        ppSelectionButton = findViewById(R.id.pp_selection_button);

        phoneNumber = getIntent().getExtras().getString("phone");

        getUsername();

        //Birthday Picker
        setupBDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());


        letMeInBtn.setOnClickListener((v) -> {
            setUsername();
        });



        //Gender Picker
        Spinner genderSelector = findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSelector.setAdapter(adapter);
        genderSelector.setSelection(0);

        genderSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    String selectedGender = parent.getItemAtPosition(position).toString();
                    // Do something with the selected item
                    gender = genderSelector.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + selectedGender, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        //Profile Picture Selector

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        uploadImageToFirebase(uri);
                    }
                });


        ppSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

    }

    private void uploadImageToFirebase(Uri imageUri) {
        String userId = FirebaseUtil.currentUserID();
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                }
            }
        });

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytesFromInputStream(inputStream);

            // Initialize Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images/" + userId);

            UploadTask uploadTask = imagesRef.putBytes(imageBytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get the download URL
                    imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Now uri is the download URL that you can store in Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userRef = db.collection("users").document(userId);
                            userRef.update("profileImageUri", uri.toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            userModel.setProfilePictureLink(uri.toString());

                                            // Load image into ImageButton
                                            Glide.with(LoginUserActivity.this)
                                                    .load(uri)
                                                    .into(ppSelectionButton);

                                            Log.d(TAG, "URI stored successfully!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error storing URI", e);
                                        }
                                    });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e(TAG, "Error uploading image", exception);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch the image picker
                mGetContent.launch("image/*");
            } else {
                // Permission denied, show a toast or handle the error
                Toast.makeText(this, "Permission denied. Unable to pick an image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);

                    if(userModel != null) {
                        Intent intent = new Intent(LoginUserActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    void setUsername() {
        name = nameInput.getText().toString();
        surname = surnameInput.getText().toString();

        if(name.isEmpty() || name.length()<3) {
            nameInput.setError("Name length should at least be 3 characters!");
            return;
        }
        if(surname.isEmpty() || surname.length()<3) {
            surnameInput.setError("Surname length should at least be 3 characters!");
            return;
        }
        setInProgress(true);

        String uniqueID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(userModel!=null) {
            userModel.setName(name);
            userModel.setSurname(surname);
            userModel.setPhone(phoneNumber);
            userModel.setGender(gender);
            userModel.setDateOfBirth(dateOfBirth);
            userModel.setUserId(uniqueID);
        } else {
            userModel = new UserModel(phoneNumber, name, surname, gender, dateOfBirth, Timestamp.now());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()) {
                    Intent intent = new Intent(LoginUserActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month, year);
    }

    private void setupBDatePicker()
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

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }
    private String makeDateString(int day, int month, int year)
    {
        return day + " / " + month + " / " + year;
    }
    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }
    void setInProgress(boolean inProgress) {
        if(inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

}