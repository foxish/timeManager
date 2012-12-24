package com.anirudhr.timeMan;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.anirudhr.timeMan.R;
import com.anirudhr.timeMan.db.MyTodoContentProvider;
import com.anirudhr.timeMan.db.TodoTable;

public class TimeList extends SherlockFragmentActivity
{
    public static final String PREFS_NAME = "FoxTimer";
	static String[] dummyData;
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int ADD_PROJECT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;

	public static class CurrentListFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor>	{
        FoxListAdapter mAdapter;
        @Override public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        setEmptyText("No data");
            setHasOptionsMenu(true);
            registerForContextMenu(getListView());
            setListShown(false);
            getLoaderManager().initLoader(0, null, this);
	        setHasOptionsMenu(true);
		}	
		
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
		  ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, EDIT_ID, 1, R.string.menu_edit);
		}
		
		@Override
		public boolean onContextItemSelected(android.view.MenuItem item) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			switch (item.getItemId()) {
			case DELETE_ID:
			  Uri uri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/" + info.id);
			  getActivity().getContentResolver().delete(uri, null, null);
			  return true;
			case EDIT_ID:
			Intent i = new Intent(getActivity(), TimeDetailActivity.class);
			Uri todoUri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/" + info.id);
			i.putExtra(MyTodoContentProvider.CONTENT_ITEM_TYPE, todoUri);
			startActivityForResult(i, ACTIVITY_EDIT);
			}
			return super.onContextItemSelected(item);
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			MenuItem projectItem = menu.add(Menu.NONE, ADD_PROJECT_ID, 0, "Add Activity");
			projectItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	    }
		@Override public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("UserClick", "Item clicked: " + id);
			ContentValues values = new ContentValues();
			values.put(TodoTable.COLUMN_ID, id);
			values.put(TodoTable.COLUMN_DESCRIPTION, "00:00:10");
			Uri todoUri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/" + id);
			getActivity().getContentResolver().update(todoUri, values, null, null);
			
        }
		 @Override public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
            	case ADD_PROJECT_ID:
            		Intent i = new Intent(getActivity(), TimeDetailActivity.class);
            		startActivityForResult(i, ACTIVITY_CREATE);
            		break;
            }	
            return false;
		 }
		 
		static final String[] projection = { TodoTable.COLUMN_ID, TodoTable.COLUMN_SUMMARY, TodoTable.COLUMN_DESCRIPTION };
		
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader cursorLoader = new CursorLoader(getActivity(), 
					MyTodoContentProvider.CONTENT_URI, projection, null, null, null);
			return cursorLoader;
		}
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mAdapter = new FoxListAdapter(getActivity().getApplicationContext(), data, true);
			setListAdapter(mAdapter);
			
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
		}
		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			//mAdapter.swapCursor(null);
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