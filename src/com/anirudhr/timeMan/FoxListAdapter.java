package com.anirudhr.timeMan;

import com.anirudhr.timeMan.R;
import com.anirudhr.timeMan.db.TodoTable;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FoxListAdapter extends CursorAdapter  {
	private Activity a;
	public FoxListAdapter(Context con, Activity a, Cursor c, boolean autoRequery) {
		super(con, c, autoRequery);
		this.a = a;
	}
	@Override
	public void bindView(View vi, Context arg1, Cursor cursor) {
		TimeStructures ts = new TimeStructures(a);
		
		TextView task = (TextView)vi.findViewById(R.id.title); // heading
		TextView priority = (TextView)vi.findViewById(R.id.list_image); // left number
		TextView timeElapsed =  (TextView)vi.findViewById(R.id.todayTime); //time
		
		task.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_ACTIVITY)));
		priority.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_PRIORITY)));
		
		long id = cursor.getLong(cursor.getColumnIndex(TodoTable.COLUMN_ID));
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
        bindView(vi, context, cursor);
        return vi;
	}

   
}
