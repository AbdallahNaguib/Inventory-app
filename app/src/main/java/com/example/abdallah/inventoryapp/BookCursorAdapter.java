package com.example.abdallah.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdallah.inventoryapp.data.bookContract;
import com.example.abdallah.inventoryapp.data.bookDbHelper;

import java.util.zip.Inflater;

/**
 * Created by Abd Allah on 8/8/2018.
 */

public class BookCursorAdapter extends CursorAdapter {
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, final Context context,final Cursor cursor) {
        TextView tvName=(TextView) view.findViewById(R.id.book_name);
        TextView tvSupplierName=(TextView) view.findViewById(R.id.supplier_name);
        TextView tvPrice=(TextView) view.findViewById(R.id.price);
        final TextView tvQuantity=(TextView) view.findViewById(R.id.quantity);
        final int id=cursor.getInt(cursor.getColumnIndex(bookContract.bookEntry._ID));
        String name=cursor.getString(cursor.getColumnIndex(bookContract.bookEntry.COLUMN_BOOK_NAME));
        String supplierName=cursor.getString(cursor.getColumnIndex(bookContract.bookEntry.COLUMN_BOOK_SUPPLIER_NAME));
        int price=cursor.getInt(cursor.getColumnIndex(bookContract.bookEntry.COLUMN_BOOK_PRICE));
        int quant=cursor.getInt(cursor.getColumnIndex(bookContract.bookEntry.COLUMN_BOOK_QUANTITY));
        tvName.setText(name);
        tvSupplierName.setText(supplierName);
        tvPrice.setText(price+" $");
        tvQuantity.setText("only "+quant+" left");
        Button button=(Button)view.findViewById(R.id.buy_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * I had to access the database directly cause I couldn't find any way to get a content
                 * resolver in this class , so please if there is way to do it tell me in the review
                 */
                String[] word=tvQuantity.getText().toString().split(" ");
                int quantity=Integer.parseInt(word[1]);
                /**
                 * I couldn't use the outer quant variable cause I wanted to decrease it
                 * every time the button was clicked which i couldn't do cause the var would
                 * have been final
                 */
                if(quantity == 0){
                    Toast.makeText(context,"product is finished",Toast.LENGTH_LONG).show();
                    bookDbHelper mDbHelper=new bookDbHelper(context);
                    SQLiteDatabase database = mDbHelper.getWritableDatabase();
                    String selection = bookContract.bookEntry._ID + "=?";
                    /**
                     * When I delete the item the app must be closed and opened again so that this
                     * product gets out of the list shown to the user cause I couldn't send a
                     * notification in this class , so please provide me with a solution in
                     * the review
                     */
                    String[] selectionArgs = new String[] {String.valueOf(id)};//I already know that it is one
                    database.delete(bookContract.bookEntry.TABLE_NAME, selection, selectionArgs);
                }else {
                    quantity--;
                    tvQuantity.setText("only " + quantity + " left");
                    ContentValues values = new ContentValues();
                    values.put(bookContract.bookEntry.COLUMN_BOOK_QUANTITY, quantity);
                    String selection = bookContract.bookEntry._ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(id)};
                    bookDbHelper mDbHelper = new bookDbHelper(context);
                    SQLiteDatabase database = mDbHelper.getWritableDatabase();
                    database.update(bookContract.bookEntry.TABLE_NAME, values, selection, selectionArgs);
                }
            }
        });
    }
}
