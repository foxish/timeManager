package com.anirudhr.timeMan;

import android.app.Activity;
import android.content.SharedPreferences;

public class timeManSettings {
	private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    public timeManSettings(Activity activityArg){
    	settings = activityArg.getSharedPreferences(PREFS_NAME, 0);
    	editor = settings.edit();
    }
    
    
    
    private static final String PREFS_NAME = "FoxTimer";
}
