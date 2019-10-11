package com.kivicube.webar.sample;

import android.content.pm.PackageManager;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kivicube.webar.WebARFragment;
import com.kivicube.webar.WebARWebView;

public class WebARSampleActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        WebARFragment mWebarFragment = (WebARFragment) getSupportFragmentManager().findFragmentById(R.id.webar);
        WebARWebView webView = mWebarFragment.mWebView;

        // 可以直接使用默认配置。
        webView.config(mWebarFragment);

        // 也可以在SDK已有的配置上，自行继承更改配置，或增加其他的功能开发。
        /*
        webView.config(new com.kivicube.webar.WebChromeClient(mWebarFragment) {
            {
                addAllowOpenCameraDomainWhiteList(new String[] {
                        "webrtc.github.io",
                        "your domain"
                });
            }
        });
        */

        // 也可以直接使用WebView的API进行配置。比如增加UA信息。
        WebSettings webSettings = webView.getSettings();
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua + " your app name/" + getVersionName());

        // 危险操作，正式应用必须删除。使开发环境支持自定义证书
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        // 打开Kivicube的场景URL地址
        webView.loadUrl(getIntent().getStringExtra("url"));
    }

    /**
     * 获取版本号名称
     */
    protected String getVersionName() {
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}