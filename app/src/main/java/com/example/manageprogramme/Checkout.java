package com.example.manageprogramme;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Checkout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from intent
        String packageName = getIntent().getStringExtra("package_name");
        String price = getIntent().getStringExtra("price");

        // Find views
        TextView tvItems = findViewById(R.id.tv_items);
        TextView tvTotalPrice = findViewById(R.id.tv_total_price);

        // Display received data
        if (packageName != null && price != null) {
            tvItems.setText(packageName);
            tvTotalPrice.setText("Total: " + price);
        }
    }
}
