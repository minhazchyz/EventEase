package com.example.manageprogramme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ConfirmBooking extends AppCompatActivity {

    private TextView tvName, tvPhone, tvAddress, tvPayment, tvItems, tvTotalPrice;
    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_booking);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Initialize views
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvPayment = findViewById(R.id.tvPayment);
        tvItems = findViewById(R.id.tvItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnHome = findViewById(R.id.btnHome);

        // Receive data from Checkout
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String phone = intent.getStringExtra("phone");
        String address = intent.getStringExtra("address");
        String paymentMethod = intent.getStringExtra("paymentMethod");
        String items = intent.getStringExtra("items");
        String totalPrice = intent.getStringExtra("totalPrice");

        // Set data to TextViews
        tvName.setText("Name: " + name);
        tvPhone.setText("Phone: " + phone);
        tvAddress.setText("Address: " + address);
        tvPayment.setText("Payment: " + paymentMethod);
        tvItems.setText("Items: " + items);
        tvTotalPrice.setText("Total: " + totalPrice);

        // ✅ Save Booking info to Firebase
        saveBookingToFirebase(name, phone, address, paymentMethod, items, totalPrice);

        // Back to Home button click
        btnHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ConfirmBooking.this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });
    }

    private void saveBookingToFirebase(String name, String phone, String address,
                                       String paymentMethod, String items, String totalPrice) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "❌ User not logged in!", Toast.LENGTH_LONG).show();
            return;
        }

        String uid = currentUser.getUid();

        // Firebase database reference
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("Bookings");

        // Unique booking ID
        String bookingId = ref.push().getKey();
        if (bookingId == null) {
            Toast.makeText(this, "❌ Failed to generate booking ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Booking data map
        HashMap<String, Object> bookingData = new HashMap<>();
        bookingData.put("name", name);
        bookingData.put("phone", phone);
        bookingData.put("address", address);
        bookingData.put("payment", paymentMethod);
        bookingData.put("items", items);
        bookingData.put("totalPrice", totalPrice);
        bookingData.put("timestamp", System.currentTimeMillis());

        // Upload to Firebase
        ref.child(bookingId).setValue(bookingData)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "✅ Booking saved successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "❌ Failed to save booking: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
