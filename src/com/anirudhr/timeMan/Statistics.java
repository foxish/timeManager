package com.anirudhr.timeMan;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;

public class Statistics extends SherlockFragmentActivity	{
	static String[] dummyData;
	public static class CurrentListFragment extends SherlockListFragment	{
		@Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		
        setEmptyText("No accounts");
        setHasOptionsMenu(false);
	}
		@Override public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("UserClick", "Item clicked: " + id);
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