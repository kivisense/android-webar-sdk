package com.kivicube.webar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.Arrays;

public class WebChromeClient extends android.webkit.WebChromeClient {
    private WebARFragment mWebARFragment;
    private ArrayList<String> domainWhiteList = new ArrayList<>();

    public WebChromeClient(WebARFragment webARFragment) {
        super();
        mWebARFragment = webARFragment;
        domainWhiteList.add("kivicube.develop");
        domainWhiteList.add("kivicube.cc");
        domainWhiteList.add("kivicube.com");
        domainWhiteList.add("kivisense.com");
    }

    public void addAllowOpenCameraDomainWhiteList(String[] list) {
        domainWhiteList.addAll(Arrays.asList(list));
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        handleCameraPermission(request);
    }

    protected void handleCameraPermission(final PermissionRequest request) {
        String host = request.getOrigin().getHost();
        for (String domain : domainWhiteList) {
            if (host != null && host.endsWith(domain)) {
                for (String res : request.getResources()) {
                    if (res.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                        mWebARFragment.checkCameraPermission(new WebARFragment.CameraPermissionListener() {
                            @Override
                            public void success() {
                                request.grant(request.getResources());
                            }

                            @Override
                            public void fail(String errMsg) {
                                request.deny();
                            }
                        });
                        return;
                    }
                }
            }
        }
        request.deny();
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mWebARFragment.getContext());
        alertDialog.setTitle("");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.create().show();
        return true;
    }
}
