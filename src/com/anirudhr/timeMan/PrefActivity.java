package com.anirudhr.timeMan;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class PrefActivity extends PreferenceActivity{
	@Override
    protected void onCreate(final Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	if (Build.VERSION.SDK_INT >= 11)
            AddResourceApi11AndGreater();
    	else
            AddResourceApiLessThan11();
    }
    
    @SuppressWarnings("deprecation")
    protected void AddResourceApiLessThan11()
    {
        addPreferencesFromResource(R.xml.settings);
    }

    @SuppressLint("NewApi")
    protected void AddResourceApi11AndGreater()
    {
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    @SuppressLint("NewApi")
	public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        TaskUtilites tu = new TaskUtilites(this);
        tu.setExpirationTime();
        Toast.makeText(getApplicationContext(), "Restart Application for changes to take effect", Toast.LENGTH_SHORT).show();
    }
}
