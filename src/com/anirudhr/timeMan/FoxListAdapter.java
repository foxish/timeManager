package com.anirudhr.timeMan;

import com.anirudhr.timeMan.R;
import com.anirudhr.timeMan.db.TodoTable;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FoxListAdapter extends CursorAdapter  {
	public FoxListAdapter(Context con, Cursor c, boolean autoRequery) {
		super(con, c, autoRequery);
	}
	@Override
	public void bindView(View vi, Context arg1, Cursor cursor) {
		TextView state = (TextView)vi.findViewById(R.id.title); // heading
		TextView numbering = (TextView)vi.findViewById(R.id.list_image); // left number
		TextView timeElapsed =  (TextView)vi.findViewById(R.id.todayTime); //time
		
		state.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_SUMMARY)));
		numbering.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_ID)));
		timeElapsed.setText(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_DESCRIPTION)));
	}
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup arg2) {
		LayoutInflater inflater = LayoutInflater.from(context);
        View vi = inflater.inflate(R.layout.task_list_item, null);
        bindView(vi, context, cursor);
        return vi;
	}

   
}
