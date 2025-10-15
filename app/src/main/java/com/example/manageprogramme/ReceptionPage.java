package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ReceptionPage extends AppCompatActivity {

    private Button btnReception1, btnReception2, btnReception3, btnReception4, btnReception5, btnReception6;

    private String username, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reception_page);

        // ✅ Receive user data
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
        }

        // Initialize buttons
        btnReception1 = findViewById(R.id.btn_reception1);
        btnReception2 = findViewById(R.id.btn_reception2);
        btnReception3 = findViewById(R.id.btn_reception3);
        btnReception4 = findViewById(R.id.btn_reception4);
        btnReception5 = findViewById(R.id.btn_reception5);
        btnReception6 = findViewById(R.id.btn_reception6);

        // Setup all packages
        setupButton(btnReception1, "Reception Package 1", 500);
        setupButton(btnReception2, "Reception Package 2", 700);
        setupButton(btnReception3, "Reception Package 3", 900);
        setupButton(btnReception4, "Reception Package 4", 1100);
        setupButton(btnReception5, "Reception Package 5", 1300);
        setupButton(btnReception6, "Reception Package 6", 1500);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;

            if (id == R.id.nav_home) {
                navIntent = new Intent(ReceptionPage.this, MainActivity.class);
            } else if (id == R.id.nav_profile) {
                navIntent = new Intent(ReceptionPage.this, UserProfile.class);
            } else if (id == R.id.nav_cart) {
                navIntent = new Intent(ReceptionPage.this, Cart.class);
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
                    Toast.makeText(ReceptionPage.this, packageName + " added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    // Open Cart
                    Intent intent = new Intent(ReceptionPage.this, Cart.class);
                    intent.putExtra("username", username);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });
    }
}
