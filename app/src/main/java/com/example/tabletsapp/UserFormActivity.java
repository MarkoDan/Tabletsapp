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

    private Spinner spinnerTabletBrand;
    private RadioGroup radioGroupCable;
    private EditText etBorrowerName, etBorrowerContact;
    private Button btnSubmitLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        spinnerTabletBrand = findViewById(R.id.spinnerTabletBrand);
        radioGroupCable = findViewById(R.id.radioGroupCable);
        etBorrowerName = findViewById(R.id.etBorrowerName);
        etBorrowerContact = findViewById(R.id.etBorrowerContact);
        btnSubmitLoad = findViewById(R.id.btnSubmitLoan);

        btnSubmitLoad.setOnClickListener(v -> registerLoan());


    }

    private void registerLoan() {
        String tabletBrand = spinnerTabletBrand.getSelectedItem().toString();
        String borrowerName = etBorrowerName.getText().toString();
        String borrowerContact = etBorrowerContact.getText().toString();
        String cableType = null;

        int selectedCableId = radioGroupCable.getCheckedRadioButtonId();
        if(selectedCableId != -1) {
            cableType = ((RadioButton) findViewById(selectedCableId)).getText().toString();
        }

        if (tabletBrand.isEmpty() || borrowerName.isEmpty()) {
            Toast.makeText(this, "Tablet brand and borrower name are required", Toast.LENGTH_SHORT).show();
        }

        // Get current date and time
        String loanDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

        //Save to database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TABLET_BRAND, tabletBrand);
        values.put(DatabaseHelper.COLUMN_CABLE_TYPE, cableType);
        values.put(DatabaseHelper.COLUMN_BORROWER_NAME, borrowerName);
        values.put(DatabaseHelper.COLUMN_BORROWER_CONTACT, borrowerContact);
        values.put(DatabaseHelper.COLUMN_LOAN_DATE, loanDate);

        long result = db.insert(DatabaseHelper.TABLE_LOANS, null, values);

        if (result != -1) {
            Toast.makeText(this, "Loan registered successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Return to the previous screen
        } else {
            Toast.makeText(this, "Error registering loan.", Toast.LENGTH_SHORT).show();
        }

    }
}