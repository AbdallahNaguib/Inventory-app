package com.example.abdallah.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Abd Allah on 8/8/2018.
 */

public class bookProvider extends ContentProvider {

    private bookDbHelper mDbHelper;

    private static final int BOOKS = 1;//the id for all the books uri matcher

    private static final int BOOK_ID = 2;//the id for the uri matcher if the uri has specific row

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Creating the uri matchers
     */
    static {
        sUriMatcher.addURI(bookContract.CONTENT_AUTHORITY,bookContract.bookEntry.TABLE_NAME,BOOKS);
        sUriMatcher.addURI(bookContract.CONTENT_AUTHORITY,bookContract.bookEntry.TABLE_NAME+"/#",BOOK_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper=new bookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection
            , String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database=mDbHelper.getReadableDatabase();
        int match=sUriMatcher.match(uri);
        Cursor cursor;
        switch (match){
            case BOOKS:
                //we need to get all the books in the database
                cursor=database.query(bookContract.bookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case BOOK_ID:
                //we need a specific book
                selection=bookContract.bookEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};//this gives us the id of the rwo that we want
                Log.e("query: ", "we are getting a specific book "+selectionArgs[0]);
                cursor=database.query(bookContract.bookEntry.TABLE_NAME,projection ,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException();//wrong uri
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);//setting a notification to know whenever the data is changed
        return cursor;
    }
    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return bookContract.bookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return bookContract.bookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    public void dataValidation(ContentValues values){
        //checking for every field in data to make sure that nothing is missing
        String name=values.getAsString(bookContract.bookEntry.COLUMN_BOOK_NAME);
        if(name == null){
            throw new IllegalArgumentException("book must have a name");
        }
        String email=values.getAsString(bookContract.bookEntry.COLUMN_BOOK_SUPPLIER_Email);
        if(email == null){
            throw new IllegalArgumentException("supplier's email must be provided");
        }
        String supplierName=values.getAsString(bookContract.bookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        if(supplierName == null){
            throw new IllegalArgumentException("supplier's name must be provided");
        }
        int price=values.getAsInteger(bookContract.bookEntry.COLUMN_BOOK_PRICE);
        if(price <= 0){
            throw new IllegalArgumentException("price must be positive");
        }

    }
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        int match=sUriMatcher.match(uri);
        dataValidation(contentValues);//checking the values before inserting them into the database
        switch (match){
            case BOOKS:
                return insertBook(uri,contentValues);
            default:
                throw new IllegalArgumentException();
        }
    }
    private Uri insertBook(Uri uri,ContentValues values){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id=db.insert(bookContract.bookEntry.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri,null);//setting a notifier to tell the cursor that a change has occured
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.e("delete: ", "I'm deleting now");
        getContext().getContentResolver().notifyChange(uri,null);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                return database.delete(bookContract.bookEntry.TABLE_NAME, selection, selectionArgs);
            case BOOK_ID:
                Log.e("delete: ", "I'm close");
                // Delete a single row given by the ID in the URI
                selection = bookContract.bookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(bookContract.bookEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK_ID:
                //updating can only be done with a specific row
                selection = bookContract.bookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
            return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        getContext().getContentResolver().notifyChange(uri,null);
        return database.update(bookContract.bookEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
