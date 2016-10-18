package com.example.android.inventoryapp1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp1.data.InventoryContract.InventoryEntry;

public class InventoryProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final int MOVIES = 100;
    private static final int MOVIE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_MOVIES, MOVIES);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_MOVIES + "/#", MOVIE_ID);
    }

    /**
     * Database helper object
     */
    private InventoryDbHelper mDbHelper;
    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MOVIE_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return insertMovie(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a Movie into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertMovie(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(InventoryEntry.COLUMN_MOVIE_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Movie requires a name");
        }

        // Check that the rating is valid
        Integer rating = values.getAsInteger(InventoryEntry.COLUMN_MOVIE_RATING);
        if (rating == null || !InventoryEntry.isValidRating(rating)) {
            throw new IllegalArgumentException("Movie requires valid rating");
        }

        // If the quantity is provided, check that it's greater than or equal to 0
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_MOVIE_QTY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Movie requires total quantity");
        }

        // If the Price is provided, check that it's greater than or equal to 0
        Integer price = values.getAsInteger(InventoryEntry.COLUMN_MOVIE_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Movie requires valid price");
        }

        // Check that the name is not null
        String picture = values.getAsString(InventoryEntry.COLUMN_MOVIE_PICTURE);
        if (picture == null) {
            throw new IllegalArgumentException("Movie requires a picture");
        }

        // If the quantity is provided, check that it's greater than or equal to 0
        Integer sold = values.getAsInteger(InventoryEntry.COLUMN_SOLD);
        if (sold != null && sold < 0) {
            throw new IllegalArgumentException("Movie quantity soldButton");
        }



        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //notify all listeners that the data has changed for the pet content Uri
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    //Update method
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return updateMovie(uri, contentValues, selection, selectionArgs);
            case MOVIE_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMovie(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateMovie(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InventoryEntry.COLUMN_MOVIE_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_MOVIE_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Movie requires a name");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_MOVIE_DESCRIPTION)) {
            String name = values.getAsString(InventoryEntry.COLUMN_MOVIE_DESCRIPTION);
            if (name == null) {
                throw new IllegalArgumentException("Movie requires a Description");
            }
        }

        //Check the quantity
        if (values.containsKey(InventoryEntry.COLUMN_MOVIE_QTY)) {
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_MOVIE_QTY);
            if (quantity <= 0) {
                throw new IllegalArgumentException("Movie requires valid Quantity");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_MOVIE_RATING)) {
            Integer rating = values.getAsInteger(InventoryEntry.COLUMN_MOVIE_RATING);
            if (rating == null || !InventoryEntry.isValidRating(rating)) {
                throw new IllegalArgumentException("Movie requires valid Rating");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_MOVIE_PRICE)) {
            // Check that the price is greater than or equal to 0
            Double price = values.getAsDouble(InventoryEntry.COLUMN_MOVIE_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Movie requires valid price");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_MOVIE_PICTURE)) {
             String picture = values.getAsString(InventoryEntry.COLUMN_MOVIE_PICTURE);
            if (picture == null ) {
                throw new IllegalArgumentException("Movie requires a picture");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_SOLD)) {
            String sold = values.getAsString(InventoryEntry.COLUMN_SOLD);
            if (sold == null ) {
                throw new IllegalArgumentException("Sold");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeble database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        //Track the number of rows that were deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
            case MOVIE_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }
    @Override
    public String getType (Uri uri){
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case MOVIE_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

}
