package com.example.abdallah.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.abdallah.inventoryapp.data.bookContract.bookEntry;
/**
 * Created by Abd Allah on 8/5/2018.
 */

public class bookDbHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME="book.db";

    public final static int DATABASE_VERSION=5;

    public bookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_STATEMENT="CREATE TABLE "+ bookEntry.TABLE_NAME+
                "("+bookEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                bookEntry.COLUMN_BOOK_NAME+" TEXT NOT NULL,"+
                bookEntry.COLUMN_BOOK_SUPPLIER_PHONE+" TEXT,"+
                bookEntry.COLUMN_BOOK_SUPPLIER_NAME+" TEXT,"+
                bookEntry.COLUMN_BOOK_SUPPLIER_Email+" TEXT,"+
                bookEntry.COLUMN_BOOK_PRICE+" INTEGER NOT NULL,"+
                bookEntry.COLUMN_BOOK_QUANTITY+" INTEGER NOT NULL  DEFAULT 0);";
        sqLiteDatabase.execSQL(SQL_CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
