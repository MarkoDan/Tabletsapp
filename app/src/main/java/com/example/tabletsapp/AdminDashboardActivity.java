package com.example.tabletsapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLoans;
    private LoanAdapter loanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_overview);

        recyclerViewLoans = findViewById(R.id.recyclerViewLoans);
        recyclerViewLoans.setLayoutManager(new LinearLayoutManager(this));

        loadLoans();
    }

    private void loadLoans() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Loan> loanList = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_LOANS;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String tabletBrand = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TABLET_BRAND));
                @SuppressLint("Range") String cableType = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CABLE_TYPE));
                @SuppressLint("Range") String borrowerName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BORROWER_NAME));
                @SuppressLint("Range") String loanDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LOAN_DATE));

                loanList.add(new Loan(id, tabletBrand, cableType, borrowerName, loanDate));
            }while (cursor.moveToLast());
        }

        cursor.close();
        db.close();

        if (loanList.isEmpty()) {
            Toast.makeText(this, "No loans found.", Toast.LENGTH_SHORT).show();
        }

        loanAdapter = new LoanAdapter(loanList);
        recyclerViewLoans.setAdapter(loanAdapter);
    }
}