package com.kivicube.webar;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebARWebView extends WebView {
    private static final String TAG = "WebARWebView";
    private static final String UA = "kivicube webar/1.0";

    public WebARWebView(Context context) {
        this(context, null);
    }

    public WebARWebView(Context context, AttributeSet attrs) {
        this(context, attrs, Resources.getSystem().getIdentifier("webViewStyle","attr","android"));
    }

    public WebARWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void config(WebARFragment webARFragment) {
        config(new WebChromeClient(webARFragment));
    }

    public void config(WebChromeClient webChromeClient) {
        WebSettings webSettings = getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua + " " + UA);
        setWebViewClient(new WebViewClient());
        setWebChromeClient(webChromeClient);
    }
}
