package edu.uco.faaezcmakhdoomj.faaezcmakhdoomj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Faaez on 11/13/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Snake.db";
    public static final String TABLE_NAME = "score_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "SCORE";

    public static final String CONFIG_TABLE = "config_table";
    public static final String CONFIG_COL_1 = "SPEED";
    public static final String CONFIG_COL_2 = "AUTO";
    public static final String CONFIG_COL_3 = "WALLS";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SCORE INTEGER)");
        db.execSQL("create table " + CONFIG_TABLE +" (SPEED INTEGER, AUTO INTEGER, WALLS INTEGER)");
        db.execSQL("insert into " + CONFIG_TABLE +" (SPEED, AUTO, WALLS) VALUES (10, 0, 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+CONFIG_TABLE);
        onCreate(db);
    }

    public boolean insertData(String name,String score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,score);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" order by score desc",null);
        return res;
    }

    public boolean insertConfigData(int speed, boolean autoSpeed, boolean walls) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+CONFIG_TABLE);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONFIG_COL_1,speed);
        contentValues.put(CONFIG_COL_2,autoSpeed);
        contentValues.put(CONFIG_COL_3,walls);
        long result = db.insert(CONFIG_TABLE,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getConfigData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+CONFIG_TABLE,null);
        return res;
    }
}
