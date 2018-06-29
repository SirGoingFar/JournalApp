package com.eemf.sirgoingfar.journalapp.web_browser;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;
import android.util.Log;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.activities.JournalPreviewActivity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebViewHandler extends CustomTabsCallback {

    private final String WHATSAPP_KEYWORD = "api.whatsapp.com";
    private final String YOUTUBE_KEYWORD = "youtu.be";
    private String validUrl = null;
    public static final String WEB_URL = "ARG_URL";

    private boolean isChromeSet = false;

    private CustomTabsIntent customTabsIntent;

    private String inputUrl;

    private JournalPreviewActivity previewActivity;

    public WebViewHandler(Context context, String url) {
        inputUrl = url;
        if (context instanceof JournalPreviewActivity)
            previewActivity = (JournalPreviewActivity) context;
    }

    public void processUrl() {

        //ensure validUrl is in the right format
        if (TextUtils.isEmpty(validUrl) && !validateURLFormat())
            return;

        //initialize Custom Builder and Intent
        initializeBuilderAndIntent();

        //ensure Chrome Custom Tab executes the Intent
        resolveActivity();

        //request Intent Executor Chooser if validUrl is not a Web Link
        if (!(validUrl.contains(WHATSAPP_KEYWORD)
                || validUrl.contains(YOUTUBE_KEYWORD))) {

            //Launch Custom Tab if Chrome is installed
            if (isChromeSet)
                customTabsIntent.launchUrl(previewActivity, Uri.parse(validUrl));
                /* Else use the default Web Browser */
            else {
                Bundle data = new Bundle();
                data.putString(WEB_URL, validUrl);

                DefaultWebBrowserFragment browserFragment = new DefaultWebBrowserFragment();
                browserFragment.setArguments(data);
                previewActivity.startFragment(browserFragment, true);
            }
        } else
            customTabsIntent.launchUrl(previewActivity, Uri.parse(validUrl));
    }

    private void initializeBuilderAndIntent() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(previewActivity.getResources().getColor(R.color.colorWhite));

        customTabsIntent = builder.build();
        customTabsIntent.intent.setData(Uri.parse(validUrl));
    }

    private boolean validateURLFormat() {

        if (TextUtils.isEmpty(inputUrl))
            return false;

        //ensure validUrl supplied is well formatted
        String WEB_AND_LOCAL_HTML_URL_REGEX_PATTERN = "^(https?|ftp|file):\\/\\/(www.)?[A-Za-z][A-Za-z0-9-+&@#\\/%?=~_|!:,.;]*.[A-Za-z0-9-+&@#\\/%?=~_|!:,.;]+(.[A-Za-z]+)?";

        Pattern pattern = Pattern.compile(WEB_AND_LOCAL_HTML_URL_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(inputUrl);

        if (matcher.find() && matcher.group().equals(inputUrl)) {
            validUrl = inputUrl;
            return true;
        } else
            return false;
    }

    private void resolveActivity() {
        PackageManager packageManager = previewActivity.getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(
                customTabsIntent.intent,
                PackageManager.MATCH_ALL
        );

        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;

            final String GOOGLE_CHROME_PACKAGE_KEYWORD = "chrome";
            final String GOOGLE_CHROME_STABLE_PACKAGE_NAME = "com.android.chrome";
            final String GOOGLE_CHROME_BETA_PACKAGE_NAME = "com.chrome.beta";
            final String GOOGLE_CHROME_DEV_PACKAGE_NAME = "com.chrome.dev";

            if (
                    (packageName.equalsIgnoreCase(GOOGLE_CHROME_STABLE_PACKAGE_NAME)
                            || packageName.equalsIgnoreCase(GOOGLE_CHROME_BETA_PACKAGE_NAME)
                            || packageName.equalsIgnoreCase(GOOGLE_CHROME_DEV_PACKAGE_NAME)
                            || packageName.contains(GOOGLE_CHROME_PACKAGE_KEYWORD))

                            &&

                            !(validUrl.contains(WHATSAPP_KEYWORD) || validUrl.contains(YOUTUBE_KEYWORD))
                    ) {

                customTabsIntent.intent.setPackage(packageName);
                isChromeSet = true;

                return;
            }

        }
    }

    @Override
    public void onNavigationEvent(int navigationEvent, Bundle extras) {
        switch (navigationEvent) {
            case CustomTabsCallback.NAVIGATION_STARTED:
//                GeneralAnalytics.track(GeneralAnalyticsConstants.USER_OPENED_WEB_VIEW);
                Log.d(WebViewHandler.class.getName(), "Navigation started");
                break;
            case CustomTabsCallback.NAVIGATION_FINISHED:
//                GeneralAnalytics.track(GeneralAnalyticsConstants.USER_OPENED_WEB_VIEW);
                Log.d(WebViewHandler.class.getName(), "Navigation finished");
                break;
            case CustomTabsCallback.NAVIGATION_FAILED:
//                GeneralAnalytics.track(GeneralAnalyticsConstants.USER_OPENED_WEB_VIEW);
                Log.d(WebViewHandler.class.getName(), "Navigation failed");
                break;
            case CustomTabsCallback.NAVIGATION_ABORTED:
//                GeneralAnalytics.track(GeneralAnalyticsConstants.USER_OPENED_WEB_VIEW);
                Log.d(WebViewHandler.class.getName(), "Navigation aborted");
                break;
            case CustomTabsCallback.TAB_SHOWN:
//                GeneralAnalytics.track(GeneralAnalyticsConstants.USER_OPENED_WEB_VIEW);
                Log.d(WebViewHandler.class.getName(), "User opened web view");
                break;
            case CustomTabsCallback.TAB_HIDDEN:
//                GeneralAnalytics.track(GeneralAnalyticsConstants.USER_CLOSED_WEB_VIEW);
                Log.d(WebViewHandler.class.getName(), "User closed web view");
                break;
            default:
                super.onNavigationEvent(navigationEvent, extras);
        }
    }
}
