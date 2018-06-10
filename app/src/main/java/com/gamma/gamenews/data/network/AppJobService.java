package com.gamma.gamenews.data.network;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class AppJobService extends JobService {
    private static final String TAG = "GN:AppJobService";

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
