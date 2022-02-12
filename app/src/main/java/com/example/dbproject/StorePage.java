package com.example.dbproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class StorePage extends AppCompatActivity implements View.OnClickListener {
    Button buyBtn,toDBBtn,toStoreBtn;
    TableLayout dbOutput;
    TextView cartSum;
    DBHelper dbHelper;
    SQLiteDatabase database;
    int globalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_page);
        buyBtn=findViewById(R.id.buyBtn);
        buyBtn.setOnClickListener(this);
        toDBBtn=findViewById(R.id.toDBBtn);
        toDBBtn.setOnClickListener(this);
        toStoreBtn=findViewById(R.id.toStoreBtn);
        toStoreBtn.setOnClickListener(this);

        cartSum = findViewById(R.id.cartSum);

        dbOutput=findViewById(R.id.dbOutput);
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        UpdateTable();
        globalPrice=0;
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

                TextView outputName=new TextView(this);
                params.weight = 3.0f;
                outputName.setLayoutParams(params);
                outputName.setText(cursor.getString(nameIndex));
                dbOutputRow.addView(outputName);

                TextView outputPrice=new TextView(this);
                params.weight = 3.0f;
                outputPrice.setLayoutParams(params);
                String price=cursor.getString(priceIndex)+ " р.";
                outputPrice.setText(price);
                dbOutputRow.addView(outputPrice);

                Button toCartBtn=new Button(this);
                params.weight=1.0f;
                toCartBtn.setLayoutParams(params);
                toCartBtn.setText("Cart");
                toCartBtn.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(toCartBtn);
                toCartBtn.setOnClickListener(this);

                dbOutput.addView(dbOutputRow);

            } while (cursor.moveToNext());

        }
        cursor.close();
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buyBtn:
                Toast.makeText(this,"Вы заказали товаров на "+globalPrice+" р.",Toast.LENGTH_LONG).show();
                cartSum.setText("");
                globalPrice=0;
                break;
            case R.id.toDBBtn:
                Intent intent = new Intent(StorePage.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.toStoreBtn:
                UpdateTable();
                break;
            default:
                Cursor pricer = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

                int idPricerIndex = pricer.getColumnIndex(DBHelper.KEY_ID);
                int pricerIndex = pricer.getColumnIndex(DBHelper.KEY_PRICE);

                pricer.moveToFirst();
                do{
                    if (view.getId()==pricer.getInt(idPricerIndex)){
                        globalPrice+=pricer.getInt(pricerIndex);
                        cartSum.setText(globalPrice+getString(R.string.price_symbol));
                        break;
                    }
                }while (pricer.moveToNext());
                pricer.close();
        }
    }
}