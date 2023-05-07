package com.sigma.sportsup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, age, number, fav_sports;
    private FirebaseAuth mAuth;

    private ActionBar actionBar;
    private String photoUrl;
    private Button continue_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get a reference to the action bar
        actionBar = getSupportActionBar();

        // Hide the action bar
        if (actionBar != null) {
            actionBar.hide();
        }

        Intent intent = getIntent();
        //if intent contains photoUrl, then user is registering with google
        if (intent.hasExtra("photoUrl"))
            photoUrl = intent.getStringExtra("photoUrl");
        else
            //put a default photoUrl
            photoUrl="";

        Log.i("photoUrl in RegisterActivity", photoUrl);

        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        number = findViewById(R.id.number);
        fav_sports = findViewById(R.id.fav_sports);
        continue_button = findViewById(R.id.continue_button);

        mAuth = FirebaseAuth.getInstance();

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mAuth.getCurrentUser() != null){
                    HashMap<String, Object> user = new HashMap<>();

                    //if name is null or name contains non alphabetic characters(excluding whitespace) then show error
                    if(name.getText().toString().isEmpty() || !name.getText().toString().matches("[a-zA-Z\\s]+")){
                        name.setError("Please enter a valid name");
                        name.requestFocus();
                        return;
                    }
                    user.put("name", name.getText().toString());

                    //if age is null or age is not a number or age is less than 18 or age greater than 100 then show error
                    if(age.getText().toString().isEmpty() || !age.getText().toString().matches("[0-9]+") || Integer.parseInt(age.getText().toString()) < 18 || Integer.parseInt(age.getText().toString()) > 100){
                        age.setError("Please enter a valid age");
                        age.requestFocus();
                        return;
                    }
                    user.put("age", Integer.parseInt(age.getText().toString()));

                    //if number is null or  number is not 10 digits then show error
                    if(number.getText().toString().isEmpty() || !number.getText().toString().matches("[0-9]+") || number.getText().toString().length() != 10){
                        number.setError("Please enter a valid number");
                        number.requestFocus();
                        return;
                    }
                    user.put("phone", number.getText().toString());
                    user.put("photoUrl", photoUrl);

                    //if fav_sports is null or fav_sports contains non alphabetic characters(excluding whitespace and comma) then show error
                    if(fav_sports.getText().toString().isEmpty() || !fav_sports.getText().toString().matches("[a-zA-Z\\s,]+")){
                        fav_sports.setError("Please enter a valid sports");
                        fav_sports.requestFocus();
                        return;
                    }

                    String[] userSports = fav_sports.getText().toString().split(",");
                    //remove spaces
                    for(int i = 0; i < userSports.length; i++){
                        userSports[i] = userSports[i].trim();
                    }
                    user.put("sports_fav", Arrays.asList(userSports));

                    FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



                }
                else {
                    Toast.makeText(RegisterActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}