package com.example.android.inventoryapp1;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp1.data.InventoryContract.InventoryEntry;

import java.io.FileDescriptor;
import java.io.IOException;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    /** Identifier for the Movie data loader */
    private static final int MOVIE_LOADER = 0;
    /** Adapter for the ListView */
    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView movieListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        movieListView.setEmptyView(emptyView);
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        movieListView.setAdapter(mCursorAdapter);
        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentMovieUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentMovieUri);
                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });
        // Kick off the loader
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded movie data into the database. For debugging purposes only.
     */
    private void insertMovie() {

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_MOVIE_NAME, "Superman");
        values.put(InventoryEntry.COLUMN_MOVIE_DESCRIPTION, "awesome movie");
        values.put(InventoryEntry.COLUMN_MOVIE_RATING, InventoryEntry.RATING_UNKNOWN);
        values.put(InventoryEntry.COLUMN_MOVIE_QTY, 7);
        values.put(InventoryEntry.COLUMN_MOVIE_PICTURE,0);
        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllMovies() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from Movie database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertMovie();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllMovies();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about. it shows on the screen
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_MOVIE_NAME,
                InventoryEntry.COLUMN_MOVIE_DESCRIPTION,
                InventoryEntry.COLUMN_MOVIE_QTY,
                InventoryEntry.COLUMN_MOVIE_PRICE,
                InventoryEntry.COLUMN_MOVIE_PICTURE,
                InventoryEntry.COLUMN_SOLD,
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                InventoryEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link InventoryCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    public Bitmap getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }


}
