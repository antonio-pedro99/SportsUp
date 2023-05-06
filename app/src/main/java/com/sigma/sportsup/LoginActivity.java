package com.sigma.sportsup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.sigma.sportsup.ui.chat.User;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient gsc;
    private SignInButton mGoogleSignInButton;

    private ActionBar actionBar;

    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;
    private Button signInorRegister;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get a reference to the action bar
        actionBar = getSupportActionBar();

        // Hide the action bar
        if (actionBar != null) {
            actionBar.hide();
        }

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signInorRegister = findViewById(R.id.signInorRegister);

        mAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        gsc = GoogleSignIn.getClient(this, gso);

        // Find the Google Sign-In button
        mGoogleSignInButton = (SignInButton) findViewById(R.id.google_button);

        // Set the click listener for the Google Sign-In button
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = gsc.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        signInorRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                checkForExistingUser(txt_email, txt_password);
            }
        });

        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid());



                    documentReference.get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    saveOrRestoreToken(documentReference);

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                //Toast.makeText(LoginActivity.this, "get failed with ", Toast.LENGTH_SHORT).show();
                                //show toast of error
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveUserCurrentId(String id){
        SharedPreferences.Editor editor = getSharedPreferences("USER_ID", MODE_PRIVATE).edit();
        editor.putString("currentuser", id);
        editor.apply();
    }

    private String getDeviceTokenFromSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER_ID", MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }


    private void saveOrRestoreToken(DocumentReference documentReference) {
        String token = getDeviceTokenFromSharedPreferences();
        if (token != null) {
            documentReference.update("token", token);
        } else {
            documentReference.update("token", FirebaseMessaging.getInstance().getToken().toString());
        }
        saveUserCurrentId(mAuth.getCurrentUser().getUid());
    }

    private void checkForExistingUser(String email, String password) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethods = result.getSignInMethods();
                    if (signInMethods != null && signInMethods.size() > 0) {
                        // User already exists, log in the user
                        loginUser(email, password);
                    } else {
                        // User does not exist, register the user. But first check if the email is @iiitd.ac.in
                        if(email.endsWith("@iiitd.ac.in"))
                            registerUser(email, password);
                        else
                            Toast.makeText(LoginActivity.this, "Please use your IIITD email ID", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void registerUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    addUserToDatabase(email, mAuth.getCurrentUser().getUid());
                    Toast.makeText(LoginActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    loginUser(email, password);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addUserToDatabase(String email, String uid){
        System.out.println("hello ji");
        databaseRef = FirebaseDatabase.getInstance().getReference();
        String deviceToken = FirebaseMessaging.getInstance().getToken().toString();
        String short_name = email.substring(3);
        databaseRef.child("user").child(uid).setValue(new User(email,uid, deviceToken));

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(uid);
        saveOrRestoreToken(documentReference);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Handle connection failed error
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully
            GoogleSignInAccount acct = result.getSignInAccount();


            String email = acct.getEmail();
            // Check if the email domain is @iiitd.ac.in
            if (email != null && email.endsWith("@iiitd.ac.in")) {
                // Proceed with login
                // You can get the user's name, email, profile photo URL, etc. using the following code:
                String name = acct.getDisplayName();
                String photoUrl = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : null;

                // Implement your login logic here
                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid());
                                    documentReference.get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    saveUserCurrentId(mAuth.getCurrentUser().getUid());
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    addUserToDatabase(email, mAuth.getCurrentUser().getUid());
                                                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("photoUrl", photoUrl);
                                                    Log.i("photoUrl LoginActivity", photoUrl);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            } else {
                                                Toast.makeText(LoginActivity.this, "get failed with ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
//                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
            } else {
                // Show error message to user that only @iiitd.ac.in domain is allowed
                Toast.makeText(this, "Login Failed. Only @iiitd.ac.in domain is allowed", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                gsc.signOut();
            }

        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();

        }

    }

}