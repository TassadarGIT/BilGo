package com.example.bilgo;

import android.net.Uri;
import android.util.Log;
import android.widget.*;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bilgo.model.UserModel;
import com.example.bilgo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class ProfileFragment extends Fragment {

    private EditText profileNameInput;
    private EditText profileSurnameInput;
    private TextView profilePhone;
    private TextView profileBirthdate;
    private ImageView profilePicture;
    private Button profilePictureButton;
    private String phoneNumber;
    private Button profileLogoutBtn;
    private Button profileSaveBtn;
    private UserModel userModel;
    private TextView profileRank;

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
        profilePhone = view.findViewById(R.id.profile_phoneNumber);
        profileBirthdate = view.findViewById(R.id.profile_birthday);
        profileLogoutBtn = view.findViewById(R.id.profile_logout_btn);
        profileSaveBtn = view.findViewById(R.id.profile_save_btn);
        profileRank = view.findViewById(R.id.rank);

        profilePicture = view.findViewById(R.id.profilePicture);
        profilePictureButton = view.findViewById(R.id.pp_change_button);

        getUser();

        profileLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getActivity(), SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        profileSaveBtn.setOnClickListener((v) -> {
            saveChanges();
        });

        profilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGallery();
            }
        });
    }

    void loadProfileImage(String profilePictureLink) {
        RequestOptions requestOptions = new RequestOptions()
                .circleCrop()
                .override(128, 128);
        Glide.with(this)
                .load(profilePictureLink)
                .apply(requestOptions)
                .into(profilePicture);
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

                        if (profilePhone != null) {
                            profilePhone.setText(phoneNumber);
                        } else {
                            Log.e("ProfileFragment", "TextView profilePhone is null");
                        }

                        if (profileBirthdate != null) {
                            profileBirthdate.setText(userModel.getDateOfBirth());
                        } else {
                            Log.e("ProfileFragment", "TextView profileBirthdate is null");
                        }

                        if(userModel.getProfilePictureLink() != null) {
                            loadProfileImage(userModel.getProfilePictureLink());
                        }

                        if (profileRank != null) {
                            profileRank.setText("Rank: " + userModel.getRank());
                        } else {
                            Log.e("ProfileFragment", "TextView profileRank is null");
                        }
                    }
                }
            }
        });
    }

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    if (uri != null) {
                        uploadImageToFirebase(uri);
                    }
                }
            });

    private void pickImageFromGallery() {
        mGetContent.launch("image/*");
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
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
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
                                            Glide.with(getActivity())
                                                    .load(uri)
                                                    .into(profilePicture);

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

    void saveChanges() {
        userModel.setName(profileNameInput.getText().toString());
        userModel.setSurname(profileSurnameInput.getText().toString());
        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                }
            }
        });
    }
}