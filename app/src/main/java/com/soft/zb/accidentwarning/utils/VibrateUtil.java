package com.soft.zb.accidentwarning.utils;

import android.content.Context;
import android.os.Vibrator;


/*
 *  <!-- 震动权限 -->
 * <uses-permission android:name="android.permission.VIBRATE" />
 */

public class VibrateUtil {

    private Vibrator vibrator;
    private Context context;

    public VibrateUtil(Context context) {
        this.context = context;
    }

   // 开始震动
    public void startVibrate(){

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if(!vibrator.hasVibrator()){
            return;
        }

        long[] pattern = {100, 1000, 100, 1000};
        vibrator.vibrate(pattern, 2);
    }


    //停止震动
    public void stopVibrate(){

        vibrator.cancel();
    }
}
