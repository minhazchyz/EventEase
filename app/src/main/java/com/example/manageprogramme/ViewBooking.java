package com.example.manageprogramme;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewBooking extends AppCompatActivity {

    private LinearLayout bookingsContainer;
    private DatabaseReference bookingsRef;
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);

        bookingsContainer = findViewById(R.id.scrollLinearLayout);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // ✅ Get username from SharedPreferences or Intent
        username = getSharedPreferences("UserSession", MODE_PRIVATE)
                .getString("username", "");

        if (username == null || username.isEmpty()) {
            username = getIntent().getStringExtra("username");
        }

        // ✅ Reference to user's bookings
        bookingsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(username)
                .child("Bookings");

        loadBookings();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                return true;
            }
            return false;
        });
    }

    private void loadBookings() {
        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                bookingsContainer.removeAllViews();

                if (!snapshot.exists()) {
                    Toast.makeText(ViewBooking.this, "No bookings found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    String bookingId = bookingSnapshot.getKey(); // ✅ For cancel action

                    // Create MaterialCardView
                    MaterialCardView card = new MaterialCardView(ViewBooking.this);
                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    cardParams.setMargins(16, 8, 16, 16);
                    card.setLayoutParams(cardParams);
                    card.setRadius(12f);
                    card.setCardElevation(6f);

                    // Inner layout
                    LinearLayout innerLayout = new LinearLayout(ViewBooking.this);
                    innerLayout.setOrientation(LinearLayout.VERTICAL);
                    innerLayout.setPadding(12, 12, 12, 12);
                    card.addView(innerLayout);

                    // Booking image
                    ImageView bookingImage = new ImageView(ViewBooking.this);
                    bookingImage.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 400
                    ));
                    bookingImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    bookingImage.setImageResource(R.drawable.weddding);
                    innerLayout.addView(bookingImage);

                    // Booking details
                    String name = bookingSnapshot.child("name").getValue(String.class);
                    String phone = bookingSnapshot.child("phone").getValue(String.class);
                    String address = bookingSnapshot.child("address").getValue(String.class);
                    String payment = bookingSnapshot.child("payment").getValue(String.class);
                    String items = bookingSnapshot.child("items").getValue(String.class);
                    String totalPrice = bookingSnapshot.child("totalPrice").getValue(String.class);

                    addText(innerLayout, "Name: " + name);
                    addText(innerLayout, "Phone: " + phone);
                    addText(innerLayout, "Address: " + address);
                    addText(innerLayout, "Payment: " + payment);
                    addText(innerLayout, "Items: " + items);
                    addText(innerLayout, "Total: " + totalPrice);

                    // ✅ Add Cancel Button
                    Button cancelBtn = new Button(ViewBooking.this);
                    cancelBtn.setText("Cancel Booking");
                    cancelBtn.setBackgroundColor(Color.RED);
                    cancelBtn.setTextColor(Color.WHITE);
                    cancelBtn.setPadding(16, 8, 16, 8);
                    cancelBtn.setOnClickListener(v -> cancelBooking(bookingId, card));
                    innerLayout.addView(cancelBtn);

                    // Add to container
                    bookingsContainer.addView(card);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ViewBooking.this, "Failed to load bookings: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addText(LinearLayout layout, String text) {
        TextView tv = new TextView(ViewBooking.this);
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(14f);
        layout.addView(tv);
    }

    // ✅ Function to cancel (delete) booking
    private void cancelBooking(String bookingId, View cardView) {
        if (bookingId == null) return;

        bookingsRef.child(bookingId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ((ViewGroup) cardView.getParent()).removeView(cardView);
                Toast.makeText(ViewBooking.this, "Booking cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ViewBooking.this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
