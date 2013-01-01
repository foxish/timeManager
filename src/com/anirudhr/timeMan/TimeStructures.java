package com.anirudhr.timeMan;

// Contains all kinds of helper-methods

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.anirudhr.timeMan.db.MyTodoContentProvider;
import com.anirudhr.timeMan.db.TodoTable;

import android.app.Activity;
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
		int minutes = (seconds %3600)/60;
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
	
	public void killTimer(View v){
		mHandler.removeCallbacks(mUpdateTimeTask);
		final TextView time = (TextView)v.findViewById(R.id.todayTime);
		toggleColor(time, false);
	}
	
	//return true if day-threshold is hit and reset time has come
	//return false if timer need not be reset
	public boolean timeToReset(){
		if(getExpirationTime() > 0){
			//an expiration time is set
			if(getExpirationTime() > System.currentTimeMillis()){
				//expiration time is in the future compared to current time
				//no action needed
				return false;
			}else{
				if(getCurrentTaskStart() < getExpirationTime())
					setCurrentTaskStart(timeOfExpiration(PREVIOUS));
				setExpirationTime();
				return true;
			}
		}else{
			//set a new expiration time in the future
			setExpirationTime();
			return false;
		}
			
	}
	private long timeOfExpiration(int dayOffset) {
		//returns the NEXT time of expiration, unless dayOffset is negative
		//supply dayOffset as -1 or 0
		int hh = 3, mm = 05; //this is 24 hour time, set by user for reset of day-cycle
	    Calendar cal = Calendar.getInstance();
	    cal.setTimeInMillis(System.currentTimeMillis());
	    cal.set(Calendar.HOUR_OF_DAY, hh); //set hours
	    cal.set(Calendar.MINUTE, mm); // set minutes
	    cal.set(Calendar.SECOND, 0); //set seconds to 0
	    cal.set(Calendar.MILLISECOND, 0); //set ms to 0
	    if(cal.getTimeInMillis() < System.currentTimeMillis())
	    	cal.add(Calendar.DATE, 1);
	    cal.add(Calendar.DATE, dayOffset);
	    return cal.getTimeInMillis();
	}
	
	public String getDate(long millis) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
		return formatter.format(new Date(millis));
	}
	
	public long getExpirationTime() {
		return settings.getLong("expirationTime", 0);
	}

	public void setExpirationTime() {
		editor.putLong("expirationTime", timeOfExpiration(NEXT));
		editor.commit();
	}

	private static final String PREFS_NAME = "FoxTimer";
	public static final int NEXT = 0;
	public static final int PREVIOUS = -1;
}
