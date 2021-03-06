package com.anirudhr.timeMan;

// Contains all kinds of helper-methods

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.anirudhr.timeMan.db.MyTodoContentProvider;
import com.anirudhr.timeMan.db.TodoTable;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

public class TimeUtilites{
	private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Context a;
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask;
    
    public TimeUtilites(Context activityArg){
    	this.a = activityArg;
    	settings = a.getSharedPreferences(PREFS_NAME, 0);
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
	
	public String getCurrentTaskName(){
		String[] projection = { TodoTable.COLUMN_ACTIVITY };
		Uri uri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/" + getRunning());
		Cursor cursor = a.getContentResolver().query(uri, projection, null, null, null);
		 if (cursor != null) {
		      cursor.moveToFirst();
		      String ret =  cursor.getString((cursor.getColumnIndexOrThrow(TodoTable.COLUMN_ACTIVITY)));
		      cursor.close();
		      return ret;
		    }
		return null;
	}
	
	private void updateDatabase(long id){
		long time = getExpirationTime() - getCurrentTaskStart() + getOffsetFromDatabase(id);
		ContentValues values = new ContentValues();
		values.put(TodoTable.COLUMN_ID, id);
		values.put(TodoTable.COLUMN_TIME, time);
		Uri todoUri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/" + id);
		a.getContentResolver().update(todoUri, values, null, null);
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
		//here modify actionbar
		GlobalAccess.sha.getSupportActionBar().setTitle("TT - Running");
		
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
		GlobalAccess.sha.getSupportActionBar().setTitle("TT - Stopped");
		
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
				//set rollup time, for the xml to write into history
				setRollupTime(getCurrentTaskStart());
				
				//if a task is running, its time will be reset, flush changes to database first
				if(getRunning() > 0)
					updateDatabase(getRunning());
				
				//set new expiration time
				if(getCurrentTaskStart() < getExpirationTime()){
					setCurrentTaskStart(timeOfExpiration(PREVIOUS));
				}
				
				setExpirationTime();
				return true;
			}
		}else{
			//set a new expiration time in the future
			setExpirationTime();
			return false;
		}
	}
	
	private void setRollupTime(long time){
		editor.putLong("rollUp", time);//listid of 0 indicates no item
		editor.commit();
	}
	public long getRollupTime(){
		return settings.getLong("rollUp", getCurrentTaskStart());
	}
	
	private long timeOfExpiration(int dayOffset) {
		//returns the NEXT time of expiration, unless dayOffset is negative
		//supply dayOffset as -1 or 0
		int userResetTime = getResetTime();
		int hh = userResetTime/100, mm = userResetTime%100; //this is 24 hour time, set by user for reset of day-cycle
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
	
    public int getResetTime(){
    	Integer time = 0;
    	try{
    		time = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(a.getApplicationContext()).getString("resetTime", "0"), 10);
    		if(time/100 > 23 || time%100 > 60)
    			throw new NumberFormatException();
    	}
    	catch(NumberFormatException e){
    		time = 0;
    		PreferenceManager.getDefaultSharedPreferences(a.getApplicationContext()).edit().clear().commit();
    	}
    	return time;
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
