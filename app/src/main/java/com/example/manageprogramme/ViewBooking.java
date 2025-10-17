package com.example.manageprogramme;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewBooking extends AppCompatActivity {

    private LinearLayout bookingContainer;
    private TextView tvNoBookings;
    private DatabaseReference bookingsRef;
    private String username = "";
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);

        bookingContainer = findViewById(R.id.bookingContainer);
        tvNoBookings = findViewById(R.id.tvNoBookings);
        ImageView ivBack = findViewById(R.id.ivBack);

        // Load session data
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        username = prefs.getString("username", "");
        email = prefs.getString("email", "");

        // Intent extras overwrite session if available
        if (getIntent().getStringExtra("username") != null) {
            username = getIntent().getStringExtra("username");
        }
        if (getIntent().getStringExtra("email") != null) {
            email = getIntent().getStringExtra("email");
        }

        bookingsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(username)
                .child("Bookings");

        loadBookings();

        // ✅ Back Button click handler
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(ViewBooking.this, UserProfile.class);
            intent.putExtra("username", username);
            intent.putExtra("email", email);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadBookings() {
        tvNoBookings.setVisibility(View.VISIBLE);
        tvNoBookings.setText("Loading your bookings...");
        bookingContainer.removeAllViews();

        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                bookingContainer.removeAllViews();

                if (!snapshot.exists()) {
                    tvNoBookings.setText("No bookings found");
                    tvNoBookings.setVisibility(View.VISIBLE);
                    return;
                }

                tvNoBookings.setVisibility(View.GONE);

                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    String bookingId = bookingSnapshot.getKey();

                    MaterialCardView card = new MaterialCardView(ViewBooking.this);
                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    cardParams.setMargins(16, 8, 16, 16);
                    card.setLayoutParams(cardParams);
                    card.setRadius(16f);
                    card.setCardElevation(8f);

                    LinearLayout innerLayout = new LinearLayout(ViewBooking.this);
                    innerLayout.setOrientation(LinearLayout.VERTICAL);
                    innerLayout.setPadding(24, 24, 24, 24);
                    card.addView(innerLayout);

                    ImageView bookingImage = new ImageView(ViewBooking.this);
                    bookingImage.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 350
                    ));
                    bookingImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    bookingImage.setImageResource(R.drawable.weddding);
                    innerLayout.addView(bookingImage);

                    String name = bookingSnapshot.child("name").getValue(String.class);
                    String phone = bookingSnapshot.child("phone").getValue(String.class);
                    String address = bookingSnapshot.child("address").getValue(String.class);
                    String payment = bookingSnapshot.child("paymentMethod").getValue(String.class);
                    String items = bookingSnapshot.child("items").getValue(String.class);
                    String totalPrice = bookingSnapshot.child("totalPrice").getValue(String.class);
                    String date = bookingSnapshot.child("date").getValue(String.class);

                    addText(innerLayout, "Name: " + (name != null ? name : "N/A"));
                    addText(innerLayout, "Phone: " + (phone != null ? phone : "N/A"));
                    addText(innerLayout, "Address: " + (address != null ? address : "N/A"));
                    addText(innerLayout, "Payment: " + (payment != null ? payment : "N/A"));
                    addText(innerLayout, "Items: " + (items != null ? items : "N/A"));
                    addText(innerLayout, "Total: " + (totalPrice != null ? totalPrice : "N/A"));
                    addText(innerLayout, "Date: " + (date != null ? date : "N/A"));

                    Button cancelBtn = new Button(ViewBooking.this);
                    cancelBtn.setText("Cancel Booking");
                    cancelBtn.setBackgroundColor(Color.RED);
                    cancelBtn.setTextColor(Color.WHITE);
                    cancelBtn.setPadding(10, 10, 10, 10);
                    cancelBtn.setOnClickListener(v -> cancelBooking(bookingId, card));
                    innerLayout.addView(cancelBtn);

                    bookingContainer.addView(card);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                tvNoBookings.setText("Failed to load bookings");
                Toast.makeText(ViewBooking.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addText(LinearLayout layout, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(14f);
        layout.addView(tv);
    }

    private void cancelBooking(String bookingId, View cardView) {
        if (bookingId == null) return;

        bookingsRef.child(bookingId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ((ViewGroup) cardView.getParent()).removeView(cardView);
                Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show();

                if (bookingContainer.getChildCount() == 0) {
                    tvNoBookings.setVisibility(View.VISIBLE);
                    tvNoBookings.setText("No bookings found");
                }

            } else {
                Toast.makeText(this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
