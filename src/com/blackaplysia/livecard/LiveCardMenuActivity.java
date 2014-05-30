package com.blackaplysia.livecard;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;

import java.lang.Runnable;

public class LiveCardMenuActivity extends Activity
{
    private LiveCardService _service;

    private boolean _isAttachedToWindow = false;
    private boolean _isOptionsMenuOpen = false;

    private ServiceConnection _connection = new ServiceConnection() {
	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
	    if (binder instanceof LiveCardService.LiveCardServiceBinder) {
		_service = ((LiveCardService.LiveCardServiceBinder)binder).getService();
		openOptionsMenu();
	    }
	    unbindService(this);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
	}
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	bindService(new Intent(this, LiveCardService.class), _connection, BIND_AUTO_CREATE);
    }

    @Override
    public void onAttachedToWindow() {
	super.onAttachedToWindow();
	_isAttachedToWindow = true;
	openOptionsMenu();
    }

    @Override
    public void onDetachedFromWindow() {
	super.onDetachedFromWindow();
	_isAttachedToWindow = false;
    }

    @Override
    public void openOptionsMenu() {
	Log.i(LiveCardService.getServiceTag(), "LiveCardActivity.openOptionsMenu()");
	if (!_isOptionsMenuOpen && _isAttachedToWindow) {
	    _isOptionsMenuOpen = true;
	    super.openOptionsMenu();
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	super.onCreateOptionsMenu(menu);
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.main, menu);
	return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.update:
	    _service.update();
	    return true;
	case R.id.exit:
	    stopService(new Intent(this, LiveCardService.class));
	    finish();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
	_isOptionsMenuOpen = false;
	finish();
    }

}
