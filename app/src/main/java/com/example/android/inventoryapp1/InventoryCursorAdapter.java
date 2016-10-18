package com.example.android.inventoryapp1;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp1.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter{
    private static final String LOG_TAG = InventoryCursorAdapter.class.getSimpleName();


    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button soldButton = (Button) view.findViewById(R.id.soldButton);

        final int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry._ID));
        int movieNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_MOVIE_NAME);
        int movieDescriptionColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_MOVIE_DESCRIPTION);
        final int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_MOVIE_QTY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_MOVIE_PRICE);
        int soldColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SOLD);

        String movieName = cursor.getString(movieNameColumnIndex);
        String movieDescription = cursor.getString(movieDescriptionColumnIndex);
        final int quantityColumn = cursor.getInt(quantityColumnIndex);
        int priceColumn = cursor.getInt(priceColumnIndex);
        int soldColumn = cursor.getInt(soldColumnIndex);

        //Update the textView with the attributes for the current movie
        nameTextView.setText(movieName);
        summaryTextView.setText(movieDescription);
        quantityTextView.setText(String.valueOf( quantityColumn));
        priceTextView.setText(String.valueOf( priceColumn));
        soldButton.setText(String.valueOf(soldColumn));

        if (TextUtils.isEmpty(movieDescription)) {
            movieDescription = context.getString(R.string.unknown_description);
        }

        // Update the TextViews with the attributes for the current movie
        nameTextView.setText(movieName);
        summaryTextView.setText("Description : " + movieDescription);
        quantityTextView.setText("Quantity : " + String.valueOf(quantityColumn));
        priceTextView.setText("Price : " + String.valueOf(priceColumn) + "$ each ");
        soldButton.setText("Updated qty: " + String.valueOf(soldColumn));


        soldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantityColumnIndex > 1) {
                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_MOVIE_QTY, quantityColumnIndex);
                    Uri mCurrentInventoryUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, rowId);
                    int rowsAffected = context.getContentResolver().update(mCurrentInventoryUri, values, null, null);

                    if (rowsAffected == 0) {
                        Toast.makeText(context.getApplicationContext(), "Error with sales tracker update", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context.getApplicationContext(), "Sales tracker updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
