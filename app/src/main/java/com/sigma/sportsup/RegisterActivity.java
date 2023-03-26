package com.sigma.sportsup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

    private Button continue_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                    user.put("name", name.getText().toString());
                    user.put("age", Integer.parseInt(age.getText().toString()));
                    user.put("phone", number.getText().toString());
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