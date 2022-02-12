package com.example.dbproject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button dbAdd, dbClear,toStoreBtn, toDBBtn;
    EditText dbName, dbPrice;
    ConstraintLayout mainLayout;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toStoreBtn = findViewById(R.id.toStoreBtn);
        toStoreBtn.setOnClickListener(this);
        toDBBtn = findViewById(R.id.toDBBtn);
        toDBBtn.setOnClickListener(this);

        mainLayout = findViewById(R.id.mainLayout);

        dbName = findViewById(R.id.dbName);
        dbPrice = findViewById(R.id.dbPrice);

        dbAdd = findViewById(R.id.dbAdd);
        dbAdd.setOnClickListener(this);

        dbClear = findViewById(R.id.dbClear);
        dbClear.setOnClickListener(this);


        dbHelper = new DBHelper(this);
        database= dbHelper.getWritableDatabase();
        UpdateTable();
    }

    public void UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int priceIndex = cursor.getColumnIndex(DBHelper.KEY_PRICE);
            TableLayout dbOutput = findViewById(R.id.dbOutput);
            dbOutput.removeAllViews();
            do{
                TableRow dbOutputRow = new TableRow(this);
                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

                TextView outputID=new TextView(this);
                params.weight = 1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                dbOutputRow.addView(outputID);

                TextView outputName=new TextView(this);
                params.weight = 3.0f;
                outputName.setLayoutParams(params);
                outputName.setText(cursor.getString(nameIndex));
                dbOutputRow.addView(outputName);

                TextView outputMail=new TextView(this);
                params.weight = 3.0f;
                outputMail.setLayoutParams(params);
                outputMail.setText(cursor.getString(priceIndex));
                dbOutputRow.addView(outputMail);

                Button deleteBtn=new Button(this);
                deleteBtn.setOnClickListener(this);
                params.weight=1.0f;
                deleteBtn.setLayoutParams(params);
                deleteBtn.setText("Удалить запись");
                deleteBtn.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(deleteBtn);

                dbOutput.addView(dbOutputRow);

            } while (cursor.moveToNext());

        }

        cursor.close();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.dbAdd:
                String name = dbName.getText().toString();
                String price = dbPrice.getText().toString();

                contentValues = new ContentValues();

                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_PRICE, price);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);

                UpdateTable();
                break;

            case R.id.dbClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                TableLayout dbOutput = findViewById(R.id.dbOutput);
                dbOutput.removeAllViews();
                UpdateTable();
                break;
            case R.id.toStoreBtn:
                Intent intent = new Intent(MainActivity.this, StorePage.class);
                startActivity(intent);
                break;
            case R.id.toDBBtn:
                UpdateTable();
                break;
            default:
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();

                database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{String.valueOf((v.getId()))});

                contentValues = new ContentValues();
                Cursor cursorUpdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_NAME);
                    int priceIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_PRICE);
                    int realID=1;
                    do{
                        if(cursorUpdater.getInt(idIndex)>realID){
                            contentValues.put(DBHelper.KEY_ID,realID);
                            contentValues.put(DBHelper.KEY_NAME,cursorUpdater.getString(nameIndex));
                            contentValues.put(DBHelper.KEY_PRICE,cursorUpdater.getString(priceIndex));
                            database.replace(DBHelper.TABLE_CONTACTS, null, contentValues);
                        }
                        realID++;
                    }while (cursorUpdater.moveToNext());
                    if(cursorUpdater.moveToLast()){
                        database.delete(DBHelper.TABLE_CONTACTS,DBHelper.KEY_ID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                    }
                    UpdateTable();
                }
                cursorUpdater.close();
                break;
        }
    }
}