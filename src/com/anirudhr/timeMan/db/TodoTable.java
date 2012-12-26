package com.anirudhr.timeMan.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TodoTable {

  // Table1: MainTable
  public static final String TABLE_TIMERS = "todo";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_PRIORITY = "priority";
  public static final String COLUMN_ACTIVITY = "activity";
  public static final String COLUMN_TIME = "time";
  
  // Table creation SQL statement
  private static final String TIMELIST_CREATE = "create table " 
      + TABLE_TIMERS
      + "(" 
      + COLUMN_ID + " integer primary key autoincrement, " 
      + COLUMN_PRIORITY + " integer not null, " 
      + COLUMN_ACTIVITY + " text not null, " 
      + COLUMN_TIME + " integer not null " 
      + ");";
  

  public static void onCreate(SQLiteDatabase database) {
    database.execSQL(TIMELIST_CREATE);
  }

  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
      int newVersion) {
	  
    if(!(newVersion > oldVersion))
		return;

    Log.w(TodoTable.class.getName(), "Upgrading database from version "
        + oldVersion + " to " + newVersion
        + ", which will destroy all old data");
    database.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMERS);
    onCreate(database);
  }
} 