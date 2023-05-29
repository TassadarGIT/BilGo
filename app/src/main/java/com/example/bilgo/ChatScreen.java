package com.example.bilgo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bilgo.model.MessageModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;

    private List<MessageModel> messageList;
    private MessageAdapter messageAdapter;
    private String currentUserId;
    private String currentUserName;
    private String groupId;

    private CollectionReference messagesCollection;
    private ListenerRegistration messagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("groupId")) {
            groupId = getIntent().getStringExtra("groupId");
        } else {
            // Handle the case when groupId is not provided
        }

        // Initialize Firestore and the messages collection reference
        messagesCollection = FirebaseFirestore.getInstance().collection("messages");

        // Initialize views
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        editTextMessage = findViewById(R.id.edit_text_message);
        buttonSend = findViewById(R.id.button_send);

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        // Get the current user ID
        currentUserId = getCurrentUserID();
        currentUserName();

        // Send button click listener
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = editTextMessage.getText().toString().trim();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                // Create a new message object
                MessageModel message = new MessageModel();
                message.setContent(messageContent);
                message.setSenderId(currentUserId);
                message.setTimestamp(Timestamp.now());
                message.setSenderName(currentUserName);
                // Set other message properties if necessary

                // Store the message in Firestore
                storeMessageInFirestore(message);

                // Clear the input field after sending the message
                editTextMessage.setText("");
            }
        });

        // Load group chat messages
        loadGroupChatMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove the snapshot listener to avoid listening for updates when the activity is stopped
        if (messagesListener != null) {
            messagesListener.remove();
        }
    }

    private String getCurrentUserID() {
        // You can implement this method to retrieve the current user ID from your authentication system
        // For example, using Firebase Authentication:
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            // Handle the case when the user is not logged in or authentication is required
            // Return a default value or show an error message
            return "";
        }
    }

    private void currentUserName() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // Get a reference to the Firestore collection where users are stored
        CollectionReference usersCollectionRef = FirebaseFirestore.getInstance().collection("users");

    // Query the collection to find the document of the current user
        Query query = usersCollectionRef.whereEqualTo("id", currentUser.getUid());

    // Execute the query
        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    // Check if there is a matching document for the current user
                    if (!querySnapshot.isEmpty()) {
                        // Access the first document (assuming there is only one match)
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                        // Access the fields of the document
                        currentUserName = doc.getString("name");

                    } else {
                        System.out.println("No matching document found for the current user.");
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error getting documents: " + e.getMessage());
                });
    }


    private void loadGroupChatMessages() {
        messagesListener = messagesCollection
                .whereEqualTo("groupId", groupId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot querySnapshot, @javax.annotation.Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Error occurred while listening for new messages
                            Toast.makeText(ChatScreen.this, "Failed to listen for messages.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        messageList.clear();

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // Convert each document to a MessageModel object
                            MessageModel message = document.toObject(MessageModel.class);
                            messageList.add(message);
                        }

                        // Notify the adapter that the message list has changed
                        messageAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void storeMessageInFirestore(MessageModel message) {
        message.setGroupId(groupId); // Set the groupId for the message

        messagesCollection
                .add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // The message was successfully stored in Firestore
                        // Handle any success actions or notifications
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while storing the message in Firestore
                        // Handle the failure and display an error message or perform appropriate actions
                    }
                });
    }
}
