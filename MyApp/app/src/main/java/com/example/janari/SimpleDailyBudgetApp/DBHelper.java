package com.example.janari.SimpleDailyBudgetApp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// This class is for user budget database
public class DBHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "BudgetData";
        public static final String TABLE_NAME = "Money";
        public static final String COL_1 = "ID";
        public static final String COL_2 = "DAILY_SUM";
        public static final String COL_3 = "START_DATE";
        public static final String COL_4 = "END_DATE";

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,DAILY_SUM TEXT,START_DATE TEXT,END_DATE TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }

        public boolean insertData(String daily_sum,String start_date, String end_date) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2,daily_sum);
            contentValues.put(COL_3,start_date);
            contentValues.put(COL_4,end_date);
            long result = db.insert(TABLE_NAME,null ,contentValues);
            if(result == -1)
                return false;
            else
                return true;
        }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

        public boolean updateData(String id,String daily_sum,String start_date,String end_date) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1,id);
            contentValues.put(COL_2,daily_sum);
            contentValues.put(COL_3,start_date);
            contentValues.put(COL_4,end_date);
            db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
            return true;
        }

        public Integer deleteData (String id) {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
        }
    }