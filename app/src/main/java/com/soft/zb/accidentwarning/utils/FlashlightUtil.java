package com.soft.zb.accidentwarning.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.CountDownTimer;

import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


/*
 * <!-- 闪光灯权限 -->
 * <uses-permission android:name="android.permission.FLASHLIGHT" />
 * <!-- 部分手机、android6.0 以上需要摄像头权限 -->
 * <uses-permission android:name="android.permission.CAMERA" />
 */
public class FlashlightUtil {


     // 判断设备是否能使用闪光灯
    public boolean hasFlashlight(Context context) {

        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }


    // Android 6.0	API23	VERSION_CODES：M
    private boolean isNewVersion() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    
    private Camera camera;
    private CameraManager cameraManager;

    // 手电筒是否关闭
    public boolean isOff(){
        
        if(isNewVersion()) { 
            return cameraManager == null;
            
        }else
            return camera == null;
    }


    /**
     * 打开手电筒
     **/
    public void lightsOn(Context context) {

        if(!isSosMode()) {
            sosOff();
        }
        
        if(hasFlashlight(context)) {
            if(isNewVersion()) {
                linghtOnNew(context);
                
            } else {
                linghtOnOld();
            }
        }else {
            Toast.makeText(context,"该手机不支持开启闪光灯", Toast.LENGTH_SHORT).show();
        }
    }


     // 安卓6.0以上打开手电筒
    @TargetApi(Build.VERSION_CODES.M)
    private void linghtOnNew(Context context) {
        try {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            cameraManager.setTorchMode("0", true); // 0 -> 主闪光灯

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


     // android6.0以下打开手电筒
    private void linghtOnOld() {
        if (camera == null) {
            camera = Camera.open();
            Camera.Parameters params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
        }
    }

    /**
     * 关闭手电筒
     * */
    public void lightsOff() {

        if (isNewVersion()) {
            lightsOfNew();

        } else {
            lightsOfOld();
        }
    }

    // 安卓6.0以上打关闭手电筒
    @TargetApi(Build.VERSION_CODES.M)
    private void lightsOfNew() {
        try {
            if (cameraManager == null) {
                return;
            }
            cameraManager.setTorchMode("0", false);
            cameraManager = null;
        } catch (Exception e) {
        }
    }


    // 安卓6.0以下关闭手电筒
    private void lightsOfOld() {
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.release();
            camera = null;
        }
    }
    


    /**
     * SOS闪光灯
     * */

    private boolean isSos = false;

    // 关闭SOS
    public void sosOff() {
        isSos = false;

        lightsOff();
    }


    public boolean isSosMode() {
        return isSos;
    }


    public void sosOn(final Context context){
        isSos = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isSosMode()) {
                    lightsOn(context);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    testLog("light：", "isSos : " + isSos);

                    lightsOff();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    testLog("light：", "isSos : " + isSos);
                }

                testLog("light：","Thread isSos : " + isSos);

            }
        }).start();
    }


    private void testLog(String tag, String msg){
        Log.i(tag, msg);
    }

}