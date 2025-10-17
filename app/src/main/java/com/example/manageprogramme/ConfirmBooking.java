package com.example.manageprogramme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfirmBooking extends AppCompatActivity {

    private TextView tvName, tvPhone, tvAddress, tvPayment, tvItems, tvTotalPrice, tvDate;
    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_booking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvPayment = findViewById(R.id.tvPayment);
        tvItems = findViewById(R.id.tvItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvDate = findViewById(R.id.tvDate);
        btnHome = findViewById(R.id.btnHome);

        // Get data from Intent (only show)
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String phone = intent.getStringExtra("phone");
        String address = intent.getStringExtra("address");
        String paymentMethod = intent.getStringExtra("paymentMethod");
        String items = intent.getStringExtra("items");
        String totalPrice = intent.getStringExtra("totalPrice");
        String date = intent.getStringExtra("date");

        tvName.setText("Name: " + name);
        tvPhone.setText("Phone: " + phone);
        tvAddress.setText("Address: " + address);
        tvPayment.setText("Payment: " + paymentMethod);
        tvItems.setText("Items: " + items);
        tvTotalPrice.setText("Total: " + totalPrice);
        tvDate.setText("Date: " + date);

        // Back to home
        btnHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ConfirmBooking.this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });
    }
}
