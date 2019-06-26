package com.example.diary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseIO extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "diary_database1.db";
    private static final int DATABASE_VERSION = 1;
    private String SQL_CREATE;

    private SQLiteDatabase database;


    DataBaseIO (Context context, String tableName) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQL_CREATE = "CREATE TABLE " + tableName +
                " ( _id INTEGER PRIMARY KEY, Date INTEGER, BodyText TEXT)";
        database = this.getReadableDatabase();
        database.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "  ( _id INTEGER PRIMARY KEY, Date INTEGER, BodyText TEXT)");
    }

    public void insertData (String tableName, int date, String bodyText) {
        deleteOldRecords(tableName, date);
        ContentValues values = new ContentValues();
        values.put("Date", date);
        values.put("BodyText", bodyText);
        database.insert(tableName, null, values);
    }

    public Cursor getAllItem(String tableName){
        Cursor cursor = database.query(
                tableName,
                new String[] {"Date", "BodyText"},
                null,
                null,
                null,
                null,
                "Date ASC"
        );
        return cursor;
    }

    public String readData (String tableName, int date) {
        String body = "";
        Cursor cursor = getCursorFromDate(tableName, date);
        if(cursor.moveToFirst()) {
            body = cursor.getString(0);
        }
        cursor.close();
        return body;
    }

    private void deleteOldRecords (String tableName, int date) {
        database.delete(tableName, "Date="+date, null);
    }

    private Cursor getCursorFromDate (String tableName, int date) {
        Cursor cursor = database.query(
                tableName,
                new String[] {"BodyText"},
                "Date=" + date,
                null,
                null,
                null,
                null
        );

        return cursor;
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        //SQLiteファイルがない場合テーブル作成
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        // アップデートの判別？
        onCreate(db);
    }
}
