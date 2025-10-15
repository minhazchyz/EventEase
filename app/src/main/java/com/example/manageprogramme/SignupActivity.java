package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;

    FirebaseAuth auth;
    private boolean isSubmitting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signup_name);
        signupUsername = findViewById(R.id.signup_username);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        auth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(v -> registerUser());
        loginRedirectText.setOnClickListener(v ->
                startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
    }

    private void setLoading(boolean loading) {
        isSubmitting = loading;
        signupButton.setEnabled(!loading);
        signupButton.setText(loading ? "Please wait..." : getString(R.string.action_signup));
    }

    private void registerUser() {
        if (isSubmitting) return;

        final String name = signupName.getText().toString().trim();
        final String username = signupUsername.getText().toString().trim();
        final String email = signupEmail.getText().toString().trim();
        final String password = signupPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "⚠ Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Gmail-only regex
        String emailPattern = "^[A-Za-z0-9._%+-]+@gmail\\.com$";
        if (!email.matches(emailPattern)) {
            Toast.makeText(this, "⚠ Please enter a valid Gmail address", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Strong password regex
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";
        if (!password.matches(passwordPattern)) {
            Toast.makeText(this, "⚠ Password must be at least 6 chars, include upper, lower, digit",
                    Toast.LENGTH_LONG).show();
            return;
        }

        setLoading(true);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser fUser = auth.getCurrentUser();
                    if (fUser == null) {
                        setLoading(false);
                        Toast.makeText(this, "❌ Signup failed: user null", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // ✅ Save pending profile locally
                    String uid = fUser.getUid();
                    SharedPreferences sp = getSharedPreferences("PendingProfile", MODE_PRIVATE);
                    sp.edit()
                            .putString("uid", uid)
                            .putString("name", name)
                            .putString("username", username)
                            .putString("email", email)
                            .apply();

                    // ✅ Send verification email
                    fUser.sendEmailVerification()
                            .addOnSuccessListener(v -> {
                                Toast.makeText(this,
                                        "✅ Verify the link sent to " + email,
                                        Toast.LENGTH_LONG).show();
                                auth.signOut();
                                setLoading(false);
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                setLoading(false);
                                Toast.makeText(this,
                                        "❌ Could not send verification email: " + e.getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this,
                            "❌ Auth failed: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
