package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BridalShawerPage extends AppCompatActivity {

    private Button btnElegant, btnLuxury, btnGarden, btnTheme, btnFamily, btnTheme2;
    private String username, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridal_shawer_page);

        // Receive user info from previous activity
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
        }

        // Initialize buttons
        btnElegant = findViewById(R.id.btn_elegant);
        btnLuxury = findViewById(R.id.btn_luxury);
        btnGarden = findViewById(R.id.btn_garden);
        btnTheme = findViewById(R.id.btn_theme);
        btnFamily = findViewById(R.id.btn_family);
        btnTheme2 = findViewById(R.id.btn_theme2);

        // Setup all packages with user-specific cart
        setupButton(btnElegant, "Elegant Bridal Shower", 700);
        setupButton(btnLuxury, "Luxury Bridal Shower", 1200);
        setupButton(btnGarden, "Garden Bridal Shower", 900);
        setupButton(btnTheme, "Bridal Theme Shower", 1500);
        setupButton(btnFamily, "Family Bridal Shower", 1000);
        setupButton(btnTheme2, "Theme Bridal Package", 1800);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;

            if (id == R.id.nav_home) {
                navIntent = new Intent(BridalShawerPage.this, MainActivity.class);
            } else if (id == R.id.nav_profile) {
                navIntent = new Intent(BridalShawerPage.this, UserProfile.class);
            } else if (id == R.id.nav_cart) {
                navIntent = new Intent(BridalShawerPage.this, Cart.class);
            } else {
                return false;
            }

            // Pass user info
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
                    Toast.makeText(BridalShawerPage.this, packageName + " added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    // Go to user-specific Cart page
                    Intent intent = new Intent(BridalShawerPage.this, Cart.class);
                    intent.putExtra("username", username);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });
    }
}
