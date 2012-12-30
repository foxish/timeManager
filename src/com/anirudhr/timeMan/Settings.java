package com.anirudhr.timeMan;

import java.io.*;

import org.xmlpull.v1.XmlSerializer;

import com.anirudhr.timeMan.db.MyTodoContentProvider;
import com.anirudhr.timeMan.db.TodoTable;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

public class Settings {
	private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Activity a;
    private XmlSerializer xmlSerializer;
    private FileOutputStream fos;      
    private TimeStructures ts;
    private String xmlDate = null;
    
    public Settings(Activity activityArg){
    	settings = activityArg.getSharedPreferences(PREFS_NAME, 0);
    	editor = settings.edit();
    	this.a = activityArg;
    	ts = new TimeStructures(a);
    }
    public void writeToXML(){
    	String[] projection = {TodoTable.COLUMN_ACTIVITY , TodoTable.COLUMN_TIME };
	    Uri uri = Uri.parse(String.valueOf(MyTodoContentProvider.CONTENT_URI));
	    final Cursor cursor = a.getContentResolver().query(uri, projection, null, null, null);
	    
	    xmlDate = ts.getDate(ts.getCurrentTaskStart());
	    Runnable xmlCreate = new Runnable(){
			@Override
			public void run() {
				//initalize the xml write 
				try {
					initXML();
					for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
				        String activityName = cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_ACTIVITY));
				        String timeSpent = cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_TIME));
				        writeXMLContent(activityName, timeSpent);
					}
					cursor.close();
					endXML();
				}catch(Exception e){
					Log.d("fox", e.getMessage());
				}
			}
	    };
	    xmlCreate.run();
    }
    
    private void initXML() throws IllegalArgumentException, IllegalStateException, IOException {
    	String filename = "history.xml";
        	fos = a.getApplicationContext().openFileOutput(filename,Context.MODE_APPEND);
        
	    	xmlSerializer = Xml.newSerializer();
	    	xmlSerializer.setOutput(fos, "UTF-8");
	    	xmlSerializer.startDocument("UTF-8", true);
	    	xmlSerializer.startTag(ns, xmlDate);
	    	
    }
    private void writeXMLContent(String activityName, String timeSpent) throws IllegalArgumentException, IllegalStateException, IOException{
    	//oh lord forgive my indentation
		xmlSerializer.startTag(ns, "activity");
			xmlSerializer.startTag(ns, "name");
				xmlSerializer.text(activityName);
			xmlSerializer.endTag(ns, "name");
			xmlSerializer.startTag(ns, "time");
				xmlSerializer.text(timeSpent);
			xmlSerializer.endTag(ns, "time");
		xmlSerializer.endTag(ns, "activity");
    }
    
    private void endXML() throws IOException{
    	xmlSerializer.endTag(ns, xmlDate);
    	xmlSerializer.endDocument();
    	xmlSerializer.flush();
    	fos.close();
    }
    
    private static final String ns = null;
    private static final String PREFS_NAME = "FoxTimer";
}
