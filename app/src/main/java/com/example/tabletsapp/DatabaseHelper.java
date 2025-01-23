package com.example.tabletsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "tabletRegistration.db";
    private static final int DATABASE_VERSION = 1;

    //Table Names
    public static final String TABLE_LOANS = "loans";
    public static final String TABLE_ADMINS = "admins";

    // Columns for Loans Table
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TABLET_BRAND = "tabletBrand";
    public static final String COLUMN_CABLE_TYPE = "cableType";
    public static final String COLUMN_BORROWER_NAME = "borrowerName";
    public static final String COLUMN_BORROWER_CONTACT = "borrowerContact";
    public static final String COLUMN_LOAN_DATE = "loanDate";

    //Columns for Admins Table
    public static final String COLUMN_ADMIN_USERNAME = "username";
    public static final String COLUMN_ADMIN_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Loans Table
        String createLoansTable = "CREATE TABLE " + TABLE_LOANS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TABLET_BRAND + " TEXT NOT NULL, " +
                COLUMN_CABLE_TYPE + " TEXT, " +
                COLUMN_BORROWER_NAME + " TEXT NOT NULL, " +
                COLUMN_BORROWER_CONTACT + " TEXT, " +
                COLUMN_LOAN_DATE + " TEXT NOT NULL" +
                ")";
        db.execSQL(createLoansTable);

        //Create Admin Table
        String createAdminsTable = "CREATE TABLE " + TABLE_ADMINS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ADMIN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_ADMIN_PASSWORD + " TEXT NOT NULL" +
                ")";
        db.execSQL(createAdminsTable);

        // Insert Default Admin
        db.execSQL("INSERT INTO " + TABLE_ADMINS + " (" + COLUMN_ADMIN_USERNAME + ", " + COLUMN_ADMIN_PASSWORD + ") VALUES ('admin', 'admin123')");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOANS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMINS);
        onCreate(db);
    }
}
