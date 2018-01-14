package com.example.janari.SimpleDailyBudgetApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// User input database
public class DataHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "InputData";
    public static final String TABLE_NAME = "Input";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "INCOME";
    public static final String COL_3 = "EXPENSES";
    public static final String COL_4 = "START_DATE";
    public static final String COL_5 = "END_DATE";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID TEXT, INCOME TEXT,EXPENSES TEXT,START_DATE TEXT,END_DATE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id, String income, String expenses, String start_date, String end_date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2,income);
        contentValues.put(COL_3,expenses);
        contentValues.put(COL_4,start_date);
        contentValues.put(COL_5,end_date);
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

    public String id(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        long recc=0;
        String rec=null;
        Cursor mCursor = db.rawQuery(
                "SELECT *  FROM  TABLE_NAME WHERE COL_1= '"+id+"'" , null);
        if (mCursor != null)
        {
            mCursor.moveToFirst();
            recc=mCursor.getLong(0);
            rec=String.valueOf(recc);
        }
        return rec;
    }
    public boolean updateData(String id, String income, String expenses, String start_date, String end_date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1, id);
        contentValues.put(COL_2,income);
        contentValues.put(COL_3,expenses);
        contentValues.put(COL_4,start_date);
        contentValues.put(COL_5,end_date);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return true;
    }


    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }
    public void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }
    public Cursor AllIncome(String id){

        String query = "SELECT INCOME" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, new String[]{ id });
        return res;

    }
    public long Income(String id){

        String query = "SELECT INCOME" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.longForQuery(db, query, new String[]{ id });
    }
    public Cursor AllExpenses(String id){

        String query = "SELECT EXPENSES" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, new String[]{ id });
        return res;

    }
    public long Expenses(String id){

        String query = "SELECT EXPENSES" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.longForQuery(db, query, new String[]{ id });
    }
    public Cursor AllStart(String id){

        String query = "SELECT START_DATE" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, new String[]{ id });
        return res;

    }
    public long Start(String id){

        String query = "SELECT START_DATE" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.longForQuery(db, query, new String[]{ id });
    }

    public Cursor AllEnd(String id){

        String query = "SELECT END_DATE" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, new String[]{ id });
        return res;

    }
    public long End(String id){

        String query = "SELECT END_DATE" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.longForQuery(db, query, new String[]{ id });
    }
}

