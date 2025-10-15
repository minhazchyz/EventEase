package com.example.manageprogramme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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
        tvDate = findViewById(R.id.tvDate); // XML e add kora lagbe

        btnHome = findViewById(R.id.btnHome);

        // Get booking data from Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String name = intent.getStringExtra("name");
        String phone = intent.getStringExtra("phone");
        String address = intent.getStringExtra("address");
        String paymentMethod = intent.getStringExtra("paymentMethod");
        String items = intent.getStringExtra("items");
        String totalPrice = intent.getStringExtra("totalPrice");
        String date = intent.getStringExtra("date");

        // Set data to views
        tvName.setText("Name: " + name);
        tvPhone.setText("Phone: " + phone);
        tvAddress.setText("Address: " + address);
        tvPayment.setText("Payment: " + paymentMethod);
        tvItems.setText("Items: " + items);
        tvTotalPrice.setText("Total: " + totalPrice);
        tvDate.setText("Date: " + date);

        // Save booking to Firebase
        saveBookingToFirebase(username, name, phone, address, paymentMethod, items, totalPrice, date);

        btnHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ConfirmBooking.this, MainActivity.class);
            homeIntent.putExtra("username", username);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });
    }

    private void saveBookingToFirebase(String username, String name, String phone,
                                       String address, String paymentMethod,
                                       String items, String totalPrice, String date) {

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "❌ User information missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(username)
                .child("Bookings");

        String bookingId = ref.push().getKey();
        if (bookingId == null) {
            Toast.makeText(this, "❌ Failed to generate booking ID", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> bookingData = new HashMap<>();
        bookingData.put("name", name);
        bookingData.put("phone", phone);
        bookingData.put("address", address);
        bookingData.put("payment", paymentMethod);
        bookingData.put("items", items);
        bookingData.put("totalPrice", totalPrice);
        bookingData.put("date", date); // save date
        bookingData.put("timestamp", System.currentTimeMillis());

        ref.child(bookingId).setValue(bookingData)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "✅ Booking saved successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "❌ Failed to save booking: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
