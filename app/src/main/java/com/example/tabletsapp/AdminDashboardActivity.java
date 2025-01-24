package com.example.tabletsapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity implements LoanAdapter.OnLoanReturnListener {

    // UI components for filtering
    private Spinner spinnerTabletBrand, spinnerCableType;
    private RecyclerView recyclerViewLoans;
    private LoanAdapter loanAdapter;
    private FloatingActionButton fabFilter;
    private TextView tvStartDate, tvEndDate;

    // List to hold the loans displayed in the RecyclerView
    private List<Loan> loanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_overview);

        // Initialize UI components
        spinnerTabletBrand = findViewById(R.id.spinnerTabletBrand);
        spinnerCableType = findViewById(R.id.spinnerCableType);
        recyclerViewLoans = findViewById(R.id.recyclerViewLoans);
        fabFilter = findViewById(R.id.fabFilter);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        // Set click listeners for date selection
        tvStartDate.setOnClickListener(v -> showDatePicker(tvStartDate));
        tvEndDate.setOnClickListener(v -> showDatePicker(tvEndDate));

        // Set up RecyclerView with a LinearLayoutManager
        recyclerViewLoans.setLayoutManager(new LinearLayoutManager(this));

        // Load all loans initially without filters
        loadLoans(null, null);

        // Set up the FloatingActionButton to toggle the visibility of the filter section
        fabFilter.setOnClickListener(v -> {
            CardView filterCard = findViewById(R.id.cardFilter);
            if (filterCard.getVisibility() == View.VISIBLE) {
                filterCard.setVisibility(View.GONE); // Hide filter section
            } else {
                filterCard.setVisibility(View.VISIBLE); // Show filter section
            }
        });

        // Apply filters when the filter button is clicked
        Button btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> applyFilters());
    }

    @Override
    public void onLoanReturned(Loan loan, int position) {
        // Handle the "return loan" action

        // Access the database helper
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete the loan record from the database
        String deleteQuery = "DELETE FROM " + DatabaseHelper.TABLE_LOANS + " WHERE " + DatabaseHelper.COLUMN_ID + " = ?";
        db.execSQL(deleteQuery, new String[]{String.valueOf(loan.getId())});
        db.close();

        // Remove the loan from the list and update the RecyclerView
        loanList.remove(position);
        loanAdapter.updateData(loanList);
        Log.d("AdminDashboard", "Loan with ID " + loan.getId() + " marked as returned.");
    }

    private void loadLoans(String tabletBrand, String cableType) {
        // Clear the existing loan list
        loanList.clear();

        // Access the database helper and get a readable database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Build the query with optional filtering
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_LOANS + " WHERE 1=1";

        // Append conditions if filters are provided
        if (tabletBrand != null && !tabletBrand.isEmpty() && !tabletBrand.equals("All")) {
            query += " AND " + DatabaseHelper.COLUMN_TABLET_BRAND + " = ?";
        }
        if (cableType != null && !cableType.isEmpty() && !cableType.equals("All")) {
            query += " AND " + DatabaseHelper.COLUMN_CABLE_TYPE + " = ?";
        }

        // Build the list of query parameters
        ArrayList<String> queryParams = new ArrayList<>();
        if (tabletBrand != null && !tabletBrand.isEmpty() && !tabletBrand.equals("All")) {
            queryParams.add(tabletBrand);
        }
        if (cableType != null && !cableType.isEmpty() && !cableType.equals("All")) {
            queryParams.add(cableType);
        }

        // Execute the query
        Cursor cursor = db.rawQuery(query, queryParams.toArray(new String[0]));
        try {
            if (cursor != null && cursor.moveToFirst()) {
                // Process each row and create Loan objects
                do {
                    @SuppressLint("Range") Loan loan = new Loan(
                            cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TABLET_BRAND)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CABLE_TYPE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BORROWER_NAME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LOAN_DATE))
                    );
                    loanList.add(loan); // Add loan to the list
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close(); // Close the cursor to free resources
        }

        // Update the RecyclerView adapter
        if (loanAdapter == null) {
            loanAdapter = new LoanAdapter(loanList, this);
            recyclerViewLoans.setAdapter(loanAdapter);
        } else {
            loanAdapter.updateData(loanList);
        }
    }

    private void applyFilters() {
        // Get the selected filter values
        String tabletBrand = spinnerTabletBrand.getSelectedItem().toString();
        String cableType = spinnerCableType.getSelectedItem().toString();

        // Reload the loans with the selected filters
        loadLoans(tabletBrand, cableType);
    }

    private void showDatePicker(TextView targetTextView) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Format the selected date as YYYY-MM-DD
            String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
            targetTextView.setText(formattedDate); // Set the selected date to the TextView
        }, year, month, day);

        datePickerDialog.show(); // Display the dialog
    }
}
