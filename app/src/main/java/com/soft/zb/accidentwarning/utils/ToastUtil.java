package com.soft.zb.accidentwarning.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import com.soft.zb.accidentwarning.R;

public class ToastUtil {
    private static Toast toast;
    private static CountDownTimer timer;

    public static void show(Context context){
        View layout = LayoutInflater.from(context).inflate(R.layout.layout_toast, null, false);

        if(toast == null){
            toast = new Toast(context);

            // 设置Toast位置
            toast.setGravity(Gravity.FILL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);

            timer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long l) {    // 1s执行1次
                    toast.show();
                }

                @Override
                public void onFinish() {    // 倒计时结束后执行
                    timer.start();
                }
            };
        }

        timer.start();
    }

    public static void notshow(){
        if(toast != null){
            timer.cancel();
            toast.cancel();
            toast = null;
        }
    }
}
