package com.soft.zb.accidentwarning.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

public class MiuiUtil {


    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";


    /**
     *  判断是否是小米手机（MIUI系统）
     *
     * @return
     */
    public boolean isMIUI() {
        String device = Build.MANUFACTURER;
//        if (device.equals("Xiaomi")) {    // 是否是小米手机
//            return true;
//        }
        if (device.equals("Xiaomi")) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) { // Android 8.0 及以上
                return !TextUtils.isEmpty(getSystemProperty(KEY_MIUI_VERSION_CODE, ""))
                        || !TextUtils.isEmpty(getSystemProperty(KEY_MIUI_VERSION_NAME, ""))
                        || !TextUtils.isEmpty(getSystemProperty(KEY_MIUI_INTERNAL_STORAGE, ""));

            } else {
                Properties prop = new Properties();
                try {
                    prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"))); // build.prop系统配置文件在Android O上不可读

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                        || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                        || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
            }
        } else {
            return false;
        }
    }


    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> aClass = Class.forName("android.os.SystemProperties");
            Method get = aClass.getMethod("get", String.class, String.class);  // 使用反射来获取到该方法
            return (String) get.invoke(aClass, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }



    /**
     * 跳转到权限管理界面
     * @param context
     */
    public void goPermissionSettings(Activity context) {
        Intent intent;
        // MIUI8/9/10
        try {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // MIUI5/6/7
            try {
                intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(intent);

            } catch (ActivityNotFoundException e1) {
                // 应用信息界面
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);

            }
        }
    }

}
