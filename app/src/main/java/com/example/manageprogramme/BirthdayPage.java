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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_page);

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

            if (id == R.id.nav_home) {
                startActivity(new Intent(BirthdayPage.this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(BirthdayPage.this, UserProfile.class));
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(BirthdayPage.this, Cart.class));
                return true;
            }
            return false;
        });
    }

    private void setupButton(Button button, String packageName, int price) {
        button.setOnClickListener(new View.OnClickListener() {
            private boolean booked = false;

            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("MyCart", MODE_PRIVATE);
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
                    startActivity(intent);
                }
            }
        });
    }
}
