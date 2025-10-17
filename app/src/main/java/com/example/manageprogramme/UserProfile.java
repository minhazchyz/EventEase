package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class UserProfile extends AppCompatActivity {

    private Button btnViewBookings, btnLogout;
    private TextInputEditText etEmail;

    private String username, email;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnLogout = findViewById(R.id.btnLogout);
        auth = FirebaseAuth.getInstance();

        // Load session data
        SharedPreferences sessionPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        username = sessionPrefs.getString("username", "");
        email = sessionPrefs.getString("email", "");

        etEmail.setText(email);
        etEmail.setEnabled(false);

        // View Bookings button
        btnViewBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewBooking.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            sessionPrefs.edit().clear().apply();

            // Clear cart data
            SharedPreferences cartPrefs = getSharedPreferences("Cart_" + username, MODE_PRIVATE);
            cartPrefs.edit().clear().apply();

            Intent logoutIntent = new Intent(this, LoginActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });

        // ---- Bottom Navigation ----
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // ✅ highlight profile icon

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent = null;

            if (id == R.id.nav_home) {
                navIntent = new Intent(this, MainActivity.class);
            } else if (id == R.id.nav_cart) {
                navIntent = new Intent(this, Cart.class);
            } else if (id == R.id.nav_profile) {
                return true; // Already on profile
            }

            if (navIntent != null) {
                navIntent.putExtra("username", username);
                navIntent.putExtra("email", email);
                startActivity(navIntent);
                overridePendingTransition(0, 0); // No animation between tabs
            }
            return true;
        });
    }
}
