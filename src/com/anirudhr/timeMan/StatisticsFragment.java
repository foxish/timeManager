package com.anirudhr.timeMan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class StatisticsFragment extends SherlockFragmentActivity	{
	static String[] dummyData;
    static SimpleAdapter adapter;
    private static final int TODAY_CHART = Menu.FIRST;
	public static class CurrentListFragment extends SherlockListFragment	{
		private XmlUtilites utilities;
		@Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        setListShown(false);
        setEmptyText("No data");
        utilities = new XmlUtilites(getActivity());
        allowStaticAccess();
//		adapter = new SimpleAdapter(getActivity().getApplicationContext(), settings.getAllXML(), R.layout.stats_list_item, new String[]{Settings.TAG_DATE, Settings.TAG_UTIL}, new int[]{R.id.history_date, R.id.history_util});
//		setListAdapter(adapter);	
        }
		private void allowStaticAccess(){
	    //hackish    
			GlobalAccess.clf = this;
	        GlobalAccess.sa = adapter;
	        GlobalAccess.settings = utilities;
	        GlobalAccess.changeData();
		}
		@Override public void onListItemClick(ListView l, View v, int position, long id) {
            //launch detail-activity
			TextView tv = (TextView)v.findViewById(R.id.history_date);
			String date = (String) tv.getText();
			Intent pieChart = new PieChart().execute(this.getActivity().getApplicationContext(), utilities.getTasksHash(date), date);
			startActivity(pieChart);
			//Log.i("UserClick", "Item clicked: " + id);
        }
		public void onResume(){
			super.onResume();
			//settings.getAllXML();
			//settings.writeToXML();
			//globalAccess.changeData(settings, getActivity());
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			MenuItem projectItem = menu.add(Menu.NONE, TODAY_CHART, 0, "Today");
			projectItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			super.onCreateOptionsMenu(menu, inflater);
	    }
		
		@Override public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
            	case TODAY_CHART:
            		Intent pieChart = new PieChart().execute(this.getActivity().getApplicationContext(), utilities.getTasksHash(), "Today - Util(%) " + String.valueOf(utilities.getUtil()));
        			startActivity(pieChart);
            		break;
            }	
            return false;
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