package com.anirudhr.timeMan;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.anirudhr.timeMan.StatisticsFragment.CurrentListFragment;
import com.anirudhr.timeMan.widget.WidgetProvider;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;
import android.widget.SimpleAdapter;


//utility class, to access the list in Statistics elsewhere, to call a refresh
//TODO: Really bad way, fix if time
public class GlobalAccess {
	public static SimpleAdapter sa;
	public static CurrentListFragment clf;
	public static Activity a;
	public static XmlUtilites settings;
	public static void changeData(){
		sa = null;
		sa = new SimpleAdapter(a.getApplicationContext(), settings.getAllXML(), R.layout.stats_list_item, new String[]{XmlUtilites.TAG_DATE, XmlUtilites.TAG_UTIL}, new int[]{R.id.history_date, R.id.history_util});
		clf.setListAdapter(sa);
	}
	//for changing actionbar title
	public static SherlockFragmentActivity sha;
	
	
	private static Context c;
	//widget-stuff
    public static void updateWidget(Context context){
    	c = context;
    	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    	RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
    	ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
    	remoteViews.setTextViewText(R.id.widget_currtask, getCurrentTaskName());
    	remoteViews.setTextViewText(R.id.widget_util, getCurrentUtil());
    	appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }
	private static String getCurrentUtil() {
		XmlUtilites xu = new XmlUtilites(c);
		xu.getTasksHash();
		return String.valueOf(xu.getUtil());
	}
	
	private static String getCurrentTaskName() {
		TimeUtilites tu = new TimeUtilites(c);
		if(tu.getRunning() <= 0){
			return "-";
		}else{
			return "(" + tu.getTimeString(tu.getCurrentTaskStart(), System.currentTimeMillis(), tu.getOffsetFromDatabase(tu.getRunning())) + ") " + tu.getCurrentTaskName();
		}
	}
	
}

