package com.eemf.sirgoingfar.journalapp.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkUtils {

    public static boolean isOnline(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (context == null
                    || (context instanceof Activity
                    && ((Activity) context).isFinishing()
                    && ((Activity) context).isDestroyed())) {
                return false;
            }
        } else {
            if (context == null
                    || (context instanceof Activity
                    && ((Activity) context).isFinishing())) {
                return false;
            }
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (null != activeNetwork && activeNetwork.isConnected());
    }
}