package com.sigma.sportsup.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sigma.sportsup.R;

import java.util.Objects;

public class EditProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String userId;
    private EditText etPhone, etSportsList;
//    private List<String> sportsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.activity_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etPhone = view.findViewById(R.id.etPhone);

//        etSportsList = view.findViewById(R.id.etSportsList);

        // Fetch user data from Firestore
        mFirestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String phone = documentSnapshot.getString("phone");
                    String[] userSports = {"",""};
//                    sportsList = new ArrayList<>(Arrays.asList(userSports));

                    // Set the initial values of the EditText fields to the current values in Firestore
                    etPhone.setText(phone);
//                    etSportsList.setText(TextUtils.join(", ", sportsList));
                })
                .addOnFailureListener(e -> {
                    // Show a toast message if there was an error fetching the data from Firestore
                    Toast.makeText(getContext(), "Could not fetch user data from database", Toast.LENGTH_SHORT).show();
                });

        // Set up save button click listener
        Button btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            // Get the updated phone number and sports list from the EditText fields
            String phone = etPhone.getText().toString().trim();
            String sports = etSportsList.getText().toString().trim();

            // Split the sports list string into an array of strings
            String[] sportsArray = sports.split(",");

            // Convert the array of strings to a list of strings
//            sportsList = new ArrayList<>();
//            for (String sport : sportsArray) {
//                String trimmed = sport.trim();
//                if (!TextUtils.isEmpty(trimmed)) {
//                    sportsList.add(trimmed);
//                }
//            }

            // Update the user's phone number and sports list in Firestore
            DocumentReference userRef = mFirestore.collection("users").document(userId);
            userRef.update("phone", phone);
//            userRef.update("sports_fav", sportsList);

            // Navigate back to the profile fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
}