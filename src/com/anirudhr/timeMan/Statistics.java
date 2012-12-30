package com.anirudhr.timeMan;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Statistics extends SherlockFragmentActivity	{
	static String[] dummyData;
	public static class CurrentListFragment extends SherlockListFragment	{
		@Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
        setListShown(false);
        setEmptyText("No data");
		}
		@Override public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("UserClick", "Item clicked: " + id);
        }
		public void onResume(){
			super.onResume();
			
			Settings sett = new Settings(getActivity());
			sett.writeToXML();
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