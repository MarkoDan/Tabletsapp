package com.example.tabletsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "tabletRegistration.db"; // Name of the database
    private static final int DATABASE_VERSION = 1; // Version number for database management

    // Table Names
    public static final String TABLE_LOANS = "loans"; // Table to store loan records
    public static final String TABLE_ADMINS = "admins"; // Table to store admin credentials

    // Columns for Loans Table
    public static final String COLUMN_ID = "id"; // Primary key for both tables
    public static final String COLUMN_TABLET_BRAND = "tabletBrand"; // Brand of the tablet being loaned
    public static final String COLUMN_CABLE_TYPE = "cableType"; // Type of cable provided with the tablet
    public static final String COLUMN_BORROWER_NAME = "borrowerName"; // Name of the person borrowing the tablet
    public static final String COLUMN_BORROWER_CONTACT = "borrowerContact"; // Contact info of the borrower
    public static final String COLUMN_LOAN_DATE = "loanDate"; // Date when the tablet was loaned

    // Columns for Admins Table
    public static final String COLUMN_ADMIN_USERNAME = "username"; // Admin username
    public static final String COLUMN_ADMIN_PASSWORD = "password"; // Admin password

    /**
     * Constructor for the DatabaseHelper.
     *
     * @param context The application context.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param db The SQLite database instance.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL query to create the Loans table
        String createLoansTable = "CREATE TABLE " + TABLE_LOANS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Auto-incrementing primary key
                COLUMN_TABLET_BRAND + " TEXT NOT NULL, " + // Tablet brand is required
                COLUMN_CABLE_TYPE + " TEXT, " + // Cable type is optional
                COLUMN_BORROWER_NAME + " TEXT NOT NULL, " + // Borrower name is required
                COLUMN_BORROWER_CONTACT + " TEXT, " + // Borrower contact is optional
                COLUMN_LOAN_DATE + " TEXT NOT NULL" + // Loan date is required
                ")";
        db.execSQL(createLoansTable); // Execute the query to create the Loans table

        // SQL query to create the Admins table
        String createAdminsTable = "CREATE TABLE " + TABLE_ADMINS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Auto-incrementing primary key
                COLUMN_ADMIN_USERNAME + " TEXT NOT NULL, " + // Admin username is required
                COLUMN_ADMIN_PASSWORD + " TEXT NOT NULL" + // Admin password is required
                ")";
        db.execSQL(createAdminsTable); // Execute the query to create the Admins table

        // Insert a default admin account into the Admins table
        db.execSQL("INSERT INTO " + TABLE_ADMINS + " (" + COLUMN_ADMIN_USERNAME + ", " + COLUMN_ADMIN_PASSWORD + ") VALUES ('admin', 'admin123')");
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db         The SQLite database instance.
     * @param oldVersion The current version of the database.
     * @param newVersion The new version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOANS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMINS);

        // Recreate the tables
        onCreate(db);
    }
}
