package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MehendiPage extends AppCompatActivity {

    private Button btnMehendi1, btnMehendi2, btnMehendi3, btnMehendi4, btnMehendi5, btnMehendi6;

    private String username, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mehendi_page);

        // ✅ Receive user data
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
        }

        // Initialize buttons
        btnMehendi1 = findViewById(R.id.btn_mehendi1);
        btnMehendi2 = findViewById(R.id.btn_mehendi2);
        btnMehendi3 = findViewById(R.id.btn_mehendi3);
        btnMehendi4 = findViewById(R.id.btn_mehendi4);
        btnMehendi5 = findViewById(R.id.btn_mehendi5);
        btnMehendi6 = findViewById(R.id.btn_mehendi6);

        // Setup packages
        setupButton(btnMehendi1, "Traditional Mehendi", 250);
        setupButton(btnMehendi2, "Bridal Full Hand Mehendi", 500);
        setupButton(btnMehendi3, "Bridal Feet Mehendi", 300);
        setupButton(btnMehendi4, "Guest Mehendi Package", 200);
        setupButton(btnMehendi5, "Designer Mehendi", 600);
        setupButton(btnMehendi6, "Custom Mehendi Art", 700);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;

            if (id == R.id.nav_home) {
                navIntent = new Intent(MehendiPage.this, MainActivity.class);
            } else if (id == R.id.nav_profile) {
                navIntent = new Intent(MehendiPage.this, UserProfile.class);
            } else if (id == R.id.nav_cart) {
                navIntent = new Intent(MehendiPage.this, Cart.class);
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
                SharedPreferences prefs = getSharedPreferences("Cart_" + username, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if (!booked) {
                    // Add package to cart
                    String oldData = prefs.getString("cart_items", "");
                    String newItem = packageName + "," + price + ";";
                    editor.putString("cart_items", oldData + newItem);
                    editor.apply();

                    button.setText("View Book");
                    booked = true;
                    Toast.makeText(MehendiPage.this, packageName + " added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    // Open Cart
                    Intent intent = new Intent(MehendiPage.this, Cart.class);
                    intent.putExtra("username", username);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });
    }
}