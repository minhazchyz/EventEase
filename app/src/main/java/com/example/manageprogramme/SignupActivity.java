package com.example.manageprogramme;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.Signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(v -> registerUser());

        loginRedirectText.setOnClickListener((View view) -> startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
    }

    private void registerUser() {
        String name = signupName.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String username = signupUsername.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "⚠ Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "⚠ Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "⚠ Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verifyTask -> {
                                        if (verifyTask.isSuccessful()) {
                                            // ✅ Save user data to Firestore
                                            saveUserToFirestore(user.getUid(), name, email, username);

                                            Toast.makeText(this,
                                                    "✅ Verification email sent to " + email,
                                                    Toast.LENGTH_LONG).show();

                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                            finish();

                                        } else {
                                            Toast.makeText(this,
                                                    String.format("❌ Failed to send verification email: %s", Objects.requireNonNull(verifyTask.getException()).getMessage()),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this,
                                "❌ Sign Up Failed: " + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String uid, String name, String email, String username) {
        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);
        user.put("name", name);
        user.put("username", username);
        user.put("email", email);
        user.put("emailVerified", false);

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    // no toast needed, already handled above
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "❌ Failed to save user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
