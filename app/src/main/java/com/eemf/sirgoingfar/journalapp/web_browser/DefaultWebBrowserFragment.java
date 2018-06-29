package com.eemf.sirgoingfar.journalapp.web_browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.activities.JournalPreviewActivity;

public class DefaultWebBrowserFragment extends Fragment{

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView closeButton;
    private TextView urlContainer;

    private String url = null;
    private String currentUrl = null;
    private JournalPreviewActivity previewActivity;

    public static DefaultWebBrowserFragment newInstance() {
        Bundle args = new Bundle();
        DefaultWebBrowserFragment fragment = new DefaultWebBrowserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof JournalPreviewActivity)
            previewActivity = (JournalPreviewActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_web_browser, container, false);

        if (getArguments() != null && getArguments().containsKey(WebViewHandler.WEB_URL))
            url = getArguments().getString(WebViewHandler.WEB_URL);
        else
            previewActivity.closeFragment();

        urlContainer = view.findViewById(R.id.tv_url_text);
        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                urlContainer.setText(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        progressBar = view.findViewById(R.id.pb_loading_browser);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        closeButton = view.findViewById(R.id.ic_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewActivity.closeFragment();
            }
        });


        swipeRefreshLayout = view.findViewById(R.id.webview_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentUrl = urlContainer.getText().toString();
                loadLinkUrl(currentUrl);

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

        loadLinkUrl(url);

        return view;
    }

    private void loadLinkUrl(String url) {
        if (url != null && !TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    progressBar.setProgress(newProgress);
                }
            });
        }
    }

    public void onBackPress() {
        if (webView.canGoBack())
            webView.goBack();
        else {
            previewActivity.closeFragment();
        }
    }
}
