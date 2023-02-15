package com.soft.zb.accidentwarning.utils;

import android.hardware.SensorManager;
import android.util.Log;

public class FallDetection {

    private static float[][] accelerometerValues; // 加速度传感器数据
    private static float[][] gyroscopeValues;  // 角速度传感器数据
    private static float[][] gravityValues;  // 重力传感器数据
    private static float[][] linearValues;     // 线性加速度传感器数据
    private static float[][] magnetometerValues;  // 地磁传感器数据

    private static double[][] attitudeAngle;  // 姿态角数据
    public static double[] smvData;  // 合加速度数据

    public static int dataLength = 50;

    public static int accCount = 0;
    public static int gyrCount = 0;
    public static int graCount = 0;
    public static int linCount = 0;
    public static int magCount = 0;
    public static int attCount = 0;
    public static int smvCount = 0;

    private boolean ISFALL;


    /**
     * 采样频率SENSOR_DELAY_UI：60ms，采集3s --> 数组长50
     */
    public FallDetection(){
        accelerometerValues = new float[3][50];
        gyroscopeValues = new float[3][50];
        gravityValues = new float[3][50];
        linearValues = new float[3][50];
        magnetometerValues = new float[3][50];
        attitudeAngle = new double[3][50];
        smvData = new double[50];
        ISFALL = false;
    }


    /**
     * 加速度传感器数据
     * @param sensorData
     */
    public static void getAccelerometerData(float[] sensorData){
        Log.d("falldetection-acc", accCount + "  length = " + accelerometerValues.length );

        if(accCount < dataLength){
            accelerometerValues[0][accCount] = sensorData[0];
            accelerometerValues[1][accCount] = sensorData[1];
            accelerometerValues[2][accCount] = sensorData[2];
        }else{
            accCount = 0;
            accelerometerValues[0][accCount] = sensorData[0];
            accelerometerValues[1][accCount] = sensorData[1];
            accelerometerValues[2][accCount] = sensorData[2];
        }

        Log.d("falldetection-acc", accelerometerValues[0][accCount] + " " + accelerometerValues[1][accCount]+ " " + accelerometerValues[2][accCount]);
        getSmvData();
        accCount++;
    }

    /**
     * 角速度传感器数据
     * @param sensorData
     */
    public static void getGyroscopeData(float[] sensorData){
        if(gyrCount < dataLength){
            gyroscopeValues[0][gyrCount] = sensorData[0];
            gyroscopeValues[1][gyrCount] = sensorData[1];
            gyroscopeValues[2][gyrCount] = sensorData[2];
        }else{
            gyrCount = 0;
            gyroscopeValues[0][gyrCount] = sensorData[0];
            gyroscopeValues[1][gyrCount] = sensorData[1];
            gyroscopeValues[2][gyrCount] = sensorData[2];
        }
        Log.d("falldetection-gyros", gyroscopeValues[0][gyrCount] + " " + gyroscopeValues[1][gyrCount]+ " " + gyroscopeValues[2][gyrCount]);

        gyrCount++;
    }

    /**
     * 重力传感器数据
     * @param sensorData
     */
    public static void getGravityData(float[] sensorData) {
        if(graCount < dataLength){
            gravityValues[0][graCount] = sensorData[0];
            gravityValues[1][graCount] = sensorData[1];
            gravityValues[2][graCount] = sensorData[2];
        }else{
            graCount = 0;
            gravityValues[0][graCount] = sensorData[0];
            gravityValues[1][graCount] = sensorData[1];
            gravityValues[2][graCount] = sensorData[2];
        }
        Log.d("falldetection-gravi", gravityValues[0][graCount] + " " + gravityValues[1][graCount]+ " " + gravityValues[2][graCount]);

        graCount++;
    }

    /**
     * 线性加速度传感器数据
     * @param sensorData
     */
    public static void getLinearData(float[] sensorData) {
        if(linCount < dataLength){
            linearValues[0][linCount] = sensorData[0];
            linearValues[1][linCount] = sensorData[1];
            linearValues[2][linCount] = sensorData[2];
        }else{
            linCount = 0;
            linearValues[0][linCount] = sensorData[0];
            linearValues[1][linCount] = sensorData[1];
            linearValues[2][linCount] = sensorData[2];
        }
        Log.d("falldetection-linear", linearValues[0][linCount] + " " + linearValues[1][linCount]+ " " + linearValues[2][linCount]);

        linCount++;
    }

//    static float[] graData = new float[3];
//    public void calculateLinearAcc() {
//        // alpha计算为t /（t + dT）
//        // 使用t，低通滤波器的时间常数
//        // 和dT，事件传递率
//
//        final float alpha = 0.8f;
//
//        graData[0] = alpha * gravityValues[0][linCount] + (1 - alpha) * accelerometerValues[0][linCount];
//        graData[1] = alpha * gravityValues[1][linCount] + (1 - alpha) * accelerometerValues[1][linCount];
//        graData[2] = alpha * gravityValues[2][linCount] + (1 - alpha) * accelerometerValues[2][linCount];
//
//        linearValues[0][linCount] = accelerometerValues[0][linCount] - gravityValues[0][linCount];
//        linearValues[1][linCount] = accelerometerValues[1][linCount] - gravityValues[1][linCount];
//        linearValues[2][linCount] = accelerometerValues[2][linCount] - gravityValues[2][linCount];
//    }


    /**
     * 地磁传感器数据
     * @param sensorData
     */
    public static void getMagnetometerData(float[] sensorData) {
        if(magCount < dataLength){
            magnetometerValues[0][magCount] = sensorData[0];
            magnetometerValues[1][magCount] = sensorData[1];
            magnetometerValues[2][magCount] = sensorData[2];
        }else{
            magCount = 0;
            magnetometerValues[0][magCount] = sensorData[0];
            magnetometerValues[1][magCount] = sensorData[1];
            magnetometerValues[2][magCount] = sensorData[2];
        }
        Log.d("falldetection-magneto", magnetometerValues[0][magCount] + " " + magnetometerValues[1][magCount]+ " " + magnetometerValues[2][magCount]);

        magCount++;
    }

    /**
     * 姿态角数据：azimuth    pitch   roll
     *
     *     values[0]  azimuth 方向角，绕Z轴，用（磁场+加速度）得到的数据范围是（-180～180）,0表示正北，90表示正东，180/-180表示正南，-90表示正西。
     *     values[1]  pitch   倾斜角，绕X轴  即由静止状态开始，前后翻转，手机顶部往上抬起（0~-180），手机尾部往上抬起（0~180）
     *     values[2]  roll    旋转角，绕Y轴  即由静止状态开始，左右翻转，手机左侧抬起（0~180）,手机右侧抬起（0~-180）
     *
     * @param sensorData
     */
    static float[] accData = new float[3];
    static float[] magData = new float[3];
    static float[] attData = new float[3];
    public static void getAttitudeData() {

        if(attCount < dataLength){
            Log.d("falldetection-angle", "attCount = " + attCount + " " + " accCount = " + accCount + " magCount" + magCount);
//            if((attCount+1)%50 <= accCount && (attCount+1)%50 <= magCount) {
                float[] R_Matrix = new float[9];
                for(int i = 0; i < 3; i++){
                    accData[i] = accelerometerValues[i][attCount];
                    magData[i] = magnetometerValues[i][attCount];
                }

                if(SensorManager.getRotationMatrix(R_Matrix, null, accData, magData)) {
                    SensorManager.getOrientation(R_Matrix, attData);
                    attitudeAngle[0][attCount] = Math.toDegrees(attData[0]);
                    attitudeAngle[1][attCount] = Math.toDegrees(attData[1]);
                    attitudeAngle[2][attCount] = Math.toDegrees(attData[2]);
                    Log.d("falldetection-angle", attitudeAngle[0][attCount] + " " + attitudeAngle[1][attCount]+ " " + attitudeAngle[2][attCount]);

                    attCount++;
                }
//            }

        }else{
            attCount = 0;
            getAttitudeData();
        }

    }



    public static void getSmvData(){


        if(smvCount < dataLength){
            Log.d("falldetection-smvData", "accCount = " + accCount + "  smvCount = " + smvCount);

            if((smvCount+1)%50 <= accCount) {
                double acc_X = accelerometerValues[0][smvCount];
                double acc_Y = accelerometerValues[1][smvCount];
                double acc_Z = accelerometerValues[2][smvCount];
                double sqAcc = acc_X*acc_X + acc_Y *acc_Y + acc_Z* acc_Z;
                double accMagnitude = Math.sqrt(sqAcc) / 9.8;
                smvData[smvCount] = accMagnitude;
                Log.d("falldetection-smvData", smvData[smvCount] + "");
                smvCount++;
            }
        }else{
            smvCount = 0;
            getSmvData();

        }


    }

    public void fallDetectionAlgorithm(){
        Log.e("MYThread", "fall");

        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean flag = true;
                while (flag){

                    for(int i = 0;i < smvData.length; i++){
                        double angle = Math.acos(accelerometerValues[2][i]/smvData[i]);
                        if(smvData[i] > 2 && angle > 2){
                            setFlag(true);
                            flag = false;
                            break;
                        }
                    }
                }
            }
        }).start();
    }


    public boolean isFall(){
        return ISFALL;
    }

    public void setFlag(boolean flag){
        ISFALL = flag;
    }

    public void cleanData() {
        for (int i = 0; i < smvData.length; i++) {
            smvData[i] = 0;
        }
        smvCount = 0;
    }
}
