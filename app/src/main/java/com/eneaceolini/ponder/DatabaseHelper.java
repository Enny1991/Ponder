package com.eneaceolini.ponder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create personal table
        String create = "";
        create += "CREATE TABLE " + TableNotes.TABLE_NAME + " (";
        create += "  " + TableNotes.COLUMN_ID + " INTEGER PRIMARY KEY,";
        create += "  " + TableNotes.COLUMN_EMAIL + " TEXT NOT NULL,";
        create += "  " + TableNotes.COLUMN_PASSWORD + " TEXT NOT NULL,";
        create += "  " + TableNotes.COLUMN_LANGUAGE + " TEXT NOT NULL,";
        create += "  " + TableNotes.COLUMN_LOCATION + " TEXT,";
        create += "  " + TableNotes.COLUMN_TAGS + " TEXT";
        create += ")";
        db.execSQL(create);

        create = "";
        create += "CREATE TABLE " + TableNotes.TABLE_NAME_2 + " (";
        create += "  " + TableNotes.COLUMN_ID + " INTEGER PRIMARY KEY,";
        create += "  " + TableNotes.COLUMN_URL + " TEXT,";
        create += "  " + TableNotes.COLUMN_SERIES + " TEXT,";
        create += "  " + TableNotes.COLUMN_SPK + " TEXT,";
        create += "  " + TableNotes.COLUMN_FROM + " TEXT,";
        create += "  " + TableNotes.COLUMN_TITLE + " TEXT,";
        create += "  " + TableNotes.COLUMN_DATE + " TEXT,";
        create += "  " + TableNotes.COLUMN_TIME + " TEXT,";
        create += "  " + TableNotes.COLUMN_IMG + " BLOB,";
        create += "  " + TableNotes.COLUMN_ABSTRACT + " TEXT";
        create += ")";
        db.execSQL(create);




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // non previsto
    }

}
