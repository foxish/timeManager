package com.anirudhr.timeMan;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.anirudhr.timeMan.db.MyTodoContentProvider;
import com.anirudhr.timeMan.db.TodoTable;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class XmlUtilites {
    private Activity a;
    private XmlSerializer xmlSerializer;
    private FileOutputStream fos;      
    private TimeUtilites ts;
    private String xmlDate = null;
    
    private ArrayList<HashMap<String, String>> dayList = null;
    private HashMap<String, String> dayListItem = null;
    //calculation of util
    private long totalProductive = 0;
    private long totalTime = 0;
    
    //store values of hash(date:taskArrayList)
    private HashMap<String, Long> dayTasks;
    private HashMap<String, HashMap<String, Long>> dayTasksHash;
    
    //one string, for key
    private String key;
    
    
    public XmlUtilites(Activity activityArg){
    	this.a = activityArg;
    	ts = new TimeUtilites(a);
    }
    public void writeToXML(){
    	
//		XML SAMPLE FORMAT		
//		<day>
//			<date>31/12/2012</date>
//			<activity>
//				<name>Human</name>
//				<time>0</time>
//				<productive>0<productive>   			
//			</activity>
//			<activity>
//				<name>Android</name>
//				<time>581966</time>
//	    		<productive>1</productive>
//			</activity>
//			<util>10</util>
//		</day>
	    	
    	
    	String[] projection = {TodoTable.COLUMN_ACTIVITY , TodoTable.COLUMN_TIME, TodoTable.COLUMN_PRODUCTIVE };
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
				        long isProductive = cursor.getLong(cursor.getColumnIndex(TodoTable.COLUMN_PRODUCTIVE));
				        if(Long.parseLong(timeSpent) <= 60*1000)
				        	continue;
				        accumulateTime(Long.valueOf(timeSpent), isProductive);
				        writeXMLContent(activityName, timeSpent, isProductive);
					}
					cursor.close();
					endXML();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
	    };
	    xmlCreate.run();
    }
    
	
	public HashMap<String, Long> getTasksHash(String date){
		return dayTasksHash.get(date);
	}
	
	//get today
	public HashMap<String, Long> getTasksHash(){
		String[] projection = {TodoTable.COLUMN_ACTIVITY , TodoTable.COLUMN_TIME, TodoTable.COLUMN_PRODUCTIVE, TodoTable.COLUMN_ID};
	    Uri uri = Uri.parse(String.valueOf(MyTodoContentProvider.CONTENT_URI));
	    final HashMap<String, Long> dayTasks = new HashMap<String, Long>();
	    
		final Cursor cursor = a.getContentResolver().query(uri, projection, null, null, null);
		Runnable getToday = new Runnable(){
			@Override
			public void run() {
				try {
					for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
						long id = cursor.getLong(cursor.getColumnIndex(TodoTable.COLUMN_ID));
						String activityName = cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_ACTIVITY));
						long isProductive = cursor.getLong(cursor.getColumnIndex(TodoTable.COLUMN_PRODUCTIVE));
						long timeSpent = cursor.getLong(cursor.getColumnIndex(TodoTable.COLUMN_TIME));
						
						if(id == ts.getRunning())
							timeSpent += System.currentTimeMillis() - ts.getCurrentTaskStart();
						
				        if(timeSpent < 60*1000) //dont count if interval too small (<1min)
				        	continue;
				        
						accumulateTime(Long.valueOf(timeSpent), isProductive);
				        dayTasks.put(activityName, timeSpent);
					}
					cursor.close();
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
	    };
	    getToday.run();
		return dayTasks;
	}
	
	
	
    //#PRIVATE METHODS
    private void accumulateTime(Long timeSpent, long isProductive){
    	if(timeSpent <= 0)
    		return;
    	
    	if(isProductive == 1)
    		totalProductive+=timeSpent;
    	
    	totalTime+=timeSpent;
    }
    
    public double getUtil(){
    	
    	double util = (totalTime > 0) ? ((double)totalProductive / (double)totalTime)*100 : 0;
    	return RoundTo2Decimals(util);
    }
    
    private double RoundTo2Decimals(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        return Double.valueOf(df2.format(val));
    }
    
    private void initXML() throws IllegalArgumentException, IllegalStateException, IOException {
    	fos = a.getApplicationContext().openFileOutput(FILENAME,Context.MODE_APPEND);
    	xmlSerializer = Xml.newSerializer();
    	xmlSerializer.setOutput(fos, "UTF-8");
    	
    	//day tag encloses today's entry
    	xmlSerializer.startTag(ns, TAG_DAY);
		
    	//one entry, for date
    	xmlSerializer.startTag(ns, TAG_DATE);
		xmlSerializer.text(xmlDate);
		xmlSerializer.endTag(ns, TAG_DATE);
    }
    private void writeXMLContent(String activityName, String timeSpent, long isProductive) throws IllegalArgumentException, IllegalStateException, IOException{
    	//oh lord do forgive my indentation
    	xmlSerializer.startTag(ns, TAG_ACTIVITY);
	    	xmlSerializer.startTag(ns, TAG_NAME);
				xmlSerializer.text(activityName);
			xmlSerializer.endTag(ns, TAG_NAME);
			xmlSerializer.startTag(ns, TAG_TIME);
				xmlSerializer.text(timeSpent);
			xmlSerializer.endTag(ns, TAG_TIME);
			xmlSerializer.startTag(ns, TAG_PRODUCTIVE);
				xmlSerializer.text(String.valueOf(isProductive));
			xmlSerializer.endTag(ns, TAG_PRODUCTIVE);
		xmlSerializer.endTag(ns, TAG_ACTIVITY);
    }
    
    private void endXML() throws IOException{
    		xmlSerializer.startTag(ns, TAG_UTIL);
				xmlSerializer.text(String.valueOf(getUtil()));
			xmlSerializer.endTag(ns, TAG_UTIL);
    	xmlSerializer.endTag(ns, TAG_DAY);
    	xmlSerializer.flush();
    	fos.close();
    }
    
    @SuppressWarnings("static-access")
	public ArrayList<HashMap<String, String>> getAllXML() { 
		try{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			XmlPullParser parser = factory.newPullParser(); 
			InputStream fis = a.getApplicationContext().openFileInput(FILENAME);
			parser.setInput(fis, "UTF-8");  
			int eventType;// = parser.getEventType();
			do{
				eventType = parser.getEventType();
				if(eventType == parser.START_DOCUMENT) {
					dayList = new ArrayList<HashMap<String, String>>();
					dayTasksHash = new HashMap<String, HashMap<String,Long>>();
				} else if(eventType == parser.END_DOCUMENT) {
					//System.out.println("End document");
				} else if(eventType == parser.START_TAG) {
					processStartElement(parser);
				} else if(eventType == parser.END_TAG) {
					processEndElement(parser);
				} else if(eventType == parser.TEXT) {
					processText(parser);
				}
				parser.next();
			} while (eventType != XmlPullParser.END_DOCUMENT) ;
			return dayList;   
		}catch(Exception e){
			e.printStackTrace();	
			return new ArrayList<HashMap<String, String>>();
		}
	}

    private void processText(XmlPullParser xpp) {
    	//xpp.getText();
		
	}
	private void processEndElement(XmlPullParser xpp) {
		String name = xpp.getName();
		if(name.equalsIgnoreCase(TAG_DAY)){
			dayList.add(dayListItem);
			dayTasksHash.put(dayListItem.get(TAG_DATE), dayTasks);
		}
		//Log.d("fox", "End element: " + name);
		
	}
	private void processStartElement(XmlPullParser xpp) throws XmlPullParserException, IOException {
		String name = xpp.getName();
		if(name.equalsIgnoreCase(TAG_DAY)){
			dayListItem = new HashMap<String, String>();
			dayTasks = new HashMap<String, Long>();
		}
		else if(name.equalsIgnoreCase(TAG_DATE)){
			dayListItem.put(TAG_DATE, safeNextText(xpp));
		}
		else if(name.equalsIgnoreCase(TAG_UTIL)){
			dayListItem.put(TAG_UTIL, safeNextText(xpp));
		}else if(name.equals(TAG_NAME)){
			key = safeNextText(xpp);
		}else if(name.equals(TAG_TIME)){
			dayTasks.put(key, Long.valueOf(safeNextText(xpp)));
		}
		//Log.d("fox", "Start element: " + name);
	}
	
	  private String safeNextText(XmlPullParser parser)
	          throws XmlPullParserException, IOException {
	      String result = parser.nextText();
	      if (parser.getEventType() != XmlPullParser.END_TAG) {
	          parser.nextTag();
	      }
	      return result;
	  }

	private static final String ns = null;
    public static final String FILENAME = "history.xml";
    
    public static final String TAG_ACTIVITY = "activity";
    public static final String TAG_DAY = "day";
    public static final String TAG_DATE = "date";
    public static final String TAG_NAME = "name";
    public static final String TAG_TIME = "time";
    public static final String TAG_PRODUCTIVE = "productive";
    public static final String TAG_UTIL = "util";
}
