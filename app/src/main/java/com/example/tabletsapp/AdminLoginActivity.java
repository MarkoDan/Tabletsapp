package com.example.tabletsapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminLoginActivity extends AppCompatActivity {

    // Declare UI components for admin login
    private EditText etAdminUsername, etAdminPassword; // Input fields for admin username and password
    private Button btnAdminLogin; // Button for initiating login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login); // Set the layout for the login screen

        // Initialize UI components
        etAdminUsername = findViewById(R.id.etAdminUsername);
        etAdminPassword = findViewById(R.id.etAdminPassword);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);

        // Set click listener on the login button to validate credentials
        btnAdminLogin.setOnClickListener(v -> validateAdminLogin());
    }

    /**
     * Validates admin login by checking the entered username and password
     * against the database.
     */
    private void validateAdminLogin() {
        // Retrieve entered username and password
        String username = etAdminUsername.getText().toString().trim();
        String password = etAdminPassword.getText().toString().trim();

        // Check if either field is empty
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return; // Exit the method if validation fails
        }

        // Access the database helper to query admin credentials
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // SQL query to find an admin with matching username and password
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_ADMINS +
                " WHERE " + DatabaseHelper.COLUMN_ADMIN_USERNAME + " = ? COLLATE NOCASE" + // Case-insensitive username comparison
                " AND " + DatabaseHelper.COLUMN_ADMIN_PASSWORD + " = ?";

        try {
            // Execute the query with provided username and password
            Cursor cursor = db.rawQuery(query, new String[]{username.trim(), password.trim()});

            if (cursor.moveToFirst()) {
                // If a match is found, display success message and navigate to dashboard
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AdminDashboardActivity.class)); // Open AdminDashboardActivity
                finish(); // Close the current activity
            } else {
                // If no match is found, show an error message
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
            // Close the cursor and database connection
            cursor.close();
            db.close();
        } catch (Exception e) {
            // Handle any errors during database operations
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
