package com.gamma.gamenews.Data.Network;

import android.annotation.TargetApi;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by emers on 7/6/2018.
 */

public class AppJobService extends JobService {
    private static final String TAG = AppJobService.class.getSimpleName();


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob: Job service started");
        // TODO: completar este metodo cuando se requiera
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
