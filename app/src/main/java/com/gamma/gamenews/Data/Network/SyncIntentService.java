package com.gamma.gamenews.Data.Network;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

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
        //TODO: completar este método
    }
}
