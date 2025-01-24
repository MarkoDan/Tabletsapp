package com.example.tabletsapp;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.*;

public class UserFormActivity extends AppCompatActivity {

    // Declare UI components
    private Spinner spinnerTabletBrand; // Dropdown for tablet brand selection
    private RadioGroup radioGroupCable; // Radio buttons for cable type selection
    private EditText etBorrowerName, etBorrowerContact; // EditTexts for borrower details
    private Button btnSubmitLoad; // Button to submit the loan information

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        // Initialize UI components with their corresponding IDs from the layout
        spinnerTabletBrand = findViewById(R.id.spinnerTabletBrand);
        radioGroupCable = findViewById(R.id.radioGroupCable);
        etBorrowerName = findViewById(R.id.etBorrowerName);
        etBorrowerContact = findViewById(R.id.etBorrowerContact);
        btnSubmitLoad = findViewById(R.id.btnSubmitLoan);

        // Set a listener on the submit button
        btnSubmitLoad.setOnClickListener(v -> registerLoan());
    }

    // Method to register the loan and store data in the database
    private void registerLoan() {
        // Get the selected tablet brand from the spinner
        String tabletBrand = spinnerTabletBrand.getSelectedItem().toString();

        // Get the text entered by the borrower in the name and contact EditText fields
        String borrowerName = etBorrowerName.getText().toString();
        String borrowerContact = etBorrowerContact.getText().toString();

        // Initialize cableType to null (default if not selected)
        String cableType = null;

        // Check if the required fields (tablet brand and borrower name) are filled
        if (tabletBrand.isEmpty() || borrowerName.isEmpty()) {
            Toast.makeText(this, "Tablet brand and borrower name are required", Toast.LENGTH_SHORT).show();
            return;  // Exit the method if required fields are missing
        }

        // Get the selected cable type if any radio button is checked
        int selectedCableId = radioGroupCable.getCheckedRadioButtonId();
        if(selectedCableId != -1) {
            cableType = ((RadioButton) findViewById(selectedCableId)).getText().toString();
        }

        // Get the current date and time for the loan record
        String loanDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

        // Initialize the database helper and get writable database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Prepare values to be inserted into the database
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TABLET_BRAND, tabletBrand);
        values.put(DatabaseHelper.COLUMN_CABLE_TYPE, cableType);
        values.put(DatabaseHelper.COLUMN_BORROWER_NAME, borrowerName);
        values.put(DatabaseHelper.COLUMN_BORROWER_CONTACT, borrowerContact);
        values.put(DatabaseHelper.COLUMN_LOAN_DATE, loanDate);

        // Insert the loan data into the database
        long result = db.insert(DatabaseHelper.TABLE_LOANS, null, values);

        // Check if the insertion was successful and display appropriate messages
        if (result != -1) {
            // Show a loan overview in a TextView after successful registration
            TextView tvLoanOverview = findViewById(R.id.tvLoanOverview);
            tvLoanOverview.setVisibility(View.VISIBLE); // Make the overview TextView visible
            tvLoanOverview.setText("Loan Registered Successfully!\n\n" +
                    "Tablet Brand: " + tabletBrand + "\n" +
                    "Borrower Name: " + borrowerName + "\n" +
                    "Borrower Contact: " + borrowerContact + "\n" +
                    "Cable Type: " + (cableType != null ? cableType : "Not selected") + "\n" +
                    "Loan Date: " + loanDate);

            // Show a success toast
            Toast.makeText(this, "Loan registered successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // Show an error toast if the loan registration failed
            Toast.makeText(this, "Error registering loan.", Toast.LENGTH_SHORT).show();
        }
    }
}
