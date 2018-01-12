package com.example.janari.SimpleDailyBudgetApp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// This class is for user budget database
public class DBHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "BudgetData";
        public static final String TABLE_NAME = "Money";
        public static final String COL_1 = "ID";
        public static final String COL_2 = "DAILY_SUM";


        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,DAILY_SUM TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }

    public boolean insertDaily(String id,String daily_sum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,daily_sum);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
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

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public boolean updateSum(String id,String daily_sum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,daily_sum);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

        public Integer deleteData (String id) {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
        }
    public long budget(String id){

        String query = "SELECT DAILY_SUM" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.longForQuery(db, query, new String[]{ id });

    }

    public void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }
    }