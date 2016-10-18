package com.example.android.inventoryapp1.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {
    private InventoryContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp1";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class InventoryEntry implements BaseColumns {
        /** The content URI to access the Movie data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        /** Name of database table for Inventory */
        public final static String TABLE_NAME = "movies";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_MOVIE_NAME ="name";
        public final static String COLUMN_MOVIE_DESCRIPTION = "description";
        public final static String COLUMN_MOVIE_QTY = "quantity";
        public static final String COLUMN_MOVIE_PICTURE = "picture";
        public final static String COLUMN_MOVIE_PRICE = "price";
        public final static String COLUMN_MOVIE_RATING = "rating";
        public static final int RATING_UNKNOWN = 0;
        public static final int RATING_ACTION = 1;
        public static final int RATING_DRAMA = 2;
        public final static String COLUMN_SOLD = "soldButton";
        /**
         * Returns whether or not the given rating is {@link #RATING_UNKNOWN}, {@link #RATING_ACTION},
         * or {@link #RATING_DRAMA}.
         */
        public static boolean isValidRating(int rating) {
            if (rating == RATING_UNKNOWN || rating == RATING_ACTION || rating == RATING_DRAMA) {
                return true;
            }
            return false;
        }
    }
}
