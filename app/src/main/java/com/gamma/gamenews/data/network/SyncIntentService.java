package com.gamma.gamenews.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gamma.gamenews.utils.DependencyContainer;

/**
 * Created by emers on 8/6/2018.
 */

public class SyncIntentService extends IntentService{
    private static final String TAG = SyncIntentService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *  name : Used to name the worker thread, important only for debugging.
     */
    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: Intent service started");
        NetworkDataSource networkDataSource =
                DependencyContainer.getNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchNews();
    }
}
