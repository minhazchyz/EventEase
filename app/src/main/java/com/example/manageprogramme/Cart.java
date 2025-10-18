package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Cart extends AppCompatActivity {

    private LinearLayout cartContainer;
    private TextView tvTotalPrice, emptyCartMessage;
    private int totalPrice = 0;
    private SharedPreferences prefs;
    private StringBuilder allItems = new StringBuilder();
    private String username, email; // ✅ Added email too

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        // System bar padding fix
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // ✅ Receive user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");

        // Initialize UI
        cartContainer = findViewById(R.id.cart_items_container);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        emptyCartMessage = findViewById(R.id.empty_cart_message);

        // User-specific SharedPreferences
        prefs = getSharedPreferences("Cart_" + username, MODE_PRIVATE);

        // Load items
        loadCartItems();

        // Bottom Navigation Setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_cart); // highlight Cart tab

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent = null;

            if (id == R.id.nav_home) {
                navIntent = new Intent(this, MainActivity.class);
            } else if (id == R.id.nav_profile) {
                navIntent = new Intent(this, UserProfile.class);
            } else if (id == R.id.nav_cart) {
                return true; // already here
            }

            if (navIntent != null) {
                navIntent.putExtra("username", username);
                navIntent.putExtra("email", email);
                startActivity(navIntent);
                overridePendingTransition(0, 0); // no animation
            }
            return true;
        });

        // ✅ Checkout Button
        Button btnCheckout = findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(v -> {
            if (cartContainer.getChildCount() == 0) {
                Toast.makeText(Cart.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent checkoutIntent = new Intent(Cart.this, Checkout.class);
            checkoutIntent.putExtra("username", username);
            checkoutIntent.putExtra("email", email);
            checkoutIntent.putExtra("items", allItems.toString());
            checkoutIntent.putExtra("total_price", "$" + totalPrice);
            startActivity(checkoutIntent);
        });
    }

    private void loadCartItems() {
        String data = prefs.getString("cart_items", "");
        if (data.isEmpty()) {
            emptyCartMessage.setVisibility(View.VISIBLE);
            return;
        }

        emptyCartMessage.setVisibility(View.GONE);
        String[] items = data.split(";");
        for (String item : items) {
            if (item.trim().isEmpty()) continue;
            String[] parts = item.split(",");
            if (parts.length == 2) {
                String name = parts[0];
                int price = Integer.parseInt(parts[1]);
                addCartItem(name, price);
            }
        }
    }

    private void addCartItem(String packageName, int price) {
        CardView card = new CardView(this);
        card.setCardElevation(6);
        card.setRadius(12);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 12);
        card.setLayoutParams(cardParams);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(10, 10, 10, 10);

        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.weddding);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(100, 100);
        imgParams.setMargins(0, 0, 10, 0);
        img.setLayoutParams(imgParams);
        layout.addView(img);

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView nameView = new TextView(this);
        nameView.setText(packageName);
        nameView.setTextSize(16);
        nameView.setTextColor(getResources().getColor(R.color.black));
        nameView.setPadding(0, 0, 0, 4);

        TextView priceView = new TextView(this);
        priceView.setText("Price: $" + price);
        priceView.setTextSize(14);
        priceView.setTextColor(getResources().getColor(R.color.purple));

        textLayout.addView(nameView);
        textLayout.addView(priceView);
        layout.addView(textLayout);

        ImageButton btnRemove = new ImageButton(this);
        btnRemove.setImageResource(android.R.drawable.ic_menu_delete);
        btnRemove.setBackgroundResource(android.R.color.transparent);
        layout.addView(btnRemove);

        btnRemove.setOnClickListener(v -> {
            cartContainer.removeView(card);
            totalPrice -= price;
            updateTotalPrice();
            removeItemFromPrefs(packageName, price);
            if (cartContainer.getChildCount() == 0) {
                emptyCartMessage.setVisibility(View.VISIBLE);
                allItems.setLength(0);
            } else {
                String itemStr = packageName + ", ";
                int index = allItems.indexOf(itemStr);
                if (index != -1) {
                    allItems.delete(index, index + itemStr.length());
                }
            }
        });

        card.addView(layout);
        cartContainer.addView(card);

        totalPrice += price;
        updateTotalPrice();

        if (allItems.length() > 0) allItems.append(", ");
        allItems.append(packageName);
    }

    private void removeItemFromPrefs(String name, int price) {
        String data = prefs.getString("cart_items", "");
        String toRemove = name + "," + price + ";";
        data = data.replace(toRemove, "");
        prefs.edit().putString("cart_items", data).apply();
    }

    private void updateTotalPrice() {
        tvTotalPrice.setText("Total: $" + totalPrice);
    }
}
