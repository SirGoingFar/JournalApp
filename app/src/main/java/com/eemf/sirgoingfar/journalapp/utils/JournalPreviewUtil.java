package com.eemf.sirgoingfar.journalapp.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.eemf.sirgoingfar.journalapp.service.JournalPreviewFirebaseJobService;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class JournalPreviewUtil {

    private static final int REMINDER_INTERVAL_MINUTES = 240;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = (int) TimeUnit.MINUTES.toSeconds(15);

    private static final String REMINDER_JOB_TAG = "journal_preview_reminder_tag";

    private static boolean sInitialized;

    synchronized public static void scheduleJournalPreviewReminder(@NonNull final Context context) {

        if (sInitialized)
            return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job constraintReminderJob = dispatcher.newJobBuilder()
                .setService(JournalPreviewFirebaseJobService.class)
                .setTag(REMINDER_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(constraintReminderJob);

        //flag that the job has been initialized
        sInitialized = true;
    }
}
