package com.example.manageprogramme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    // Edge-to-edge padding
    private static WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Edge-to-edge setup
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), SignupActivity::onApplyWindowInsets);

        // Initialize views
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password); // Fixed missing initialization
        signupButton = findViewById(R.id.Signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // Firebase setup
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        // Signup button click
        signupButton.setOnClickListener(view -> {
            String name = signupName.getText().toString().trim();
            String email = signupEmail.getText().toString().trim();
            String username = signupUsername.getText().toString().trim();
            String password = signupPassword.getText().toString().trim();

            // Input validation
            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create helper object and save to Firebase
            Helper helperClass = new Helper(name, email, username, password);
            reference.child(username).setValue(helperClass)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                        startActivity(intent);

                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(SignupActivity.this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        // Redirect to login
        loginRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
            startActivity(intent);
        });
    }
}
