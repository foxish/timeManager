package com.anirudhr.timeMan;

import com.anirudhr.timeMan.R;
import com.anirudhr.timeMan.db.TodoTable;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimeListAdapter extends CursorAdapter  {
	private TaskUtilites ts;
	public TimeListAdapter(Context con, Activity a, Cursor c, boolean autoRequery) {
		super(con, c, autoRequery);
        ts = new TaskUtilites(a);
	}
	@Override
	public void bindView(View vi, Context arg1, Cursor cursor) {
		TextView task = (TextView)vi.findViewById(R.id.title); // heading
		TextView priority = (TextView)vi.findViewById(R.id.list_image); // left number
		TextView timeElapsed =  (TextView)vi.findViewById(R.id.todayTime); //time
		task.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_ACTIVITY)));
		priority.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_PRIORITY)));
		
		long isChecked = cursor.getLong(cursor.getColumnIndex(TodoTable.COLUMN_PRODUCTIVE));
		if(isChecked == 0)
			task.setTextColor(Color.GRAY);
		else
			task.setTextColor(Color.BLACK);
		
		long id = cursor.getLong(cursor.getColumnIndex(TodoTable.COLUMN_ID));	
		timeElapsed.setText(R.string.time_default);
		if(ts.getRunning() == id){
			ts.startTimer(vi, ts.getCurrentTaskStart(), id);
		}else{
			long time = cursor.getLong((cursor.getColumnIndex(TodoTable.COLUMN_TIME)));
			timeElapsed.setText(ts.getTimeString(0, 0, time));
		}
	}
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup arg2) {
		LayoutInflater inflater = LayoutInflater.from(context);
        View vi = inflater.inflate(R.layout.task_list_item, null);
        return vi;
	}
	@Override
	public int getItemViewType(int position) {
	    mCursor.moveToPosition(position);
	    if (mCursor.getLong(mCursor.getColumnIndex(TodoTable.COLUMN_ID)) == ts.getRunning()){
	        return IGNORE_ITEM_VIEW_TYPE;
	    }
	    return super.getItemViewType(position);
	}
    /*@Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return 500;
    }*/
}
