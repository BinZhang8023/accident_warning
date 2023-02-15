package com.soft.zb.accidentwarning.service;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.amap.api.location.*;
import com.soft.zb.accidentwarning.MyAidl;
import com.soft.zb.accidentwarning.R;
import com.soft.zb.accidentwarning.activities.MainActivity;
import com.soft.zb.accidentwarning.activities.WarnActivity;
import com.soft.zb.accidentwarning.bean.LocalBean;
import com.soft.zb.accidentwarning.bean.SensorBean;
import com.soft.zb.accidentwarning.utils.ToastUtil;

import static com.amap.api.location.CoordinateConverter.calculateLineDistance;

public class LocalService extends Service implements AMapLocationListener {

    private SensorManager sensorManager;
    private MySensorListener sensorListener;
    private Sensor accelerometerSensor; // 加速度传感器
    private Sensor gyroscopeSensor;     // 角速度传感器
    // X：右左   Y：上下  Z：正反
    // 弧度->角度：180/PI * 弧度；    角度->弧度：PI/180* 角度
    private static Float[] accelerometer = new Float[]{0f, 0f, 0f};     // m/s^2
    private static Float[] gyroscope = new Float[]{0f, 0f, 0f};         // rad/s


    private AMapLocationClient aMapLocationClient;
    private AMapLocationClientOption aMapLocationClientOption;
    private DPoint startLoc;
    private DPoint endLoc;
    // Latitude ：纬度（北纬+，南纬-）（-90 ~ 90）  横纬线
    // Longitude：经度（东经+，西经-）（-180~180）  竖经线
    private static double latitude;
    private static double longitude;

    private static String location;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            testLogE("Client", "Connect");
            MyAidl myAidl = MyAidl.Stub.asInterface(iBinder);
            try{
                testLogE("client_data", myAidl.getData());
            }catch (RemoteException e){

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            testLogE("Client", "Disconnect");
            unbindService(serviceConnection);
            startService(new Intent(getApplicationContext(), RemoteService.class));
            startService(new Intent(getApplicationContext(), LocalService.class));
            testLogE("Local", "Restart Service");
        }
    };

    @Override
    public void onCreate() {
        testLogE("Client", "Client service have start");

        // 传感器初始化
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorListener = new MySensorListener();


        // 高德SDK定位
        aMapLocationClient = new AMapLocationClient(getApplicationContext());
        aMapLocationClient.setLocationListener(this);   // 定位回调监听
        aMapLocationClientOption = new AMapLocationClientOption();  // 初始化定位参数
        aMapLocationClientOption.setSensorEnable(true); // 当不是GPS定位时通过传感器返回getSpeed

        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        aMapLocationClientOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        aMapLocationClientOption.setInterval(2000); // 设置定位间隔,单位毫秒,默认为2000ms
        aMapLocationClient.setLocationOption(aMapLocationClientOption);
        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        aMapLocationClient.stopLocation();
        aMapLocationClient.startLocation();

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        bindService(new Intent(getApplicationContext(), RemoteService.class), serviceConnection, Context.BIND_IMPORTANT);
        testLogE("onBind", "client");
        // 注册传感器监听函数
        // SENSOR_DELAY_NOMAL 200ms     SENSOR_DELAY_UI 60ms        SENSOR_DELAY_GAME 20ms SENSOR_DELAY_FASTEST 最小采样周期（理论0ms）
        sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
        return myBinder;
    }

    MyAidl.Stub myBinder = new MyAidl.Stub() {
        @Override
        public String getData() throws RemoteException {
            return "Client";
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bindService(new Intent(getApplicationContext(), RemoteService.class), serviceConnection, Context.BIND_IMPORTANT);
        MyNotification();
        return START_STICKY;
    }

    private Context context;
    private void MyNotification(){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   // 兼容26以下SDK
            //创建渠道
            String id = "local_channel";
            String name = "LocalChannel";
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
//            mChannel.enableVibration(true);   // 震动
            notificationManager.createNotificationChannel(notificationChannel);

            Notification notification = new Notification.Builder(this, id)
                    .setContentTitle("事故预防")   // 标题
                    .setContentText("持续为您服务")   // 内容
                    .setSmallIcon(R.mipmap.ic_launcher)     //收到信息后状态栏显示的小图标
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)    //设置点击后取消Notification
                    .build();
            startForeground(110, notification);
        }else {
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)//设置小图标
                    .setContentTitle("事故预防")
                    .setContentText("持续为您服务")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            startForeground(110, notification);// 开始前台服务
        }
    }

    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
        stopForeground(true);// 停止前台服务  参数：表示是否移除之前的通知
        testLogE("Local", "Foreground Destory");
        startService(new Intent(getApplicationContext(), LocalService.class));
        startService(new Intent(getApplicationContext(), RemoteService.class));
        testLogE("Local", "Fpreground Restart");

        // 注销监听函数
        sensorManager.unregisterListener(sensorListener);

        aMapLocationClient.stopLocation();  // 停止定位后，本地定位服务并不会被销毁
        aMapLocationClient.onDestroy();     // 销毁定位客户端，同时销毁本地定位服务。
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation == null){
            testLogE("Location定位返回结果:", "为空");
        }else{
            if (aMapLocation.getErrorCode() == 0){
                testLog("Location定位类型", aMapLocation.getLocationType() + ""); // 1是GPS，只有GPS下可以正常用getSpeed
                testLog("Location定位结果", aMapLocation.toString());

                latitude = aMapLocation.getLatitude();  // 获取纬度
                longitude = aMapLocation.getLongitude();// 获取经度
                location = aMapLocation.getAddress();   // 获得详细地址

                testLog("Location经纬度(纬度，经度）", "( " + latitude + " , " + longitude + " )");

                if(startLoc == null){
                    startLoc = new DPoint(latitude, longitude);
                }else{
                    endLoc = new DPoint(latitude, longitude);
                    double distance = calculateLineDistance(startLoc, endLoc);
                    startLoc = endLoc;

                    // 单位1m/s-> 3.6km/h
                    float v_getSpeed = aMapLocation.getSpeed() * 3.6f;	// SDK获取速度	当不是GPS定位时，通过传感器获取速度m/s
                    testLog("Speed(getSpeed)", "SPEED = " + v_getSpeed);

                    // 因为2s定位一次，V = S / T
                    double v_calcDis = (distance * 3.6f) / 2 ;
                    testLog("Speed(V = S/T)", "Dis = " + distance + "   V = " + v_calcDis);

                    if(v_getSpeed > 15 && v_calcDis > 15){
                        ToastUtil.show(this);
                    }else{
                        ToastUtil.notshow();
                    }
                }
            }else{
                // 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见高德错误码表。
                Log.e("AmapError", "Location Error, " +
                        "ErrCode:" + aMapLocation.getErrorCode() +
                        ", ErrInfo:" + aMapLocation.getErrorInfo());
            }
        }
    }

    public static SensorBean getSensorData(){
        return new SensorBean(accelerometer, gyroscope);
    }

    public static LocalBean getLocalData(){
        return new LocalBean(longitude, latitude, location);
    }

    double acc_X = 0, acc_Y = 0, acc_Z = 0;
    double sqAcc, accMagnitude;
    double gyro_X = 0, gyro_Y, gyro_Z;
    double pitch, roll, yaw;    // 姿态角
    double angle;

    class MySensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){  // 加速度传感器
                accelerometer[0] = sensorEvent.values[0];       acc_X = sensorEvent.values[0];
                accelerometer[1] = sensorEvent.values[1];       acc_Y = sensorEvent.values[1];
                accelerometer[2] = sensorEvent.values[2];       acc_Z = sensorEvent.values[2];

                acc_X = Math.round(acc_X*100) / 100;
                acc_Y = Math.round(acc_Y*100) / 100;
                acc_Z = Math.round(acc_Z*100) / 100;

                testLog("acc", "X = " + acc_X + "  Y = " + acc_Y + "  Z = " + acc_Z);

                sqAcc = acc_X*acc_X + acc_Y *acc_Y + acc_Z* acc_Z;
                accMagnitude = Math.sqrt(sqAcc) / 9.8;
                angle = Math.acos(acc_Z/accMagnitude);

                pitch = acc_X / Math.sqrt(acc_Y*acc_Y + acc_Z*acc_Z);      pitch = Math.atan(pitch);
                roll = acc_Y / Math.sqrt(acc_X*acc_X + acc_Z*acc_Z);      pitch = Math.atan(roll);
                yaw = Math.sqrt(acc_X*acc_X + acc_Y*acc_Y) / acc_Z;      pitch = Math.atan(yaw);

                testLog("acc", "angle = " + angle + "  pitch = " + pitch + "  roll = " + roll + "  yaw = " + yaw);

                if(accMagnitude > 2){
                    if(angle > 2){
                        FallDetection();
                    }
                }

            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE){    //陀螺仪传感器
                gyroscope[0] = sensorEvent.values[0];       gyro_X = sensorEvent.values[0];
                gyroscope[1] = sensorEvent.values[1];       gyro_Y = sensorEvent.values[1];
                gyroscope[2] = sensorEvent.values[2];       gyro_Z = sensorEvent.values[2];

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            testLogE("Sensor", "Sensor Accuracy Changed");
        }

        private void FallDetection() {
            testLogE("Sensor", "检测为跌倒");
            Intent intent = new Intent(getApplicationContext(), WarnActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 启动服务时无该标记可能异常
            getApplicationContext().startActivity(intent);
        }
    }

    private void testLogE(String tag, String msg){
        Log.e(tag, msg);
    }

    private void testLog(String tag, String msg){
        Log.w(tag, msg);
    }
}