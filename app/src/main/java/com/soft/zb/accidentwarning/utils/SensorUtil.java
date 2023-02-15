package com.soft.zb.accidentwarning.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.soft.zb.accidentwarning.bean.SensorBean;


public class SensorUtil {

    private Context context;
    private SensorManager sensorManager;
    private MySensorListener sensorListener;
    private Sensor accelerometerSensor; // 加速度传感器
    private Sensor gyroscopeSensor;     // 角速度传感器
    private Sensor magnetometerSensor;      // 地磁传感器
    private Sensor linerSensor;         // 线性加速度传感器
    private Sensor gravitySensor;       // 重力传感器

    private FallDetection fallDetection;


    public SensorUtil(Context context){
        this.context = context;
        fallDetection = new FallDetection();

    }

    /**
     * 初始化传感器
     */
    public void initSensor(){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        linerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        sensorListener = new MySensorListener();

    }


    /**
     * 注册传感器
     */
    public void registerSensor(){
        // 注册传感器监听函数
        // SENSOR_DELAY_NOMAL 200ms     SENSOR_DELAY_UI 60ms        SENSOR_DELAY_GAME 20ms SENSOR_DELAY_FASTEST 最小采样周期（理论0ms）
        sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorListener, magnetometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorListener, linerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorListener, gravitySensor, SensorManager.SENSOR_DELAY_UI);
    }


    /**
     * 注销传感器
     */
    public void unregisterSensor(){
        sensorManager.unregisterListener(sensorListener);
    }


    public static SensorBean getSensorData(){
        return new SensorBean(accelerometer, gyroscope);
    }


    // X：右左   Y：上下  Z：正反
    // 弧度->角度：180/PI * 弧度；    角度->弧度：PI/180* 角度
    private static Float[] accelerometer = new Float[]{0f, 0f, 0f};     // m/s^2
    private static Float[] gyroscope = new Float[]{0f, 0f, 0f};         // rad/s

    class MySensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){  // 加速度传感器
                accelerometer[0] = sensorEvent.values[0];
                accelerometer[1] = sensorEvent.values[1];
                accelerometer[2] = sensorEvent.values[2];

                fallDetection.getAccelerometerData(sensorEvent.values);


            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE){    //陀螺仪传感器
                gyroscope[0] = sensorEvent.values[0];
                gyroscope[1] = sensorEvent.values[1];
                gyroscope[2] = sensorEvent.values[2];

                fallDetection.getGyroscopeData(sensorEvent.values);


            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {    // 地磁传感器
                fallDetection.getMagnetometerData(sensorEvent.values);


            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {    // 线性加速度传感器
                fallDetection.getLinearData(sensorEvent.values);

            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY){ // 重力传感器
                fallDetection.getGravityData(sensorEvent.values);

            }

            fallDetection.getAttitudeData();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }


    }

}
