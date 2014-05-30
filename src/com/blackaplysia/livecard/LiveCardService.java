package com.blackaplysia.livecard;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import android.widget.RemoteViews;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LiveCardService extends Service {

    static final String SERVICE_TAG = "LiveCardService";
    static final String CARD_TAG = "LiveCard";

    private final IBinder _binder = new LiveCardServiceBinder();
    private LiveCard _card;
    private RemoteViews _views;

    static public String getServiceTag() {
	return SERVICE_TAG;
    }

    public class LiveCardServiceBinder extends Binder {
	public LiveCardService getService() {
	    return LiveCardService.this;
	}
    }

    @Override
    public IBinder onBind(Intent intent) {
	return _binder;
    }

    @Override
    public void onCreate() {
	super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

	Log.i(getServiceTag(), "LiveCard Service started");

	if (_card == null) {
	    _card = new LiveCard(this, CARD_TAG);
	    _views = new RemoteViews(getPackageName(), R.layout.main);
	    _card.setViews(_views);

	    Intent menuIntent = new Intent(this, LiveCardMenuActivity.class);
	    menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    _card.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
	    _card.attach(this);
	    _card.publish(PublishMode.REVEAL);
	    update();
	} else {
	    _card.navigate();
	}

	return START_STICKY;
    }

    @Override
    public void onDestroy() {
	if (_card != null && _card.isPublished()) {
	    _card.unpublish();
	    _card = null;
	}
	super.onDestroy();
    }

    private static class LiveCardServiceContents {
	private static int _numberOfContents = 0;
	private static String get() {
	    _numberOfContents++;
	    return "Contents #" + Integer.toString(_numberOfContents);
	}
    }

    public void update() {
	_views.setTextViewText(R.id.contents, LiveCardServiceContents.get());
	_card.setViews(_views);
    }

}
