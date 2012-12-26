package com.anirudhr.timeMan;

import com.anirudhr.timeMan.R;
import com.anirudhr.timeMan.db.MyTodoContentProvider;
import com.anirudhr.timeMan.db.TodoTable;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TimeDetailActivity extends Activity {
	  private EditText mTitleText;
	  private EditText mBodyText;
	  private Uri todoUri;

	  @Override
	  protected void onCreate(Bundle bundle) {
	    super.onCreate(bundle);
	    setContentView(R.layout.todo_edit);

	    mTitleText = (EditText) findViewById(R.id.todo_edit_summary);
	    mBodyText = (EditText) findViewById(R.id.todo_edit_description);
	    Button confirmButton = (Button) findViewById(R.id.todo_edit_button);

	    Bundle extras = getIntent().getExtras();

	    // Check from the saved Instance
	    todoUri = (bundle == null) ? null : (Uri) bundle
	        .getParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE);

	    // Or passed from the other activity
	    if (extras != null) {
	      todoUri = extras
	          .getParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE);

	      fillData(todoUri);
	    }

	    confirmButton.setOnClickListener(new View.OnClickListener() {
	      public void onClick(View view) {
	        if (TextUtils.isEmpty(mTitleText.getText().toString())||TextUtils.isEmpty(mBodyText.getText().toString())) {
	          makeToast();
	        } else {
	          setResult(RESULT_OK);
	          finish();
	        }
	      }

	    });
	  }

	  private void fillData(Uri uri) {
	    String[] projection = { TodoTable.COLUMN_ACTIVITY, TodoTable.COLUMN_PRIORITY };
	    Cursor cursor = getContentResolver().query(uri, projection, null, null,
	        null);
	    if (cursor != null) {
	      cursor.moveToFirst();
	      mTitleText.setText(cursor.getString(cursor
	          .getColumnIndexOrThrow(TodoTable.COLUMN_ACTIVITY)));
	      mBodyText.setText(cursor.getString(cursor
	          .getColumnIndexOrThrow(TodoTable.COLUMN_PRIORITY)));
	      //Always close the cursor
	      cursor.close();
	    }
	  }
	  protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    saveState();
	    outState.putParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE, todoUri);
	  }

	  @Override
	  protected void onPause() {
	    super.onPause();
	    saveState();
	  }

	  private void saveState() {
	    String summary = mTitleText.getText().toString();
	    String description = mBodyText.getText().toString();

	    // Only save if both summary or description are available
	    if (description.length() == 0 || summary.length() == 0) {
	      return;
	    }

	    ContentValues values = new ContentValues();
	    values.put(TodoTable.COLUMN_ACTIVITY, summary);
	    values.put(TodoTable.COLUMN_TIME, "0");
	    values.put(TodoTable.COLUMN_PRIORITY, description);
	    
	    if (todoUri == null) {
	      // New todo
	      todoUri = getContentResolver().insert(MyTodoContentProvider.CONTENT_URI, values);
	    } else {
	      // Update todo
	      getContentResolver().update(todoUri, values, null, null);
	    }
	  }

	  private void makeToast() {
	    Toast.makeText(TimeDetailActivity.this, "The activity & priority must be set",
	        Toast.LENGTH_LONG).show();
	  }
	} 