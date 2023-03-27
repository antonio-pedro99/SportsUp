package com.sigma.sportsup.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sigma.sportsup.EditProfileActivity;
import com.sigma.sportsup.LoginActivity;
import com.sigma.sportsup.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import static com.google.firebase.firestore.FieldValue.arrayUnion;
import static com.google.firebase.firestore.FieldValue.delete;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String userId;

    private List<String> sportsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        Button btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Fetch user data from Firestore
        mFirestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String name = documentSnapshot.getString("name");
                    Long age = documentSnapshot.getLong("age");
                    String phone = documentSnapshot.getString("phone");
                    // Get sports list as an array of strings from Firestore
                    sportsList = (List<String>) documentSnapshot.get("sports_fav");
                    Log.d("ProfileFragment", "sportsList: " + sportsList);


                    // Update UI with user data
                    TextView tvName = view.findViewById(R.id.tvName);
                    tvName.setText(name);
                    TextView tvAge = view.findViewById(R.id.tvAge);
                    tvAge.setText(String.valueOf(age));
                    TextView tvPhone = view.findViewById(R.id.tvPhone);
                    tvPhone.setText(phone);
                    TextView tvSportsList = view.findViewById(R.id.tvSportsList);
                    //convert sportsList to a comma-separated string and set it to the TextView
                    tvSportsList.setText(TextUtils.join(", ", sportsList));

                })
                .addOnFailureListener(e -> {
                    //show a toast telling the user that something went wrong when fetching the data
                    Toast.makeText(getContext(), "Something went wrong when fetching data in onViewCreated", Toast.LENGTH_SHORT).show();

                });

        // Set up edit button click listener
        Button btnEdit = view.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
    }

    //write onResume() method to update the UI when the user returns to the ProfileFragment
    @Override
    public void onResume() {
        super.onResume();

        // Fetch user data from Firestore
        mFirestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String name = documentSnapshot.getString("name");
                    Long age = documentSnapshot.getLong("age");
                    String phone = documentSnapshot.getString("phone");
                    // Get sports list as an array of strings from Firestore
                    sportsList = (List<String>) documentSnapshot.get("sports_fav");
                    Log.d("ProfileFragment", "sportsList: " + sportsList);
                    // Update UI with user data
                    TextView tvName = getView().findViewById(R.id.tvName);
                    tvName.setText(name);
                    TextView tvAge = getView().findViewById(R.id.tvAge);
                    tvAge.setText(String.valueOf(age));
                    TextView tvPhone = getView().findViewById(R.id.tvPhone);
                    tvPhone.setText(phone);
                    TextView tvSportsList = getView().findViewById(R.id.tvSportsList);
                    //convert sportsList to a comma-separated string and set it to the TextView
                    tvSportsList.setText(TextUtils.join(", ", sportsList));
                })
                .addOnFailureListener(e -> {
                    //show a toast telling the user that something went wrong when fetching the data
                    Toast.makeText(getContext(), "Something went wrong when fetching data in onResume", Toast.LENGTH_SHORT).show();
                });
    }
}