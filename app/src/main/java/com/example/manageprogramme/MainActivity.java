package com.example.manageprogramme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private String username;
    private String name;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Receive user data
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
        }

        // CardView references
        CardView weddingCard = findViewById(R.id.card_wedding);
        CardView birthdayCard = findViewById(R.id.birthday);
        CardView bridalShawerCard = findViewById(R.id.card_bridal);
        CardView pujaCard = findViewById(R.id.card_puja);
        CardView mehendiCard = findViewById(R.id.card_mehendi);
        CardView receptionCard = findViewById(R.id.card_reception);
        CardView shoppingCard = findViewById(R.id.card_shopping);
        Button shopNowButton = findViewById(R.id.btn_shop_now);
        ImageButton btnInfo = findViewById(R.id.btn_info);

        // Open Shopping Page (for both card & button)
        View.OnClickListener openShoppingPage = v -> {
            Intent i = new Intent(MainActivity.this, ShoppingPage.class);
            i.putExtra("username", username);
            i.putExtra("name", name);
            i.putExtra("email", email);
            startActivity(i);
        };

        // Card click events
        weddingCard.setOnClickListener(v -> openPage(Weddingpage.class));
        birthdayCard.setOnClickListener(v -> openPage(BirthdayPage.class));
        bridalShawerCard.setOnClickListener(v -> openPage(BridalShawerPage.class));
        pujaCard.setOnClickListener(v -> openPage(PujaPage.class));
        mehendiCard.setOnClickListener(v -> openPage(MehendiPage.class));
        receptionCard.setOnClickListener(v -> openPage(ReceptionPage.class));

        // Shopping click listeners
        shoppingCard.setOnClickListener(openShoppingPage);
        shopNowButton.setOnClickListener(openShoppingPage);

        // Developer Info button click
        btnInfo.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DeveloperInfoActivity.class)));

        // 🔹 Bottom Navigation Setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Highlight Home tab when this page is open
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Already on Home
                return true;
            } else if (id == R.id.nav_profile) {
                Intent i = new Intent(MainActivity.this, UserProfile.class);
                passUserData(i);
                startActivity(i);
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_cart) {
                Intent i = new Intent(MainActivity.this, Cart.class);
                passUserData(i);
                startActivity(i);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    // 🔹 Helper method to open event pages with user data
    private void openPage(Class<?> targetActivity) {
        Intent i = new Intent(MainActivity.this, targetActivity);
        passUserData(i);
        startActivity(i);
    }

    // 🔹 Helper method to pass user info between activities
    private void passUserData(Intent i) {
        i.putExtra("username", username);
        i.putExtra("name", name);
        i.putExtra("email", email);
    }
}
