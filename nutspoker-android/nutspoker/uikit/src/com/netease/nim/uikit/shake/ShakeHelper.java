package com.netease.nim.uikit.shake;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.nav.Nav;
import com.netease.nim.uikit.nav.UrlConstants;

/**
 * Created by 周智慧 on 17/5/4.
 */

public class ShakeHelper implements SensorEventListener {
    private static final String GLIDE_FRAGMENT = "com.bumptech.glide.supportManager.SupportRequestManagerFragment";
    private Activity activity;
    private SensorManager mSensorManager;

    private boolean isEnable = true;

    private ShakeHelper(Activity context) {
        this.activity = context;
    }

    //摇一摇是否可用，默认可用
    @SuppressWarnings("unused")
    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    //获取摇一摇的实例
    public static ShakeHelper initShakeHelper(Activity context) {
        return new ShakeHelper(context);
    }

    //回调Activity的onStart()
    public void onStart() {
        //获取 SensorManager 负责管理传感器
        mSensorManager = ((SensorManager) activity.getSystemService(Context.SENSOR_SERVICE));
        if (isEnable && mSensorManager != null) {
            //获取加速度传感器
            Sensor mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometerSensor != null) {
                mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    //回调Activity的onPause()
    public void onPause() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {//accelerometer
            //获取三个方向值
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            if ((Math.abs(x) > 17 || Math.abs(y) > 17 || Math.abs(z) > 17)) {
                doShaked();//摇动了
            }
        }
    }

    CustomAlertDialog dialog;
    private void doShaked() {
        if (CacheConstant.debugBuildType) {
            if (dialog == null) {
                dialog = new CustomAlertDialog(activity);
                dialog.setTitle(".debug....");
                dialog.addItem("进入彩蛋页", new CustomAlertDialog.onSeparateItemClickListener() {
                    @Override
                    public void onClick() {
                        Nav.from(activity).toUri(UrlConstants.DEVELOP);
                    }
                });
                dialog.addItem("进入network_monitor", new CustomAlertDialog.onSeparateItemClickListener() {
                    @Override
                    public void onClick() {
                        Nav.from(activity).toUri(UrlConstants.NETWORK_MONITOR);
                    }
                });
            }
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            if (activity.isFinishing()) {
                return;
            }
            dialog.show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}