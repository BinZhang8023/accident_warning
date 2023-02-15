package com.soft.zb.accidentwarning.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import com.soft.zb.accidentwarning.MyAidl;

// 利用android:process=":remote"，让服务在另一个进程中运行
// 利用AIDL工具实现一个进程访问另一个进程的内存空间
public class RemoteService extends Service {
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("Remote", "Connected");
            MyAidl myAidl = MyAidl.Stub.asInterface(iBinder);
            try {
                Log.e("Remote_data", myAidl.getData());
            }catch (RemoteException e){

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("Remote", "Disconnected");
            unbindService(serviceConnection);
            startService(new Intent(getApplicationContext(), LocalService.class));
            startService(new Intent(getApplicationContext(), RemoteService.class));
            Log.e("Remote","Restart");
        }
    };

    @Override
    public void onCreate() {
        Log.e("Remote", "Remote Service have Start");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        bindService(new Intent(getApplicationContext(), LocalService.class), serviceConnection, Context.BIND_IMPORTANT);
        Log.e("onBind","Remote");
        return myBinder;
    }

    MyAidl.Stub myBinder = new MyAidl.Stub() {
        @Override
        public String getData() throws RemoteException {
            return "Remote";
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bindService(new Intent(getApplicationContext(), LocalService.class), serviceConnection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
        Log.e("Remote", "Destroy");
        startService(new Intent(getApplicationContext(), LocalService.class));
        startService(new Intent(getApplicationContext(), RemoteService.class));
        Log.e("Remote","Restart");
    }
}
