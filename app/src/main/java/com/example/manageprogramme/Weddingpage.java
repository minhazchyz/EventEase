package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Weddingpage extends AppCompatActivity {

    private Button btnRoyal, btnClassic, btnPremium, btnLuxury;

    // ✅ User data
    private String username;
    private String name;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weddingpage);

        // ✅ Receive user data from MainActivity
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
        }

        // Initialize buttons
        btnRoyal = findViewById(R.id.btn_royal);
        btnClassic = findViewById(R.id.btn_classic);
        btnPremium = findViewById(R.id.btn_premium);
        btnLuxury = findViewById(R.id.btn_luxury);

        // Setup wedding packages with user-specific cart
        setupButton(btnRoyal, "Royal Wedding Package", 2000);
        setupButton(btnClassic, "Classic Wedding Package", 1500);
        setupButton(btnPremium, "Premium Wedding Package", 2500);
        setupButton(btnLuxury, "Luxury Wedding Package", 3000);

        // Bottom Navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;

            if (id == R.id.nav_home) {
                navIntent = new Intent(Weddingpage.this, MainActivity.class);
            } else if (id == R.id.nav_profile) {
                navIntent = new Intent(Weddingpage.this, UserProfile.class);
            } else if (id == R.id.nav_cart) {
                navIntent = new Intent(Weddingpage.this, Cart.class);
            } else {
                return false;
            }

            // Pass user data
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
                // ✅ User-specific cart
                SharedPreferences prefs = getSharedPreferences("Cart_" + username, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if (!booked) {
                    // Add item to this user's cart
                    String oldData = prefs.getString("cart_items", "");
                    String newItem = packageName + "," + price + ";";
                    editor.putString("cart_items", oldData + newItem);
                    editor.apply();

                    button.setText("View Book");
                    booked = true;
                    Toast.makeText(Weddingpage.this, packageName + " added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    // Open Cart page for this user
                    Intent intent = new Intent(Weddingpage.this, Cart.class);
                    intent.putExtra("username", username);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });
    }
}
