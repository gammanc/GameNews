package com.gamma.gamenews.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gamma.gamenews.utils.DependencyContainer;

/**
 * Performs data sync on a diferent thread
 * so if the app is closed, the task will still complete
 */

public class SyncIntentService extends IntentService{
    private static final String TAG = "GN:SyncIntentService";

    /**
     * Creates an IntentService for fetch the data
     */
    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: Intent service started");
        if (intent != null){
            String[] favs = intent.getStringArrayExtra("favorites");
            NetworkDataSource networkDataSource =
                    DependencyContainer.getNetworkDataSource(this.getApplicationContext());
            networkDataSource.fetchNews(favs);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Intent service finished");
    }
}
