package com.example.manageprogramme;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // CardView references
        CardView weddingCard = findViewById(R.id.card_wedding);
        CardView birthdayCard = findViewById(R.id.birthday);
        CardView bridalShawerCard = findViewById(R.id.card_bridal);

        // Card click events
        weddingCard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Weddingpage.class)));
        birthdayCard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BirthdayPage.class)));
        bridalShawerCard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BridalShawerPage.class)));

        // Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Already in MainActivity, do nothing
                return true;
            } else if (id == R.id.nav_profile) {
                // Open profile activity (if exists)
                startActivity(new Intent(MainActivity.this, Cart.class));
                return true;
            } else if (id == R.id.nav_cart) {
                // Open Cart activity
                startActivity(new Intent(MainActivity.this, Cart.class));
                return true;
            }

            return false;
        });
    }
}
