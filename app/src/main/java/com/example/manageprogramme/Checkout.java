package com.example.manageprogramme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Checkout extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etAddress;
    private TextView tvItems, tvTotalPrice;
    private RadioGroup radioPayment;
    private Button btnConfirmOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

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
        tvItems = findViewById(R.id.tv_items);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        radioPayment = findViewById(R.id.radio_payment);
        btnConfirmOrder = findViewById(R.id.btn_confirm_order);

        // Get data from Cart
        Intent intent = getIntent();
        String items = intent.getStringExtra("items");
        String totalPrice = intent.getStringExtra("total_price");

        if (items != null && totalPrice != null) {
            tvItems.setText(items);
            tvTotalPrice.setText("Total: " + totalPrice);
        }

        // Confirm Order Button Click
        btnConfirmOrder.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            int selectedPaymentId = radioPayment.getCheckedRadioButtonId();
            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show();
            // Validation checks
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill all personal information!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Please select a payment method!", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedPayment = findViewById(selectedPaymentId);
            String paymentMethod = selectedPayment.getText().toString();

            //  Open ConfirmBooking page with data



            // Send data to ConfirmBooking
            Intent confirmIntent = new Intent(Checkout.this, ConfirmBooking.class);

            confirmIntent.putExtra("name", name);
            confirmIntent.putExtra("phone", phone);
            confirmIntent.putExtra("address", address);
            confirmIntent.putExtra("paymentMethod", paymentMethod);
            confirmIntent.putExtra("items", items);
            confirmIntent.putExtra("totalPrice", totalPrice);

            startActivity(confirmIntent);
            finish(); // Close Checkout
        });
    }
}
