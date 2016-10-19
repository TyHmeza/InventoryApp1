package com.example.android.inventoryapp1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.inventoryapp1.data.InventoryContract.InventoryEntry;

import java.io.FileDescriptor;
import java.io.IOException;
/**
 * Allows user to create a new movie or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private static final int EXISTING_MOVIE_LOADER = 0;
    private Uri mCurrentMovieUri;
    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private Spinner mRatingSpinner;
    private static final int PICK_IMAGE_REQUEST = 0;
    private ImageView mImageView;
    private TextView mTextView;
    private Uri mUri;
    private int mRating = InventoryEntry.RATING_UNKNOWN;
    /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
    private boolean mMovieHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mMovieHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mMovieHasChanged = true;
            return false;
        }
    };

    private EditText mSoldEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentMovieUri = intent.getData();
        if (mCurrentMovieUri == null) {
            // This is a new movie, so change the app bar to say "Add a Movie"
            setTitle(getString(R.string.editor_activity_title_new_movie));
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
            setTitle(getString(R.string.editor_activity_title_edit_movie));
            getLoaderManager().initLoader(EXISTING_MOVIE_LOADER, null, this);
        }
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_movie_name);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_movie_description);
        mQuantityEditText = (EditText) findViewById(R.id.edit_movie_qty);
        mPriceEditText = (EditText) findViewById(R.id.edit_movie_price);
        mRatingSpinner = (Spinner) findViewById(R.id.spinner_rating);

        mTextView = (TextView) findViewById(R.id.image_uri);
        mImageView = (ImageView) findViewById(R.id.image);
        mSoldEditText = (EditText) findViewById(R.id.edit_sale_movie);

        mNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mRatingSpinner.setOnTouchListener(mTouchListener);
        setupSpinner();
    }

    public void openImageSelector(View view) {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                mUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mUri.toString());
                mTextView.setText(mUri.toString());
                mImageView.setImageBitmap(getBitmapFromUri(mUri));
            }
        }
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

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_rating_options, android.R.layout.simple_spinner_item);
        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        mRatingSpinner.setAdapter(genderSpinnerAdapter);
        // Set the integer mSelected to the constant values
        mRatingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.rating_action))) {
                        mRating = InventoryEntry.RATING_ACTION;
                    } else if (selection.equals(getString(R.string.rating_drama))) {
                        mRating = InventoryEntry.RATING_DRAMA;
                    } else {
                        mRating = InventoryEntry.RATING_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mRating = InventoryEntry.RATING_UNKNOWN;
            }
        });
    }
    /**
     * Get user input from editor and save Movie into database.
     */
    private void saveMovie() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        // Check if this is supposed to be a new movie
        // and check if all the fields in the editor are blank
        if (mCurrentMovieUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(descriptionString) &&
                TextUtils.isEmpty(priceString) && mRating == InventoryEntry.RATING_UNKNOWN &&
                TextUtils.isEmpty(mUri.toString())
                )
        {
            return;
        }
        String soldString = mSoldEditText.getText().toString().trim();//NEW

        // Create a ContentValues object where column names are the keys,
        // and movie attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_MOVIE_NAME, nameString);
        values.put(InventoryEntry.COLUMN_MOVIE_DESCRIPTION, descriptionString);
        values.put(InventoryEntry.COLUMN_MOVIE_RATING, mRating);
        values.put(InventoryEntry.COLUMN_MOVIE_PICTURE, mUri.toString());
        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(InventoryEntry.COLUMN_MOVIE_PRICE, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(priceString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(InventoryEntry.COLUMN_MOVIE_QTY, quantity);
        values.put(InventoryEntry.COLUMN_SOLD, soldString);

        // Determine if this is a new or existing pet by checking if mCurrentMovieUri is null or not
        if (mCurrentMovieUri == null) {
            // This is a NEW movie, so insert a new movie into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_movie_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_movie_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentMovieUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_movie_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_movie_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new movie, hide the "Delete" menu item.
        if (mCurrentMovieUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save movie to database
                saveMovie();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mMovieHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the movie hasn't changed, continue with handling back button press
        if (!mMovieHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all movie attributes, define a projection that contains
        // all columns from the movie table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_MOVIE_NAME,
                InventoryEntry.COLUMN_MOVIE_DESCRIPTION,
                InventoryEntry.COLUMN_MOVIE_QTY,
                InventoryEntry.COLUMN_MOVIE_PRICE,
                InventoryEntry.COLUMN_MOVIE_RATING,
                InventoryEntry.COLUMN_MOVIE_PICTURE,
                InventoryEntry.COLUMN_SOLD,
        };


        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentMovieUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of Movie attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_MOVIE_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_MOVIE_DESCRIPTION);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_MOVIE_QTY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_MOVIE_PRICE);
            int ratingColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_MOVIE_RATING);
            int pictureColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_MOVIE_PICTURE);
            int soldColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SOLD);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int rating = cursor.getInt(ratingColumnIndex);
            mUri = Uri.parse(cursor.getString(pictureColumnIndex));
            int sold = cursor.getInt(soldColumnIndex);

            // Update the views on the screen with the values from the database in the Editor
            mNameEditText.setText(name);
            mDescriptionEditText.setText( description);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));
            mImageView.setImageBitmap(getBitmapFromUri(mUri));
            switch (rating) {
                case InventoryEntry.RATING_ACTION:
                    mRatingSpinner.setSelection(1);
                    break;
                case InventoryEntry.RATING_DRAMA:
                    mRatingSpinner.setSelection(2);
                    break;
                default:
                    mRatingSpinner.setSelection(0);
                    break;
            }
            mSoldEditText.setText(Integer.toString(sold));
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mRatingSpinner.setSelection(0);
        mSoldEditText.setText("");
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteMovie();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteMovie() {
        // Only perform the delete if this is an existing Movie.
        if (mCurrentMovieUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentMovieUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public void submitOrder(View view) {
        EditText nameField = (EditText)findViewById(R.id.edit_movie_name);
        String name = nameField.getText().toString();

        EditText qtyField = (EditText)findViewById(R.id.edit_movie_qty);
        String quantityField = qtyField.getText().toString();

        EditText pField = (EditText)findViewById(R.id.edit_movie_price);
        String priceField = pField.getText().toString();

        String priceMessage = createOrderSummary(name, quantityField, priceField);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "Movie order for " + name );
        intent.putExtra(Intent.EXTRA_TEXT, priceMessage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private String createOrderSummary(String name, String quantityField, String price) {
        String priceMessage = "Name of Movie : " + name ;
        priceMessage += "\nQuantity to order : " + quantityField;
        priceMessage += "\nTotal Price : $ " + price;
        priceMessage += "\n" + getString(R.string.thank_you);
        return priceMessage;
    }
}