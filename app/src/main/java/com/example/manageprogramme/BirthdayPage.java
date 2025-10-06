package com.example.manageprogramme;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BirthdayPage extends AppCompatActivity {

    private Button btnKids, btnTeen, btnAdult, btnLuxury, btnFamily, btnTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_page); // XML file name check korben

        // Button initialize
        btnKids = findViewById(R.id.btn_kids);
        btnTeen = findViewById(R.id.btn_teen);
        btnAdult = findViewById(R.id.btn_adult);
        btnLuxury = findViewById(R.id.btn_luxury);
        btnFamily = findViewById(R.id.btn_family);
        btnTheme = findViewById(R.id.btn_theme);

        // Click listener add
        setButtonListener(btnKids);
        setButtonListener(btnTeen);
        setButtonListener(btnAdult);
        setButtonListener(btnLuxury);
        setButtonListener(btnFamily);
        setButtonListener(btnTheme);
    }

    private void setButtonListener(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setText("View Book"); // Text change
                // Ekhane chaile aro kaj korte paren, like intent diye new activity open kora
            }
        });
    }
}
