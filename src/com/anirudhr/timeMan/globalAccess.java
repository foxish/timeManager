package com.anirudhr.timeMan;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.anirudhr.timeMan.StatisticsFragment.CurrentListFragment;

import android.app.Activity;
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
}

