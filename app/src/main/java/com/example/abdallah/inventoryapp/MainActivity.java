package com.example.abdallah.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.app.LoaderManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.abdallah.inventoryapp.data.bookContract.bookEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    BookCursorAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCursorAdapter=new BookCursorAdapter(this,null);
        ListView listView=(ListView)findViewById(R.id.lst_view);
        View emptyView=findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        listView.setAdapter(mCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent=new Intent(MainActivity.this,Edit_activity.class);
                intent.setData(ContentUris.withAppendedId(bookEntry.CONTENT_URI,l));
                startActivity(intent);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Edit_activity.class);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(0,null,this);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_msg);
        builder.setPositiveButton(R.string.dialog_del, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.dialog_can, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteBook(){
        getContentResolver().delete(bookEntry.CONTENT_URI,null,null);
    }
    public void insertDummyData(){
        ContentValues values=new ContentValues();
        values.put(bookEntry.COLUMN_BOOK_NAME,"toto");
        values.put(bookEntry.COLUMN_BOOK_PRICE,2);
        values.put(bookEntry.COLUMN_BOOK_QUANTITY,5);
        values.put(bookEntry.COLUMN_BOOK_SUPPLIER_NAME,"seller");
        values.put(bookEntry.COLUMN_BOOK_SUPPLIER_PHONE,"12345");
        values.put(bookEntry.COLUMN_BOOK_SUPPLIER_Email,"ahmed@gmail.com");
        getContentResolver().insert(bookEntry.CONTENT_URI,values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.catalog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert:
                insertDummyData();
                break;
            case R.id.delete_all:
                showDeleteConfirmationDialog();
                break;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection={
                bookEntry._ID,
               bookEntry.COLUMN_BOOK_NAME,
               bookEntry.COLUMN_BOOK_SUPPLIER_NAME,
               bookEntry.COLUMN_BOOK_PRICE,
               bookEntry.COLUMN_BOOK_QUANTITY
        };
        return new CursorLoader(this,bookEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
