package com.anirudhr.timeMan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
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

public class TimeListFragment extends SherlockFragmentActivity
{
	static String[] dummyData;
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int ADD_PROJECT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	
	private static final String BASE_URL = "";
	private static final String SALT = "";
	private static final String PEPPER = "";

	public static class CurrentListFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor>	{
        private TimeListAdapter mAdapter = null;
        private TaskUtilites ts;
        private XmlUtilites utilities;
        private Handler resetHandler = new Handler();
        private Runnable resetTask;
        
        @Override 
        public void onCreate(Bundle savedInstanceState){
        	super.onCreate(savedInstanceState);
	        GlobalAccess.sha = getSherlockActivity();
        }
        @Override public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        setEmptyText("No data");
            registerForContextMenu(getListView());
            setListShown(false);
            getLoaderManager().initLoader(0, null, this);
	        setHasOptionsMenu(true);
	        
	        //create instance of the timeStructures
	        ts = new TaskUtilites(getActivity());
	        utilities = new XmlUtilites(getActivity());
        }	
		@Override
		public void onResume(){
			super.onResume();
			trackExpiration();
			
			if(resetHandler!=null)
				resetHandler.removeCallbacks(resetTask);
			resetTask = new Runnable() {
				public void run() {			
						trackExpiration();
					}
				};
	        resetHandler.postDelayed(resetTask, ts.getExpirationTime() - System.currentTimeMillis());
		}
		
		public void trackExpiration(){
			if(ts.timeToReset()){
				utilities.writeToXML();
				try{
					GlobalAccess.changeData();
				}catch(NullPointerException e){
					//changeData will be called by StatisticsFragment.allowStaticAccess() at a later time, dont worry
				}
				resetDatabase();
			}
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
			final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			switch (item.getItemId()) {
			case DELETE_ID:
				AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
	        	builder.setMessage("Are you sure you want to delete this item?")
	        	       .setCancelable(false)
	        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	                try{
	        	                	Uri uri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/" + info.id);
	        	      			  	getActivity().getContentResolver().delete(uri, null, null);
	        	                }catch(Exception e){
	        	                	e.printStackTrace();
	        	                }
	        	           }
	        	       })
	        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	                dialog.cancel();
	        	           }
	        	       });
	        	AlertDialog alert = builder.create();
	        	alert.show();
			  return true;
			case EDIT_ID:
			Intent i = new Intent(getActivity(), TaskDetailActivity.class);
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
			super.onCreateOptionsMenu(menu, inflater);
	    }
		
		@Override public void onListItemClick(ListView l, View v, int position, long id) {
			if(ts.getRunning() == 0){
				//no task running at all
				final long start = System.currentTimeMillis();
				ts.setCurrentTaskStart(start);
				ts.setRunning(id);
				ts.startTimer(v, start, id);
				sendWebRequest(id);//sends to server
			}
			else
			{
				//stop all running tasks
				ts.killTimer(v);
				updateDatabase(ts.getRunning());
				ts.setRunning(0);
				sendWebRequest(-1);//stops tasks on server
			}
        }
		private String buildUrl(long id){
			//"http://anirudhr.com/status/put.php?message=test1&productive=0&efficiency=82&auth=648153773ccf5c0dad49c5392ce97cb5";
			
			StringBuilder bdr = new StringBuilder(BASE_URL);
			bdr.append("?message=");
			bdr.append(id != -1 ? ts.getCurrentTaskName() : "-" );
			bdr.append("&productive=");
			bdr.append(id != -1 ? ts.getProductive(id): "0");
			bdr.append("&efficiency=");
			bdr.append((int)utilities.getUtil());
			bdr.append("&auth=");
			bdr.append(getMd5());
			return bdr.toString();
		}
		//credit: http://www.sergiy.ca/how-to-make-java-md5-and-sha-1-hashes-compatible-with-php-or-mysql/
		private String getMd5(){
			//will use salt and pepper 
			String toHash = SALT + PEPPER;
			//some operation, removed for security reasons
			
			MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			md5.update(toHash.getBytes());
			BigInteger hash = new BigInteger(1, md5.digest());
	        String result = hash.toString(16);
	        while(result.length() < 32) {
	            result = "0" + result;
	        }
	        
			return result;
		}

		
		private boolean sendWebRequest(final long id){
			new Thread(new Runnable() {
		        public void run() {
		        	HttpClient httpClient = new DefaultHttpClient();  
					String url = buildUrl(id);
					HttpGet httpGet = new HttpGet(url);
					try {
					    HttpResponse response = httpClient.execute(httpGet);
					    StatusLine statusLine = response.getStatusLine();
					    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					        HttpEntity entity = response.getEntity();
					        ByteArrayOutputStream out = new ByteArrayOutputStream();
					        entity.writeTo(out);
					        out.close();
					        //String responseStr = out.toString();
					    } else {
					        // handle bad response
					    }
					} catch (ClientProtocolException e) {
					    // handle exception
					} catch (IOException e) {
					    // handle exception
					}
		        }
		    }).start();
			return false;
		}
		
		private void updateDatabase(long id){
			try{
				long time = System.currentTimeMillis() - ts.getCurrentTaskStart() + ts.getOffsetFromDatabase(id);
				ContentValues values = new ContentValues();
				values.put(TodoTable.COLUMN_ID, id);
				values.put(TodoTable.COLUMN_TIME, time);
				Uri todoUri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/" + id);
				getActivity().getContentResolver().update(todoUri, values, null, null);
			}catch(Exception e){
				//could happen if you deleted the running task. Ignore and move on
				e.printStackTrace();
			}
		}
		
		private void resetDatabase(){
			//updates and fills ALL entries with set value ##CAREFUL
			ContentValues values = new ContentValues();
			values.put(TodoTable.COLUMN_TIME, 0);
			Uri todoUri = Uri.parse(String.valueOf(MyTodoContentProvider.CONTENT_URI));
			GlobalAccess.a.getContentResolver().update(todoUri, values, null, null);
		}
		
		 @Override public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
            	case ADD_PROJECT_ID:
            		Intent i = new Intent(getActivity(), TaskDetailActivity.class);
            		startActivityForResult(i, ACTIVITY_CREATE);
            		break;
            }	
            return false;
		 }
		static final String[] projection = { TodoTable.COLUMN_ID, TodoTable.COLUMN_ACTIVITY, TodoTable.COLUMN_TIME, TodoTable.COLUMN_PRIORITY, TodoTable.COLUMN_PRODUCTIVE };
		
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String sortOrder = TodoTable.COLUMN_PRIORITY;
			CursorLoader cursorLoader = new CursorLoader(getActivity(), 
					MyTodoContentProvider.CONTENT_URI, projection, null, null, sortOrder);
			return cursorLoader;
		}
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if(mAdapter == null){
				mAdapter = new TimeListAdapter(getActivity().getApplicationContext(), getActivity(), data, true);
				setListAdapter(mAdapter);
			}else{
				mAdapter.changeCursor(data);
			}
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