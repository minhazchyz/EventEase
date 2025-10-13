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

    private String username;
    private String name;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ✅ Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ Receive user data from LoginActivity
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
        }

        // ✅ CardView references
        CardView weddingCard = findViewById(R.id.card_wedding);
        CardView birthdayCard = findViewById(R.id.birthday);
        CardView bridalShawerCard = findViewById(R.id.card_bridal);
        CardView pujaCard = findViewById(R.id.card_puja);

        // ✅ Card click events → pass user data
        weddingCard.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, Weddingpage.class);
            i.putExtra("username", username);
            i.putExtra("name", name);
            i.putExtra("email", email);
            startActivity(i);
        });

        birthdayCard.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, BirthdayPage.class);
            i.putExtra("username", username);
            i.putExtra("name", name);
            i.putExtra("email", email);
            startActivity(i);
        });

        bridalShawerCard.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, BridalShawerPage.class);
            i.putExtra("username", username);
            i.putExtra("name", name);
            i.putExtra("email", email);
            startActivity(i);
        });

        pujaCard.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, PujaPage.class);
            i.putExtra("username", username);
            i.putExtra("name", name);
            i.putExtra("email", email);
            startActivity(i);
        });

        // ✅ Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true; // already in home
            } else if (id == R.id.nav_profile) {
                Intent i = new Intent(MainActivity.this, UserProfile.class);
                i.putExtra("username", username);
                i.putExtra("name", name);
                i.putExtra("email", email);
                startActivity(i);
                return true;
            } else if (id == R.id.nav_cart) {
                Intent i = new Intent(MainActivity.this, Cart.class);
                i.putExtra("username", username);
                i.putExtra("name", name);
                i.putExtra("email", email);
                startActivity(i);
                return true;
            }

            return false;
        });
    }
}
