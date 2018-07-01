package com.eemf.sirgoingfar.journalapp.service;

import android.content.Context;
import android.os.AsyncTask;

import com.eemf.sirgoingfar.journalapp.utils.NotificationUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class JournalPreviewFirebaseJobService extends JobService {

    private AsyncTask mUserNotificationTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        mUserNotificationTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                Context context = JournalPreviewFirebaseJobService.this;
                NotificationUtils.remindUserToPreviewJournal(context);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(jobParameters, false);
            }
        };

        mUserNotificationTask.execute();
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mUserNotificationTask != null)
            mUserNotificationTask.cancel(true);
        return true;
    }
}