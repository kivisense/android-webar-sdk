package com.kivicube.webar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WebARFragment extends Fragment {
    private static final String TAG = "WebARFragment";
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    public WebARWebView mWebView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_ar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mWebView = view.findViewById(R.id.web_ar_web_view);
    }

    protected CameraPermissionListener mListener;

    public void checkCameraPermission(CameraPermissionListener listener) {
        mListener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getTargetVersion() < Build.VERSION_CODES.M) {
                if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PermissionChecker.PERMISSION_GRANTED) {
                    listener.success();
                } else {
                    // 因为小于6.0，没有任何办法的错误提示。除非重装APP
                    new ErrorTipDialogFragment().setListener(listener).show(getChildFragmentManager(), "ErrorTipDialog");
                }
            } else {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    listener.success();
                } else {
                    requestCameraPermission();
                }
            }
        } else {
            // 安卓小于5.0，提示不支持。
            new NotSupportDialogFragment().setListener(listener).show(getChildFragmentManager(), "NotSupportDialog");
        }
    }

    private int getTargetVersion() {
        try {
            final PackageInfo info = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0);
            return info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return Build.VERSION.SDK_INT;
        }
    }

    private void requestCameraPermission() {
        // @TODO 小米6上测试，未勾选"不再询问"，第二次请求后，一样会返回true。
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new AppSettingsDialogFragment().show(getChildFragmentManager(), "AppSettingsDialog");
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mListener.success();
            } else {
                new AgainRequestPermissionDialogFragment().show(getChildFragmentManager(), "AgainRequestPermissionDialog");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppSettingsDialogFragment.REQUEST_CODE) {
            checkCameraPermission(mListener);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public interface CameraPermissionListener {
        void success();
        void fail(String errMsg);
    }

    /**
     * 安卓5.0以下，提示用户不支持。
     */
    public static class NotSupportDialogFragment extends DialogFragment {
        CameraPermissionListener mListener;

        public NotSupportDialogFragment setListener(CameraPermissionListener listener) {
            mListener = listener;
            return this;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.not_support)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListener.fail(String.valueOf(getContext().getText(R.string.not_support)));
                        }
                    })
                    .setCancelable(false)
                    .create();
        }
    }

    /**
     * 安卓5.0及以上，6.0以下，无相机权限时，提示用户。
     */
    public static class ErrorTipDialogFragment extends DialogFragment {
        CameraPermissionListener mListener;

        public ErrorTipDialogFragment setListener(CameraPermissionListener listener) {
            mListener = listener;
            return this;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.can_not_open_camera)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListener.fail(String.valueOf(getContext().getText(R.string.can_not_open_camera)));
                        }
                    })
                    .setCancelable(false)
                    .create();
        }
    }

    /**
     * 权限被用户拒绝后，提示用户可重新唤起权限申请的弹窗
     */
    public static class AgainRequestPermissionDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_permission)
                    .setNegativeButton(R.string.quit_current_activity, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    })
                    .setPositiveButton(R.string.go_grant, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WebARFragment parent = (WebARFragment) getParentFragment();
                            parent.requestCameraPermission();
                        }
                    })
                    .setCancelable(false)
                    .create();
        }
    }

    /**
     * 权限被禁用，不能再申请时，提示用户，去设置界面打开权限
     */
    public static class AppSettingsDialogFragment extends DialogFragment {
        public static final int REQUEST_CODE = 1314;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.go_to_setting)
                    .setNegativeButton(R.string.quit_current_activity, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    })
                    .setPositiveButton(R.string.go_setting, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Activity activity = getActivity();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            getParentFragment().startActivityForResult(intent, REQUEST_CODE);
                        }
                    })
                    .setCancelable(false)
                    .create();
        }
    }
}
