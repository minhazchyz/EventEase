package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PujaPage extends AppCompatActivity {

    private Button btnGanesh, btnDurga, btnLaxmi, btnSaraswati, btnSpecial, btnFestival;

    private String username, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puja_page);

        // ✅ Receive user data
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
        }

        // Initialize buttons
        btnGanesh = findViewById(R.id.btn_puja1);
        btnDurga = findViewById(R.id.btn_puja2);
        btnLaxmi = findViewById(R.id.btn_puja3);
        btnSaraswati = findViewById(R.id.btn_puja4);
        btnSpecial = findViewById(R.id.btn_puja5);
        btnFestival = findViewById(R.id.btn_puja6);

        // Setup all packages
        setupButton(btnGanesh, "Ganesh Puja", 300);
        setupButton(btnDurga, "Durga Puja", 600);
        setupButton(btnLaxmi, "Laxmi Puja", 400);
        setupButton(btnSaraswati, "Saraswati Puja", 500);
        setupButton(btnSpecial, "Special Puja Ceremony", 700);
        setupButton(btnFestival, "Festival Puja Package", 900);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;

            if (id == R.id.nav_home) {
                navIntent = new Intent(PujaPage.this, MainActivity.class);
            } else if (id == R.id.nav_profile) {
                navIntent = new Intent(PujaPage.this, UserProfile.class);
            } else if (id == R.id.nav_cart) {
                navIntent = new Intent(PujaPage.this, Cart.class);
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
                    Toast.makeText(PujaPage.this, packageName + " added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    // Open Cart
                    Intent intent = new Intent(PujaPage.this, Cart.class);
                    intent.putExtra("username", username);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });
    }
}
