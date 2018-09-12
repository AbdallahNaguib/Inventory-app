package com.example.abdallah.inventoryapp;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.abdallah.inventoryapp.data.bookContract.bookEntry;

import java.util.Objects;

public class Edit_activity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{
    Button plus,min;
    TextView tvQuantity;
    EditText editBookName;
    EditText editSupplierName;
    EditText editPrice;
    EditText editEmail;
    EditText editPhone;
    boolean mBookHasChanged;
    Uri bookUri;
    String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        bookUri=getIntent().getData();
        if(bookUri != null){
            setTitle(getString(R.string.edit_book_title));
            getLoaderManager().initLoader(0,null,this);
        }else{
            setTitle(getString(R.string.add_book_title));
            invalidateOptionsMenu();
        }
        View.OnTouchListener mTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mBookHasChanged = true;
                return false;
            }
        };
        tvQuantity=(TextView)findViewById(R.id.quantity_tv);
        plus=(Button)findViewById(R.id.plus_button);
        min=(Button)findViewById(R.id.min_button);
        editBookName=(EditText)findViewById(R.id.edit_book_name);
        editSupplierName=(EditText)findViewById(R.id.edit_supplier_name);
        editPrice=(EditText)findViewById(R.id.edit_book_price);
        editEmail=(EditText)findViewById(R.id.edit_book_email);
        editPhone=(EditText)findViewById(R.id.edit_book_phone);

        editEmail.setOnTouchListener(mTouchListener);
        editPhone.setOnTouchListener(mTouchListener);
        editPrice.setOnTouchListener(mTouchListener);
        editSupplierName.setOnTouchListener(mTouchListener);
        editBookName.setOnTouchListener(mTouchListener);
        plus.setOnTouchListener(mTouchListener);
        min.setOnTouchListener(mTouchListener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curr=Integer.parseInt(tvQuantity.getText().toString());
                curr++;
                tvQuantity.setText(Integer.toString(curr));
            }
        });
        min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curr=Integer.parseInt(tvQuantity.getText().toString());
                curr--;
                if(curr<0){
                    Toast.makeText(Edit_activity.this
                            ,"you can't offer less than zero",Toast.LENGTH_SHORT).show();
                }else {
                    tvQuantity.setText(Integer.toString(curr));
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (bookUri == null) {
            MenuItem deleteItem = menu.findItem(R.id.edit_menu_delete);
            MenuItem contactItem=menu.findItem(R.id.contact);
            deleteItem.setVisible(false);
            contactItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    public void upButton(){
        if (!mBookHasChanged) {
            NavUtils.navigateUpFromSameTask(Edit_activity.this);
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(Edit_activity.this);
                    }
                };

        // Show a dialog that notifies the user they have unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.dialog_can, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_menu_save:
                if(saveBook()) {
                    finish();
                }
                break;
            case R.id.edit_menu_delete:
                showDeleteConfirmationDialog();
                break;
            case android.R.id.home:
                upButton();
                break;
            case R.id.contact:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
        }
        return true;
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialoge_msg);
        builder.setPositiveButton(R.string.dialog_del, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.dialog_can, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        // Only perform the delete if this is an existing pet.
        Log.e("deletePet: ", "deletes now");
        if (bookUri != null) {
            Log.e("deletePet: ",bookUri.toString() );
            int rowsDeleted = getContentResolver().delete(bookUri, null, null);
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.error_del_book),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.deleted_successfully),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    public boolean saveBook(){
        String bookName=editBookName.getText().toString();
        String supplierName=editSupplierName.getText().toString();
        String phone=editPhone.getText().toString();
        String email=editEmail.getText().toString();
        if(Objects.equals(bookName.trim(), "") || Objects.equals(supplierName.trim(), "")
                || Objects.equals(phone.trim(), "") || Objects.equals(email.trim(), "")){
            Toast.makeText(this,"some fields weren't entered",Toast.LENGTH_LONG).show();
            return false;
        }
        if(email.length()>10) {
            if (!Objects.equals(email.substring((email.length() - 10)), "@gmail.com")) {
                Toast.makeText(this, R.string.wrong_email, Toast.LENGTH_LONG).show();
                return false;
            }
        }else{
            Toast.makeText(this, R.string.wrong_email, Toast.LENGTH_LONG).show();
            return false;
        }
        int price;
        try{
            price=Integer.parseInt(editPrice.getText().toString());
        }catch (Exception e){
            Toast.makeText(this,"price saved with 1",Toast.LENGTH_LONG);
            price=1;
        }
        int quantity=Integer.parseInt(tvQuantity.getText().toString());
        ContentValues values=new ContentValues();
        values.put(bookEntry.COLUMN_BOOK_NAME,bookName);
        values.put(bookEntry.COLUMN_BOOK_SUPPLIER_NAME,supplierName);
        values.put(bookEntry.COLUMN_BOOK_SUPPLIER_PHONE,phone);
        values.put(bookEntry.COLUMN_BOOK_SUPPLIER_Email,email);
        values.put(bookEntry.COLUMN_BOOK_PRICE,price);
        values.put(bookEntry.COLUMN_BOOK_QUANTITY,quantity);
        Log.e("saveBook: ", "saved");
        if(bookUri == null) {
            getContentResolver().insert(bookEntry.CONTENT_URI, values);
        }else{
            getContentResolver().update(bookUri,values,null,null);
        }
        Toast.makeText(this, "book saved successfully", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String projection[]={bookEntry._ID,
                bookEntry.COLUMN_BOOK_NAME,
                bookEntry.COLUMN_BOOK_PRICE,
                bookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                bookEntry.COLUMN_BOOK_QUANTITY,
                bookEntry.COLUMN_BOOK_SUPPLIER_Email,
                bookEntry.COLUMN_BOOK_SUPPLIER_PHONE};
        Log.e("Loading now ",projection[3]);
        return new CursorLoader(this,
                bookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            int nameIndex = data.getColumnIndex(bookEntry.COLUMN_BOOK_NAME);
            editBookName.setText(data.getString(nameIndex));

            int supplierIndex = data.getColumnIndex(bookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            editSupplierName.setText(data.getString(supplierIndex));

            int phoneIndex = data.getColumnIndex(bookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            editPhone.setText(data.getString(phoneIndex));
            phoneNumber=data.getString(phoneIndex);
            int priceIndex = data.getColumnIndex(bookEntry.COLUMN_BOOK_PRICE);
            editPrice.setText(data.getString(priceIndex));

            int emailIndex = data.getColumnIndex(bookEntry.COLUMN_BOOK_SUPPLIER_Email);
            editEmail.setText(data.getString(emailIndex));

            int quantityIndex = data.getColumnIndex(bookEntry.COLUMN_BOOK_QUANTITY);
            tvQuantity.setText(data.getString(quantityIndex));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
