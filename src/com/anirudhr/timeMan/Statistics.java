package com.anirudhr.timeMan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.text.StaticLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Statistics extends SherlockFragmentActivity	{
	static String[] dummyData;
    static SimpleAdapter adapter;
	public static class CurrentListFragment extends SherlockListFragment	{
		private Settings settings;
		@Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
        setListShown(false);
        setEmptyText("No data");
        settings = new Settings(getActivity());
        allowStaticAccess();
//		adapter = new SimpleAdapter(getActivity().getApplicationContext(), settings.getAllXML(), R.layout.stats_list_item, new String[]{Settings.TAG_DATE, Settings.TAG_UTIL}, new int[]{R.id.history_date, R.id.history_util});
//		setListAdapter(adapter);	
        }
		private void allowStaticAccess(){
	    //hackish    
			globalAccess.clf = this;
	        globalAccess.sa = adapter;
	        globalAccess.a = getActivity();
	        globalAccess.settings = settings;
	        globalAccess.changeData();
		}
		@Override public void onListItemClick(ListView l, View v, int position, long id) {
            //launch detail-activity
			Intent pieChart = new PieChart().execute(this.getActivity().getApplicationContext());
			startActivity(pieChart);
			Log.i("UserClick", "Item clicked: " + id);
        }
		public void onResume(){
			super.onResume();
			//settings.getAllXML();
			//settings.writeToXML();
			//globalAccess.changeData(settings, getActivity());
		}
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FragmentManager fm = getSupportFragmentManager();
		if (fm.findFragmentById(android.R.id.content) == null) {
			CurrentListFragment list = new CurrentListFragment();
			fm.beginTransaction().add(android.R.id.content, list).commit();
		}
	}
	
	
	
	
}