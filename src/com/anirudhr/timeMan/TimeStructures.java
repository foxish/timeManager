package com.anirudhr.timeMan;

// Contains all kinds of helper-methods

import com.anirudhr.timeMan.db.MyTodoContentProvider;
import com.anirudhr.timeMan.db.TodoTable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TimeStructures{
	private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Activity a;
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask;
    
    public TimeStructures(Activity activityArg){
    	this.a = activityArg;
    	settings = activityArg.getSharedPreferences(PREFS_NAME, 0);
    	editor = settings.edit();
    }
	
    public long getRunning() {
		return settings.getLong("currentItem", 0);
	}

	public void setRunning(long listId) {
		editor.putLong("currentItem", listId);//listid of 0 indicates no item
		editor.commit();
	}
	
	public long getOffsetFromDatabase(long id){
		String[] projection = { TodoTable.COLUMN_TIME };
	    Uri uri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/" + id);
	    Cursor cursor = a.getContentResolver().query(uri, projection, null, null, null);
	    if (cursor != null) {
	      cursor.moveToFirst();
	      long ret =  cursor.getLong((cursor.getColumnIndexOrThrow(TodoTable.COLUMN_TIME)));
	      cursor.close();
	      return ret;
	    }
	    return 0;
	}

	public void toggleColor(TextView tv, boolean running){
		tv.setTextColor(running? Color.RED: Color.BLACK);
	}
	
	public String getTimeString(long startTime, long endTime, long offset){		
		
		long millis = endTime - startTime + offset;
		int seconds = (int) (millis / 1000);
		int hours = seconds/3600;
		int minutes = seconds / 60;
		seconds     = seconds % 60;
		
		StringBuilder timeString = new StringBuilder();
		timeString.append(hours < 10? "0" + hours: String.valueOf(hours));
		timeString.append(":");
		timeString.append(minutes < 10?  "0" + minutes: String.valueOf(minutes));
		timeString.append(":");
		timeString.append(seconds < 10? "0" + seconds: String.valueOf(seconds));

		return timeString.toString();
	}
	public Long getCurrentTaskStart() {
		return settings.getLong("currentTaskStart", System.currentTimeMillis());
	}

	public void setCurrentTaskStart(Long currentTaskStart) {
		editor.putLong("currentTaskStart", currentTaskStart);
		editor.commit();
	}
	
	public void startTimer(View v, final long start, long id){
		final long offset = getOffsetFromDatabase(id);
		
		if(mHandler!=null)
			mHandler.removeCallbacks(mUpdateTimeTask);			
		final TextView time = (TextView)v.findViewById(R.id.todayTime);
		
		toggleColor(time, true);
		mUpdateTimeTask = new Runnable() {
		public void run() {			
			time.setText(getTimeString(start, System.currentTimeMillis(), offset));
			mHandler.postDelayed(this, 200);
			}
		};
        mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	public void killTimer(){
		if(mHandler!=null)
			mHandler.removeCallbacks(mUpdateTimeTask);
	}
	
	public static final String PREFS_NAME = "FoxTimer";
}
