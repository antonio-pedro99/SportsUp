package com.sigma.sportsup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sigma.sportsup.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String userId;
    private EditText etPhone, etSportsList;
    private List<String> sportsList;

    private Button btnSave;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        etPhone = findViewById(R.id.etPhone);
        etSportsList = findViewById(R.id.etSportsList);


        // Fetch user data from Firestore
        mFirestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String phone = documentSnapshot.getString("phone");
                    sportsList = (List<String>) documentSnapshot.get("sports_fav");

                    // Set the initial values of the EditText fields to the current values in Firestore
                    etPhone.setText(phone);
                    etSportsList.setText(TextUtils.join(", ", sportsList));
                })
                .addOnFailureListener(e -> {
                    // Show a toast message if there was an error fetching the data from Firestore
                    Toast.makeText(this, "Could not fetch user data from database", Toast.LENGTH_SHORT).show();
                });

        // Set up save button click listener
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            // Get the updated phone number and sports list from the EditText fields
            String phone = etPhone.getText().toString().trim();
            String sports = etSportsList.getText().toString().trim();
            Log.d("EditProfileActivity", "sports: " + sports);

            if (!phone.matches("[0-9]+") || !(phone.length() == 10)) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!sports.matches("[a-zA-Z,\\s]+")) {
                // Sports list is invalid
                Toast.makeText(this, "Please enter a valid sports list with sports names separated by commas", Toast.LENGTH_SHORT).show();
                return;
            }

            // Split the sports list string into an array of strings
            String[] sportsArray = sports.split(",");
            Log.d("EditProfileActivity", "sportsArray: " + Arrays.toString(sportsArray));

//             Convert the array of strings to a list of strings
            sportsList = new ArrayList<>();
            for (String sport : sportsArray) {
                String trimmed = sport.trim();
                if (!TextUtils.isEmpty(trimmed)) {
                    sportsList.add(trimmed);
                }
            }

            // Update the user's phone number and sports list in Firestore
            DocumentReference userRef = mFirestore.collection("users").document(userId);
            userRef.update("phone", phone);
            userRef.update("sports_fav", sportsList);
            finish();
        });
    }
}