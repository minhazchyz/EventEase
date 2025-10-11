package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BirthdayPage extends AppCompatActivity {

    private Button btnKids, btnTeen, btnAdult, btnLuxury, btnFamily, btnTheme;

    // ✅ User data
    private String username;
    private String name;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_page);

        // ✅ Receive user data from MainActivity
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
        }

        // Buttons initialize
        btnKids = findViewById(R.id.btn_kids);
        btnTeen = findViewById(R.id.btn_teen);
        btnAdult = findViewById(R.id.btn_adult);
        btnLuxury = findViewById(R.id.btn_luxury);
        btnFamily = findViewById(R.id.btn_family);
        btnTheme = findViewById(R.id.btn_theme);

        // Setup all packages
        setupButton(btnKids, "Kids Birthday Party", 500);
        setupButton(btnTeen, "Teen Birthday Bash", 800);
        setupButton(btnAdult, "Adult Celebration", 1200);
        setupButton(btnLuxury, "Luxury Package", 2000);
        setupButton(btnFamily, "Family Special", 1500);
        setupButton(btnTheme, "Theme Birthday", 1000);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;

            if (id == R.id.nav_home) {
                navIntent = new Intent(BirthdayPage.this, MainActivity.class);
            } else if (id == R.id.nav_profile) {
                navIntent = new Intent(BirthdayPage.this, UserProfile.class);
            } else if (id == R.id.nav_cart) {
                navIntent = new Intent(BirthdayPage.this, Cart.class);
            } else {
                return false;
            }

            // ✅ Pass user data
            navIntent.putExtra("username", username);
            navIntent.putExtra("name", name);
            navIntent.putExtra("email", email);
            startActivity(navIntent);
            return true;
        });
    }

    private void setupButton(Button button, String packageName, int price) {
        button.setOnClickListener(new View.OnClickListener() {
            private boolean booked = false;

            @Override
            public void onClick(View view) {
                // 🔹 Use user-specific SharedPreferences
                SharedPreferences prefs = getSharedPreferences("Cart_" + username, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if (!booked) {
                    // Add this package to cart data
                    String oldData = prefs.getString("cart_items", "");
                    String newItem = packageName + "," + price + ";";
                    editor.putString("cart_items", oldData + newItem);
                    editor.apply();

                    // Update UI
                    button.setText("View Book");
                    booked = true;

                    Toast.makeText(BirthdayPage.this, packageName + " added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    // Open Cart page
                    Intent intent = new Intent(BirthdayPage.this, Cart.class);
                    // ✅ Pass user data
                    intent.putExtra("username", username);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });
    }
}
