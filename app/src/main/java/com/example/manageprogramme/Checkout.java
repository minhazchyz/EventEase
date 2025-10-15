package com.example.manageprogramme;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Checkout extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etAddress, etDate;
    private TextView tvItems, tvTotalPrice;
    private RadioGroup radioPayment;
    private Button btnConfirmOrder;
    private String username;

    private DatabaseReference bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        // Edge to edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Initialize views
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etDate = findViewById(R.id.et_date);
        tvItems = findViewById(R.id.tv_items);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        radioPayment = findViewById(R.id.radio_payment);
        btnConfirmOrder = findViewById(R.id.btn_confirm_order);

        // Firebase reference
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");

        // Get data from Cart
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        String items = intent.getStringExtra("items");
        String totalPrice = intent.getStringExtra("total_price");

        if (items != null && totalPrice != null) {
            tvItems.setText(items);
            tvTotalPrice.setText("Total: " + totalPrice);
        }

        // DatePicker for date selection
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Checkout.this,
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "-" + (month1 + 1) + "-" + year1;
                        etDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Confirm Order Button Click
        btnConfirmOrder.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            int selectedPaymentId = radioPayment.getCheckedRadioButtonId();

            // Validation
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all information and select a date!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Please select a payment method!", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedPayment = findViewById(selectedPaymentId);
            String paymentMethod = selectedPayment.getText().toString();

            // Check date availability in Firebase
            bookingsRef.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        Toast.makeText(Checkout.this, "Selected date is not available!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Date available, save booking
                        Map<String, String> bookingData = new HashMap<>();
                        bookingData.put("username", username);
                        bookingData.put("name", name);
                        bookingData.put("email", email);
                        bookingData.put("phone", phone);
                        bookingData.put("address", address);
                        bookingData.put("paymentMethod", paymentMethod);
                        bookingData.put("items", items);
                        bookingData.put("totalPrice", totalPrice);
                        bookingData.put("date", date);

                        bookingsRef.child(date).setValue(bookingData).addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                Toast.makeText(Checkout.this, "Booking confirmed!", Toast.LENGTH_SHORT).show();
                                // Navigate to ConfirmBooking
                                Intent confirmIntent = new Intent(Checkout.this, ConfirmBooking.class);
                                confirmIntent.putExtra("username", username);
                                confirmIntent.putExtra("name", name);
                                confirmIntent.putExtra("phone", phone);
                                confirmIntent.putExtra("address", address);
                                confirmIntent.putExtra("paymentMethod", paymentMethod);
                                confirmIntent.putExtra("items", items);
                                confirmIntent.putExtra("totalPrice", totalPrice);
                                confirmIntent.putExtra("date", date);
                                startActivity(confirmIntent);
                                finish();
                            } else {
                                Toast.makeText(Checkout.this, "Failed to book. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Checkout.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
