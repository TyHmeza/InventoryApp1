package com.example.android.inventoryapp1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp1.data.InventoryContract.InventoryEntry;
/**
 * Database helper for INVENTORY  app. Manages database creation and version management.
 */
public class InventoryDbHelper extends SQLiteOpenHelper{
        public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

        /** Name of the database file */
        private static final String DATABASE_NAME = "inventory.db";
        /**
         * Database version. If you change the database schema, you must increment the database version.
         */
        private static final int DATABASE_VERSION = 4;

        public InventoryDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * This is called when the database is created for the first time.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create a String that contains the SQL statement to create the movies table
            String SQL_CREATE_MOVIES_TABLE =  " CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                    + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + InventoryEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, "
                    + InventoryEntry.COLUMN_MOVIE_DESCRIPTION + " TEXT , "
                    + InventoryEntry.COLUMN_MOVIE_QTY + " INTEGER NOT NULL, "
                    + InventoryEntry.COLUMN_MOVIE_PRICE + " INTEGER NOT NULL, "
                    + InventoryEntry.COLUMN_MOVIE_RATING + " INTEGER NOT NULL DEFAULT 0, "
                    + InventoryEntry.COLUMN_MOVIE_PICTURE + " TEXT NOT NULL, "
                    + InventoryEntry.COLUMN_SOLD + " INTEGER NOT NULL "
                    + ");";
            // Execute the SQL statement
            db.execSQL(SQL_CREATE_MOVIES_TABLE);
        }

        /**
         * This is called when the database needs to be upgraded.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // The database is still at version 1, so there's nothing to do be done here.
                db.execSQL("DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME);
                onCreate(db);
        }
}
