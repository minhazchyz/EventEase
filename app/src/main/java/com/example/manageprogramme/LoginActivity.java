package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String savedUsername = prefs.getString("username", null);
        String savedName = prefs.getString("name", null);
        String savedEmail = prefs.getString("email", null);

        if (savedUsername != null) {
            // User is already logged in → go to MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", savedUsername);
            intent.putExtra("name", savedName);
            intent.putExtra("email", savedEmail);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        loginButton.setOnClickListener(v -> loginUser());
        signupRedirectText.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    private void loginUser() {
        String username = loginUsername.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (username.isEmpty()) {
            loginUsername.setError("Username can't be empty");
            return;
        }
        if (password.isEmpty()) {
            loginPassword.setError("Password can't be empty");
            return;
        }

        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordFromDB = snapshot.child("password").getValue(String.class);

                    if (passwordFromDB != null && passwordFromDB.equals(password)) {
                        String nameFromDB = snapshot.child("name").getValue(String.class);
                        String emailFromDB = snapshot.child("email").getValue(String.class);

                        Toast.makeText(LoginActivity.this, "✅ Login successful!", Toast.LENGTH_SHORT).show();

                        // ✅ Save session
                        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("username", username);
                        editor.putString("name", nameFromDB);
                        editor.putString("email", emailFromDB);
                        editor.apply();

                        // ✅ Go to MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("username", username);
                        intent.putExtra("email", emailFromDB);
                        startActivity(intent);
                        finish();
                    } else {
                        loginPassword.setError("Invalid password");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginUsername.setError("User does not exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
