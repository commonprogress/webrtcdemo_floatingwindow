package com.pine.rtcdemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dongxl.fw.FloatWindowActivity;

import java.util.Arrays;

public class LoadingActivity extends Activity {
    private final static String TAG = LoadingActivity.class.getSimpleName();
    private final static int PERMISSIONS_REQUEST_CODE = 1;
    private final static int FLOATINGWINDOW_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        requestPermission();
        checkFloatPermission();
    }

    private void checkFloatPermission() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                FloatingWindowUtils.applyFloatPermission(LoadingActivity.this, FLOATINGWINDOW_REQUEST_CODE);
//            }
//        }, 1500);

        startActivity(new Intent(this, FloatWindowActivity.class));
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                        PERMISSIONS_REQUEST_CODE);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult requestCode:" + requestCode + ",resultCode:" + resultCode);
        switch (requestCode) {
            case FLOATINGWINDOW_REQUEST_CODE: {

                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult requestCode:" + requestCode + ",permissions[]:" + Arrays.toString(permissions) + ",grantResults[]:" + Arrays.toString(grantResults));
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    startActivity(new Intent(this, ConnectActivity.class));
                    finish();
                } else {
                    showWaringDialog();
                }
                return;
            }
        }
    }

    private void showWaringDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("警告！")
                .setMessage("请前往设置->应用->AppRTC->权限中打开相关权限，否则功能无法正常运行！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                        finish();
                    }
                }).show();
    }
}
