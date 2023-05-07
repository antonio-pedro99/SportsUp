package com.sigma.sportsup.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String userId;
    private List<String> sportsList;
    private GoogleSignInClient gsc;
    private CircleImageView ivProfilePic;
    private Button btnSave;
    TextInputEditText etAge;
    TextInputEditText tvSportsList;
    TextInputEditText tvPhone;
    ImageView ageEditIcon;
    ImageView ageEditIcon2;
    ImageView ageEditIcon3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
//        Button btnLogout = view.findViewById(R.id.btn_logout);
        // Configure sign-in to request the user's ID, email address, and basic profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        gsc = GoogleSignIn.getClient(getActivity(), gso);
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mAuth.getCurrentUser() != null) {
//                    mAuth.signOut();
//                    gsc.signOut();
//                }
//
//                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        etAge = view.findViewById(R.id.etAge);
        ageEditIcon = view.findViewById(R.id.ageEditIcon);
        etAge.setEnabled(false);

        // show/hide the edit icon based on the EditText's enabled state
        ageEditIcon.setVisibility(etAge.isEnabled() ? View.GONE : View.VISIBLE);

        // set a click listener to toggle the EditText's enabled state and hide/show the edit icon
        ageEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAge.setEnabled(!etAge.isEnabled());
                ageEditIcon.setVisibility(etAge.isEnabled() ? View.GONE : View.VISIBLE);
            }
        });

        // set a focus change listener to show the edit icon when the EditText loses focus
        etAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ageEditIcon.setVisibility(View.VISIBLE);
                    etAge.setEnabled(false);
                }
            }
        });

        tvPhone = view.findViewById(R.id.tvPhone);
        ageEditIcon2 = view.findViewById(R.id.ageEditIcon2);

        tvPhone.setEnabled(false);
        ageEditIcon2.setVisibility(tvPhone.isEnabled() ? View.GONE : View.VISIBLE);

        ageEditIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPhone.setEnabled(!tvPhone.isEnabled());
                ageEditIcon2.setVisibility(tvPhone.isEnabled() ? View.GONE : View.VISIBLE);
            }
        });

        tvPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ageEditIcon2.setVisibility(View.VISIBLE);
                    tvPhone.setEnabled(false);
                }
            }
        });

        tvSportsList = view.findViewById(R.id.tvSportsList);
        ageEditIcon3 = view.findViewById(R.id.ageEditIcon3);

        tvSportsList.setEnabled(false);
        ageEditIcon3.setVisibility(tvSportsList.isEnabled() ? View.GONE : View.VISIBLE);

        ageEditIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSportsList.setEnabled(!tvSportsList.isEnabled());
                ageEditIcon3.setVisibility(tvSportsList.isEnabled() ? View.GONE : View.VISIBLE);
            }
        });

        tvSportsList.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ageEditIcon3.setVisibility(View.VISIBLE);
                    tvSportsList.setEnabled(false);
                }
            }
        });

        btnSave = view.findViewById(R.id.save_edits);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String age = etAge.getText().toString();
                String phone = tvPhone.getText().toString();
                String sports = tvSportsList.getText().toString();

                if (age.isEmpty()) {
                    etAge.setError("Age is required");
                    etAge.requestFocus();
                    return;
                }

                if (phone.isEmpty()) {
                    tvPhone.setError("Phone is required");
                    tvPhone.requestFocus();
                    return;
                }

                if (sportsList.isEmpty()) {
                    tvSportsList.setError("Sports List is required");
                    tvSportsList.requestFocus();
                    return;
                }

                if (!phone.matches("[0-9]+") || !(phone.length() == 10)) {
                    //make a toast to show the please enter valid phone number
                    tvPhone.setError("Please enter a valid phone number");
                    tvPhone.requestFocus();
                    return;
                }

                if (!sports.matches("[a-zA-Z,\\s]+")) {
                    // Sports list is invalid
                    tvSportsList.setError("Please enter a valid sports list");
                    tvSportsList.requestFocus();
                    return;
                }

                //if age is not a number or is less than 10 or greater than 100 then show error
                if (!age.matches("[0-9]+") || Integer.parseInt(age) < 10 || Integer.parseInt(age) > 100) {
                    etAge.setError("Please enter a valid age");
                    etAge.requestFocus();
                    return;
                }

                String[] sportsArray = sports.split(",");
                sportsList = new ArrayList<>();
                for (String sport : sportsArray) {
                    String trimmed = sport.trim();
                    if (!TextUtils.isEmpty(trimmed)) {
                        sportsList.add(trimmed);
                    }
                }

                // Update the user's phone number and sports list in Firestore
//                DocumentReference userRef = mFirestore.collection("users").document(userId);
//                userRef.update("phone", phone);
//                userRef.update("sports_fav", sportsList);
//                userRef.update("age", Long.parseLong(age));

                // Update user data in Firestore
                mFirestore.collection("users")
                        .document(userId)
                        .update(
                                "age", Long.parseLong(age),
                                "phone", phone,
                                "sports_fav", sportsList
                        )
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_LONG).show();
                            etAge.setEnabled(false);
                            ageEditIcon.setVisibility(etAge.isEnabled() ? View.GONE : View.VISIBLE);
                            tvPhone.setEnabled(false);
                            ageEditIcon2.setVisibility(tvPhone.isEnabled() ? View.GONE : View.VISIBLE);
                            tvSportsList.setEnabled(false);
                            ageEditIcon3.setVisibility(tvSportsList.isEnabled() ? View.GONE : View.VISIBLE);
                        })
                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error updating profile", Toast.LENGTH_LONG).show());
            }
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
                    TextView tvNameLayout = getView().findViewById(R.id.tvName);
                    tvNameLayout.setText(String.format("%s", name));

                    TextInputLayout tvAgeLayout = getView().findViewById(R.id.tvAge);
                    TextInputEditText tvAgeEditText = (TextInputEditText) tvAgeLayout.getEditText();
                    tvAgeEditText.setText(String.format("%s", age));


                    TextInputEditText tvPhoneEditText = getView().findViewById(R.id.tvPhone);
                    tvPhoneEditText.setText(String.format("%s", phone));

                    TextInputEditText tvSportsListEditText = getView().findViewById(R.id.tvSportsList);
                    tvSportsListEditText.setText(String.format("%s", TextUtils.join(", ", sportsList)));

                    ivProfilePic = getView().findViewById(R.id.ivProfilePic);
                    String profilePicUrl = documentSnapshot.getString("photoUrl");
                    //if profile pic url is not empty string, load the image into the ImageView
                    if (!TextUtils.isEmpty(profilePicUrl)) {
                        profilePicUrl.replaceAll("s96-c", "s384-c");  //
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