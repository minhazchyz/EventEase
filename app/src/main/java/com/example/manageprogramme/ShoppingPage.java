package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShoppingPage extends AppCompatActivity {

    private String username, name, email;

    private Button btnSareeCombo, btnSherwaniCombo, btnBirthdayCake, btnFlowerBouquet, btnGiftBox, btnDecorLights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_page);

        // Receive user data
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
        }

        // Initialize buttons
        btnSareeCombo = findViewById(R.id.btn_add_saree);
        btnSherwaniCombo = findViewById(R.id.btn_add_sherwani);
        btnBirthdayCake = findViewById(R.id.btn_add_cake);
        btnFlowerBouquet = findViewById(R.id.btn_add_flowers); // fixed
        btnGiftBox = findViewById(R.id.btn_add_giftbox);
        btnDecorLights = findViewById(R.id.btn_add_lights);

        // Setup all items
        setupItem(btnSareeCombo, "Saree Combo", 200);
        setupItem(btnSherwaniCombo, "Sherwani Combo", 300);
        setupItem(btnBirthdayCake, "Birthday Cake", 50);
        setupItem(btnFlowerBouquet, "Flower Bouquet", 30);
        setupItem(btnGiftBox, "Gift Box Set", 80);
        setupItem(btnDecorLights, "Decorative Lights", 120);

        // Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_shopping); // fixed
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;

            if (id == R.id.nav_home) {
                navIntent = new Intent(ShoppingPage.this, MainActivity.class);
            } else if (id == R.id.nav_profile) {
                navIntent = new Intent(ShoppingPage.this, UserProfile.class);
            } else if (id == R.id.nav_cart) {
                navIntent = new Intent(ShoppingPage.this, Cart.class);
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

    private void setupItem(Button button, String itemName, int price) {
        button.setOnClickListener(new View.OnClickListener() {
            private boolean added = false;

            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("Cart_" + username, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if (!added) {
                    String oldData = prefs.getString("cart_items", "");
                    String newItem = itemName + "," + price + ";";
                    editor.putString("cart_items", oldData + newItem);
                    editor.apply();

                    button.setText("View Cart");
                    added = true;
                    Toast.makeText(ShoppingPage.this, itemName + " added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ShoppingPage.this, Cart.class);
                    intent.putExtra("username", username);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });
    }
}