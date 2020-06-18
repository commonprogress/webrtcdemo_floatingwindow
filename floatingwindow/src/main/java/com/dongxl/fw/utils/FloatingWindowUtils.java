package com.dongxl.fw.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 系统权限管理中心服务
 */
public class FloatingWindowUtils {
    private final static String TAG = FloatingWindowUtils.class.getSimpleName();
    /**
     * 系统悬浮窗权限
     */
    public static final int OP_SYSTEM_ALERT_WINDOW = 24;

    public static boolean checkSystemAlterWindow(Context context) {
        return checkAlertWindowsPermission(context, OP_SYSTEM_ALERT_WINDOW);
    }

    public static boolean checkNofication(Context context) {
        return checkAlertWindowsPermission(context, 25);
    }

    /**
     * 判断 悬浮窗口权限是否打开
     *
     * @param context
     * @return true 允许  false禁止
     */
    public static boolean checkAlertWindowsPermission(Context context, int op) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return true;
        }
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = op;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1));
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.e(TAG, "checkAlertWindowsPermission NoSuchMethodException:" + e.getLocalizedMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "checkAlertWindowsPermission IllegalAccessException:" + e.getLocalizedMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(TAG, "checkAlertWindowsPermission IllegalArgumentException:" + e.getLocalizedMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.e(TAG, "checkAlertWindowsPermission InvocationTargetException:" + e.getLocalizedMessage());
        }
        return false;
    }

    /**
     * 判断悬浮窗权限是否开启
     *
     * @param context
     * @return
     */
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, OP_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                Log.e(TAG, "checkFloatPermission NoSuchMethodException:" + e.getLocalizedMessage());
                return false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Log.e(TAG, "checkFloatPermission IllegalAccessException:" + e.getLocalizedMessage());
                return false;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Log.e(TAG, "checkFloatPermission IllegalArgumentException:" + e.getLocalizedMessage());
                return false;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                Log.e(TAG, "checkFloatPermission InvocationTargetException:" + e.getLocalizedMessage());
                return false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "checkFloatPermission ClassNotFoundException:" + e.getLocalizedMessage());
                return false;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                Log.e(TAG, "checkFloatPermission NoSuchFieldException:" + e.getLocalizedMessage());
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }

    /**
     * AppOpsManager.MODE_ALLOWED —— 表示授予了权限并且重新打开了应用程序
     * AppOpsManager.MODE_IGNORED —— 表示授予权限并返回应用程序
     * AppOpsManager.MODE_ERRORED —— 表示当前应用没有此权限
     * AppOpsManager.MODE_DEFAULT —— 表示默认值，有些手机在没有开启权限时，mode的值就是这个
     *
     * @param activity
     * @param requestCode
     */
    public static void applyFloatPermission(Activity activity, int requestCode) {
        try {
            if (checkFloatPermission(activity)) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0以上
                //            第一种：会进入到悬浮窗权限应用列表
                //            使用以下代码，会进入到悬浮窗权限的列表，列表中是手机中需要悬浮窗权限的应用列表，你需要在此列表中找到自己的应用，然后点进去，才可以打开悬浮窗权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);

                //添加这行跳转指定应用，不添加跳转到应用列表，需要用户自己选择。网上有说加上这行华为手机有问题
                intent.setData(Uri.parse("package:" + activity.getPackageName()));

                activity.startActivityForResult(intent, requestCode);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0-8.0
                //            第二种：直接进入到自己应用的悬浮窗权限开启界面
                //            使用以下代码，则不会到上述所说的应用列表，而是直接进入到自己应用的悬浮窗权限开启界面
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, requestCode);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4-6.0
                //无需处理了
            } else {//4.4以下

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "applyFloatPermission Exception:" + e.getLocalizedMessage());
        }
    }
}