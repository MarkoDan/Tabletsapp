package com.example.tabletsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * MainActivity class serves as the entry point for the app, allowing users
 * to choose between User and Admin modes.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is starting. This is where most initialization happens.
     *
     * @param savedInstanceState If the activity is being re-initialized after being previously
     *                           shut down, this contains the saved state data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display for a more immersive experience
        EdgeToEdge.enable(this);

        // Set the content view to the layout file for MainActivity
        setContentView(R.layout.activity_main);

        // Adjust padding to account for system bars (like the status bar and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the buttons for User and Admin modes in the layout
        Button btnUser = findViewById(R.id.btnUser);
        Button btnAdmin = findViewById(R.id.btnAdmin);

        // Set an OnClickListener for the "User" button
        btnUser.setOnClickListener(v -> {
            // Navigate to the UserFormActivity when the "User" button is clicked
            Intent intent = new Intent(MainActivity.this, UserFormActivity.class);
            startActivity(intent);
        });

        // Set an OnClickListener for the "Admin" button
        btnAdmin.setOnClickListener(v -> {
            // Navigate to the AdminLoginActivity when the "Admin" button is clicked
            Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }
}
