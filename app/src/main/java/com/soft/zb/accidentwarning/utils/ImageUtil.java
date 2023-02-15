package com.soft.zb.accidentwarning.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImageUtil {

    public void changeView(Context context, ImageView imageView) {
        //ImageView调整后的宽、高
        int width, height;

        // 获取屏幕宽、高
        DisplayMetrics displayMetrics = new DisplayMetrics();

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int windowWidth = displayMetrics.widthPixels;
        int windowHeight = displayMetrics.heightPixels;

        // 获取图片宽、高
        Drawable drawable = imageView.getDrawable();
        int picWidth = drawable.getIntrinsicWidth();
        int picHeight = drawable.getIntrinsicHeight();

        // 屏幕宽高比
        float windowScale = (float) windowWidth / windowHeight;
        // 图片宽高比
        float picScale = (float) picWidth / picHeight;


        // 缩放比
        // windowScale > picScale，高相等时，屏幕宽，图片适应高度，缩放比是二者的高之比，图片宽度用缩放比计算
        // windowScale < picScale，高相等时，图片宽，图片适应宽度，缩放比是二者的宽之比，图片高度用缩放比计算
        float scale = 1.0f;
        if (windowScale > picScale) {
            scale = (float) picHeight / windowHeight;
            height = windowHeight;               // 图片高度适应屏幕高度
            width = (int) (picWidth * scale);    // 图片宽度自适应

        } else if (windowScale < picScale) {
            scale = (float) picWidth / windowWidth;
            width = windowWidth;
            height = (int) (picHeight / scale);

        } else {
            width = windowWidth;
            height = windowHeight;
        }

        //重设ImageView宽高
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        imageView.setLayoutParams(params);
    }

}
