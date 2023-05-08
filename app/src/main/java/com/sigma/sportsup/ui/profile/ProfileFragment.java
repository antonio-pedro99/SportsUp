package com.sigma.sportsup.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sigma.sportsup.EditProfileActivity;
import com.sigma.sportsup.LoginActivity;
import com.sigma.sportsup.R;

import java.text.MessageFormat;
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

    private GoogleSignInClient gsc;

    private ImageView ivProfilePic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        Button btnLogout = view.findViewById(R.id.btn_logout);
        // Configure sign-in to request the user's ID, email address, and basic profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        setHasOptionsMenu(true);
        // Build a GoogleSignInClient with the options specified by gso.
        gsc = GoogleSignIn.getClient(getActivity(), gso);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    gsc.signOut();
                }

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout_item:
                //logout
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    gsc.signOut();
                }

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

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
                    tvName.setText(String.format("Name: %s", name));
                    TextView tvAge = getView().findViewById(R.id.tvAge);
                    tvAge.setText(MessageFormat.format("Age: {0}", String.valueOf(age)));
                    TextView tvPhone = getView().findViewById(R.id.tvPhone);
                    tvPhone.setText(MessageFormat.format("Phone: {0}", phone));
                    TextView tvSportsList = getView().findViewById(R.id.tvSportsList);
                    //convert sportsList to a comma-separated string and set it to the TextView
                    tvSportsList.setText(MessageFormat.format("Favourite Sports: {0}", TextUtils.join(", ", sportsList)));
                    ivProfilePic = getView().findViewById(R.id.ivProfilePic);
                    String profilePicUrl = documentSnapshot.getString("photoUrl");
                    if (profilePicUrl != null) {
                        profilePicUrl.replaceAll("s96-c", "s384-c");
                        Glide.with(requireContext())
                                .load(profilePicUrl)
                                .into(ivProfilePic);
                    }
                })
                .addOnFailureListener(e -> {
                    //show a toast telling the user that something went wrong when fetching the data
                    Toast.makeText(getContext(), "Something went wrong when fetching data in onResume", Toast.LENGTH_SHORT).show();
                });
    }
}