package com.eemf.sirgoingfar.journalapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.eemf.sirgoingfar.journalapp.activities.SplashActivity;
import com.eemf.sirgoingfar.journalapp.task.RetrieveUserDataTask;


public class RetrieveUserDataService extends IntentService {

    public RetrieveUserDataService() {
        super("RetrieveUserDataService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String serviceAction = intent.getAction();
        String userId = null;

        if(intent.hasExtra(SplashActivity.USER_ID))
            userId = intent.getStringExtra(SplashActivity.USER_ID);

        RetrieveUserDataTask.fetchUserData(this.getApplicationContext(), userId, serviceAction);
    }
}
