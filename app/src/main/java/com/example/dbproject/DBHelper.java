package com.example.dbproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="storeDB";
    public static final String TABLE_CONTACTS="computerParts";

    public static final String KEY_ID="_id";
    public static final String KEY_NAME="name";
    public static final String KEY_PRICE="price";
    public DBHelper(@Nullable Context context){
        super(context,DATABASE_NAME ,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_CONTACTS+"("
                +KEY_ID+" integer primary key,"
                +KEY_NAME+ " text,"
                +KEY_PRICE+ " integer"+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists "+TABLE_CONTACTS);
        onCreate(db);
    }
}
