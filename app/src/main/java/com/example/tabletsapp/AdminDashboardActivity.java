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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminDashboardActivity extends AppCompatActivity implements LoanAdapter.OnLoanReturnListener {

    // UI components for filtering
    private Spinner spinnerTabletBrand, spinnerCableType;
    private RecyclerView recyclerViewLoans;
    private LoanAdapter loanAdapter;
    private FloatingActionButton fabFilter;
    private TextView tvStartDate, tvEndDate;

    // List to hold the loans displayed in the RecyclerView
    private List<Loan> loanList = new ArrayList<>();

    // SimpleDateFormat for the database-stored date: "Jan 23, 2025 6:48:47 PM"
    private static final SimpleDateFormat DB_DATE_FORMAT =
            new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH);

    // SimpleDateFormat for the user-selected date: "YYYY-MM-DD"
    private static final SimpleDateFormat USER_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

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

        // Load all loans initially (no filters)
        loadLoans(null, null, null, null);

        // FloatingActionButton toggles visibility of the filter CardView
        fabFilter.setOnClickListener(v -> {
            CardView filterCard = findViewById(R.id.cardFilter);
            if (filterCard.getVisibility() == View.VISIBLE) {
                filterCard.setVisibility(View.GONE);  // Hide filter section
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
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete the loan record from the database
        String deleteQuery = "DELETE FROM " + DatabaseHelper.TABLE_LOANS +
                " WHERE " + DatabaseHelper.COLUMN_ID + " = ?";
        db.execSQL(deleteQuery, new String[]{String.valueOf(loan.getId())});
        db.close();

        // Remove the loan from the list and update the RecyclerView
        loanList.remove(position);
        loanAdapter.updateData(loanList);

        Log.d("AdminDashboard", "Loan with ID " + loan.getId() + " marked as returned.");
    }

    /**
     * Loads loans from the database with optional filters.
     *
     * @param tabletBrand The selected tablet brand (or null to ignore).
     * @param cableType   The selected cable type (or null to ignore).
     * @param startDate   The selected start date in "yyyy-MM-dd" (or null/empty to ignore).
     * @param endDate     The selected end date in "yyyy-MM-dd" (or null/empty to ignore).
     */
    private void loadLoans(String tabletBrand, String cableType, String startDate, String endDate) {
        // Clear the existing loan list
        loanList.clear();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Build the query for brand/cable type filtering only
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_LOANS + " WHERE 1=1";
        ArrayList<String> queryParams = new ArrayList<>();

        // Filter by brand if not empty/All
        if (tabletBrand != null && !tabletBrand.isEmpty() && !tabletBrand.equals("All")) {
            query += " AND " + DatabaseHelper.COLUMN_TABLET_BRAND + " = ?";
            queryParams.add(tabletBrand);
        }

        // Filter by cable type if not empty/All
        if (cableType != null && !cableType.isEmpty() && !cableType.equals("All")) {
            query += " AND " + DatabaseHelper.COLUMN_CABLE_TYPE + " = ?";
            queryParams.add(cableType);
        }

        // We do NOT filter by date in SQL because the stored format
        // ("Jan 23, 2025 6:48:47 PM") is not easily comparable in SQLite.
        // Instead, we fetch brand/cable results and then filter in Java.

        Cursor cursor = db.rawQuery(query, queryParams.toArray(new String[0]));

        // Parse the user-provided start/end into Date objects
        Date startDateObj = null, endDateObj = null;
        try {
            if (startDate != null && !startDate.isEmpty()) {
                startDateObj = USER_DATE_FORMAT.parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                // Adjust endDate to include the entire day, e.g. 23:59:59
                // if you want to be inclusive for that day
                endDateObj = USER_DATE_FORMAT.parse(endDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Now iterate through the cursor and filter by date in Java
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range")
                    int loanId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));

                    @SuppressLint("Range")
                    String dbTabletBrand = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TABLET_BRAND));

                    @SuppressLint("Range")
                    String dbCableType = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CABLE_TYPE));

                    @SuppressLint("Range")
                    String borrowerName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BORROWER_NAME));

                    @SuppressLint("Range")
                    String loanDateStr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LOAN_DATE));

                    // Parse the DB date string: "Jan 23, 2025 6:48:47 PM"
                    Date loanDateObj = null;
                    try {
                        if (loanDateStr != null && !loanDateStr.isEmpty()) {
                            loanDateObj = DB_DATE_FORMAT.parse(loanDateStr);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // If we have start/end filters, check them:
                    // Only add to loanList if loanDateObj is within [startDateObj, endDateObj].
                    boolean withinDateRange = true; // assume true unless filters say otherwise

                    if (loanDateObj != null) {
                        if (startDateObj != null && loanDateObj.before(startDateObj)) {
                            withinDateRange = false;
                        }
                        if (endDateObj != null && loanDateObj.after(endDateObj)) {
                            withinDateRange = false;
                        }
                    }
                    // If there's no valid loanDateObj, you can decide whether to
                    // include or exclude that row. Here, let's exclude it if we have a date filter:
                    else {
                        // If user has any date filter, let's exclude loans with no valid date.
                        if ((startDateObj != null) || (endDateObj != null)) {
                            withinDateRange = false;
                        }
                    }

                    if (withinDateRange) {
                        // Build the Loan object
                        Loan loan = new Loan(
                                loanId,
                                dbTabletBrand,
                                dbCableType,
                                borrowerName,
                                loanDateStr
                        );
                        loanList.add(loan);
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
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

        // Get the date range from TextViews
        String startDate = tvStartDate.getText().toString().trim();
        String endDate   = tvEndDate.getText().toString().trim();

        // Reload the loans with all filters
        loadLoans(tabletBrand, cableType, startDate, endDate);
    }

    private void showDatePicker(TextView targetTextView) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);

        // Show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date as yyyy-MM-dd
                    String formattedDate = String.format(
                            Locale.ENGLISH,
                            "%04d-%02d-%02d",
                            selectedYear, (selectedMonth + 1), selectedDay
                    );
                    targetTextView.setText(formattedDate);
                },
                year,
                month,
                day
        );

        datePickerDialog.show(); // Display the dialog
    }
}
