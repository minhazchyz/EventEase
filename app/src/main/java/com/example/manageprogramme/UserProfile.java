package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfile extends AppCompatActivity {

    private Button btnEditProfile, btnViewBookings, btnLogout;
    private TextInputEditText etFullName, etEmail, etPhone, etAddress;
    private String username, name, email;

    private boolean isEditing = false;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnLogout = findViewById(R.id.btnLogout);

        // 🔹 Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // 🔹 Load user info from session
        SharedPreferences sessionPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        username = sessionPrefs.getString("username", "");
        name = sessionPrefs.getString("name", "");
        email = sessionPrefs.getString("email", "");

        etFullName.setText(name);
        etEmail.setText(email);

        // Disable fields except name edit
        etFullName.setEnabled(false);
        etEmail.setEnabled(false);
        etPhone.setEnabled(false);
        etAddress.setEnabled(false);

        btnEditProfile.setOnClickListener(v -> {
            if (!isEditing) {
                etFullName.setEnabled(true);
                btnEditProfile.setText("Save");
                isEditing = true;
            } else {
                String newName = etFullName.getText().toString().trim();
                if (newName.isEmpty()) {
                    etFullName.setError("Name can't be empty");
                    return;
                }

                // 🔹 Update SharedPreferences
                sessionPrefs.edit().putString("name", newName).apply();
                name = newName;

                // 🔹 Update Firebase
                databaseReference.child(username).child("name").setValue(newName)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(UserProfile.this, "Name updated in Firebase!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(UserProfile.this, "Firebase update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                etFullName.setEnabled(false);
                btnEditProfile.setText("Edit Profile");
                isEditing = false;
            }
        });

        btnLogout.setOnClickListener(v -> {
            sessionPrefs.edit().clear().apply();
            SharedPreferences cartPrefs = getSharedPreferences("Cart_" + username, MODE_PRIVATE);
            cartPrefs.edit().clear().apply();

            Intent logoutIntent = new Intent(UserProfile.this, LoginActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;

            if (id == R.id.nav_home) {
                navIntent = new Intent(UserProfile.this, MainActivity.class);
            } else if (id == R.id.nav_cart) {
                navIntent = new Intent(UserProfile.this, Cart.class);
                navIntent.putExtra("username", username);
            } else {
                return false;
            }

            navIntent.putExtra("username", username);
            navIntent.putExtra("name", name);
            navIntent.putExtra("email", email);
            startActivity(navIntent);
            return true;
        });
    }
}
