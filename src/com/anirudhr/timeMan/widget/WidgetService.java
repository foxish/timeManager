package com.anirudhr.timeMan.widget;

import com.anirudhr.timeMan.GlobalAccess;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WidgetService extends Service {
	public static final String UPDATEWIDGET = "updateWidget";
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
        updateWidget(intent);
		stopSelf(startId);
		return START_STICKY;
	}
	
	private void updateWidget(Intent intent) {
        if (intent != null){
    		String requestedAction = intent.getAction();
    		if (requestedAction != null && requestedAction.equals(UPDATEWIDGET)){
    			GlobalAccess.updateWidget(this.getApplicationContext());
    		}
        }
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
