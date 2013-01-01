package com.anirudhr.timeMan;

import com.anirudhr.timeMan.Statistics.CurrentListFragment;

import android.app.Activity;
import android.widget.SimpleAdapter;


//utility class, to access the list in Statistics elsewhere, to call a refresh
//TODO: Really bad way, fix if time
public class globalAccess {
	public static SimpleAdapter sa;
	public static CurrentListFragment clf;
	public static Activity a;
	public static Settings settings;
	public static void changeData(){
		sa = null;
		sa = new SimpleAdapter(a.getApplicationContext(), settings.getAllXML(), R.layout.stats_list_item, new String[]{Settings.TAG_DATE, Settings.TAG_UTIL}, new int[]{R.id.history_date, R.id.history_util});
		clf.setListAdapter(sa);
	}
}

