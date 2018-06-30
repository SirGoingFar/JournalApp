package com.eemf.sirgoingfar.journalapp.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eemf.sirgoingfar.journalapp.activities.CatalogActivity;
import com.eemf.sirgoingfar.journalapp.activities.SplashActivity;

public class RetrieveUserDataTask {

    public static void fetchUserData(Context context, String userId, String serviceAction){
        //Todo: Fetch the data here
        //Todo: Start the activity after saving to the DB
        //On Success of Db update, start the serviceAction - lines below must be blocking me
//        (onSuccess) {
        Log.d("Akintunde:", "Retrieval done");
        if (serviceAction.equalsIgnoreCase(SplashActivity.SERVICE_ACTION))
            Log.d("Akintunde:", "User logged in starting the catalog Activity");
            context.startActivity(new Intent(context, CatalogActivity.class));
//        }
    }
}
