package com.soft.zb.accidentwarning.utils;

import android.app.PendingIntent;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.List;

/*
 * <!-- 发送短信权限（危险级） -->
 * <uses-permission android:name="android.permission.SEND_SMS" />
 */

public class SmsUtil {
    public void sendMsgToPeople(String phoneNumber, String message){

        SmsManager smsManager = SmsManager.getDefault();

        // 通过sendMultipartTextMessage()方法发送超长短信
        if (message.length() > 70) {
            ArrayList<String> msgs = smsManager.divideMessage(message);
            ArrayList<PendingIntent> msgIntent =  new ArrayList<PendingIntent>();
            for(int i = 0;i<msgs.size();i++){
                msgIntent.add(null);
            }
            smsManager.sendMultipartTextMessage(phoneNumber, null, msgs, msgIntent, null);
        // 发送短短信
        } else {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        }

//        SmsManager smsManager = SmsManager.getDefault();
//
//        //拆分短信内容（手机短信长度限制）
//        List<String> divideContents = smsManager.divideMessage(message);
//        for (String text : divideContents) {
//            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
//        }

//        smsManager.sendTextMessage(phoneNumber.replace(" ","").replace(";",""), null, message, null, null);
    }
}
