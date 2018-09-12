package com.example.abdallah.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Abd Allah on 8/5/2018.
 */

public class bookContract {
    public final static String  CONTENT_AUTHORITY = "com.example.abdallah.books";
    public final static Uri BASE_CONTENT_URI =  Uri.parse("content://" + CONTENT_AUTHORITY);

    private bookContract(){}
    public static abstract class bookEntry implements BaseColumns {

        public final static String TABLE_NAME="books";
        public final static String _ID=BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME="name";
        public final static String COLUMN_BOOK_PRICE="price";
        public final static String COLUMN_BOOK_QUANTITY="quantity";
        public final static String COLUMN_BOOK_SUPPLIER_NAME="Supplier_name";
        public final static String COLUMN_BOOK_SUPPLIER_PHONE="Supplier_phone";
        public final static String COLUMN_BOOK_SUPPLIER_Email="Supplier_email";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;


    }
}
