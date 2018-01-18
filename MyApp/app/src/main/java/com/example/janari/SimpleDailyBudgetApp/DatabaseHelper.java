package com.example.janari.SimpleDailyBudgetApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


//My user database class
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "UserData";
    public static final String TABLE_NAME = "User";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "EMAIL";
    public static final String COL_4 = "PASSWORD";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,EMAIL TEXT,PASSWORD TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public long insertData(String name,String email,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,email);
        contentValues.put(COL_4,password);
        long id = db.insert(TABLE_NAME,null ,contentValues);

        return id;
    }
    public long id(String email){

        String query = "SELECT ID" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_3 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.longForQuery(db, query, new String[]{ email });
    }
    public String name(String email){

        String query = "SELECT NAME" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_3 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.stringForQuery(db, query, new String[]{ email });
        }

    public boolean updateData(String id,String name,String email,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,email);
        contentValues.put(COL_4,password);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    // Method that checks if in one tables row are both entered email and entered password
    public boolean hasObject(String email, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectString = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_3 + "= ?"+ " AND "+ COL_4 + " =?";

        Cursor cursor = db.rawQuery(selectString,new String[]{email, password});
        boolean exist;
        if(cursor.getCount()>0){
            exist=true;
        } else {
            exist=false;
        }
        db.close();
        cursor.close();

        return exist;
    }
    public void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }


    public Cursor AllID(String email){

        String query = "SELECT ID" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_3 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, new String[]{ email });
        return res;

    }
    public long ID(String email){

        String query = "SELECT ID" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_3 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.longForQuery(db, query, new String[]{ email });
    }
    public Cursor AllName(String email){

        String query = "SELECT NAME" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_3 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, new String[]{ email });
        return res;

    }
    public String Name(String email){

        String query = "SELECT NAME" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_3 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.stringForQuery(db, query, new String[]{ email });
    }
    public Cursor viewEmail(String id){

        String query = "SELECT EMAIL" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, new String[]{ id });
        return res;

    }
    public String email(String id){

        String query = "SELECT EMAIL" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.stringForQuery(db, query, new String[]{ id });
    }

}
