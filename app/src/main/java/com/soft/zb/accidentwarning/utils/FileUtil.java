package com.soft.zb.accidentwarning.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * <!-- 写入扩展存储，向扩展卡写入数据 -->
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 */

public class FileUtil {
    private static String saveDataPATH = Environment.getExternalStorageDirectory() + "/";
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    // 在SD卡上创建目录
    private static void createSDDirs(String filePath) {
        File dir = new File(saveDataPATH + filePath);
        if(dir.exists()) {
            return;
        }

        dir.mkdirs();
    }

    // 在创建文件
    private static File createSDFile(String filePathAndName) {
        File file = new File(saveDataPATH + filePathAndName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.e("createFile", e.toString());
        }
        return file;
    }

    // 文件保存至SD卡
    private static File creatFileToSD(String filePath, String fileName) {
        File file = new File(saveDataPATH + filePath+"/" + fileName);
        if(file.exists()) {
            return file;
        }
        createSDDirs(filePath);
        return createSDFile(filePath+"/"+ fileName);
    }

    // 写文件
    public static void writeSensorDataToFile(String msg, String filePath, String fileName) {
        String strContent = getNowTime() + " " + msg + "\r\n";
        try {
            File file = creatFileToSD(filePath,fileName);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(file.length());
            randomAccessFile.write(strContent.getBytes());
            randomAccessFile.close();
        } catch (Exception e) {
            Log.e("File", "Error when write on File:" + e);
        }
    }

    public static String getNowTime(){
        return format.format(new Date(System.currentTimeMillis()));
    }

}
