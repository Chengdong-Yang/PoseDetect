package com.google.mlkit.vision.demo.java;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDataHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "SQUART";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_COUNT = "count";

    public MyDataHelper(@Nullable Context context) {
        super(context, "deep_squart.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql= "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_COUNT + " INTEGER);";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public String addOne(squart_model squart_model){
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_COUNT,squart_model.getCount());
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.insert(TABLE_NAME,COLUMN_COUNT,cv);
        long insert=sqLiteDatabase.insert(TABLE_NAME,COLUMN_COUNT,cv);
        if(insert==-1){
            return "fail";
        }
        sqLiteDatabase.close();
        return "success";
    }
    public boolean insertData(squart_model squart_model){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_COUNT,squart_model.getCount());
        long result=sqLiteDatabase.insert(TABLE_NAME,null,cv);
        if(result==-1){
            return false;
        }
        else{
            return true;
        }
    }
}
