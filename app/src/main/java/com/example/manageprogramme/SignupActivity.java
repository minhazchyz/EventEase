package com.example.manageprogramme;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupPassword;
    TextView loginRedirectText;
    Button signupButton;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signup_name);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.Signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        signupButton.setOnClickListener(v -> registerUser());
        loginRedirectText.setOnClickListener(v ->
                startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
    }

    private void registerUser() {
        String name = signupName.getText().toString().trim();
        String username = signupUsername.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "⚠ Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username already exists
        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(SignupActivity.this, "⚠ Username already taken!", Toast.LENGTH_SHORT).show();
                } else {
                    // Save user to Realtime Database
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("password", password);

                    databaseReference.child(username).setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(SignupActivity.this, "✅ Signup successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(SignupActivity.this, "❌ Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SignupActivity.this, "❌ Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
