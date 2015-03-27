package com.alok.sense.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Alok on 3/26/2015.
 */
public class SensorProvider {

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Sensor rotationSensor;
    private final Sensor magneticField;
    private WifiManager wifiManager;
    private WifiEventHandler wifiEventHandler;
    private final Activity activity;
    private float gravity[] = new float[3];
    private float linearAcceleration[] = new float[3];
    private float[] orientation;
    private float[] magneticFieldStrength = new float[3];
    private ProcessedSensorEventListener eventListener;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private SensorEventListener accelerometerEventHandler;
    private SensorEventListener rotationEventHandler;
    private SensorEventListener magneticEventHandler;

    public static boolean setAccelerometer = true;
    public static boolean setRotation = true;
    public static boolean setMagneticField = true;
    public static boolean setWifi = true;
    public static boolean setBle = true;

    public static final int WIFI = 1024;
    public static final int BLE = 2048;

    public SensorProvider(Activity activity, ProcessedSensorEventListener eventListener) {
        this.activity = activity;
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SenseWakeLock");
        this.eventListener = eventListener;

        accelerometerEventHandler = new AccelerometerEventHandler();
        rotationEventHandler = new RotationEventHandler();
        magneticEventHandler = new MagneticEventHandler();
        wifiEventHandler = new WifiEventHandler();
    }

    public void startSensing() {

        wakeLock.acquire();
        if (setAccelerometer)
            sensorManager.registerListener(accelerometerEventHandler, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (setRotation)
            sensorManager.registerListener(rotationEventHandler, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (setMagneticField)
            sensorManager.registerListener(magneticEventHandler, magneticField, SensorManager.SENSOR_DELAY_NORMAL);

        if (setWifi) {
            activity.registerReceiver(wifiEventHandler, new IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
        }
    }

    public void stopSensing() {

        if (setAccelerometer)
            sensorManager.unregisterListener(accelerometerEventHandler);

        if (setRotation)
            sensorManager.unregisterListener(rotationEventHandler);

        if (setMagneticField)
            sensorManager.unregisterListener(magneticEventHandler);

        if (setWifi)
            activity.unregisterReceiver(wifiEventHandler);

        wakeLock.release();
    }

    private void prepareAccelerometerData(SensorEvent event) {
        final float alpha = 0.8f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linearAcceleration[0] = event.values[0] - gravity[0];
        linearAcceleration[1] = event.values[1] - gravity[1];
        linearAcceleration[2] = event.values[2] - gravity[2];
    }

    private void prepareRotationVector(SensorEvent event) {
        orientation = event.values;
    }

    private void prepareMagneticField(SensorEvent event) {
        magneticFieldStrength = event.values;
    }

    private class AccelerometerEventHandler implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            prepareAccelerometerData(event);
            eventListener.getEventData(linearAcceleration, event.sensor.getType());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private class RotationEventHandler implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            prepareRotationVector(event);
            eventListener.getEventData(orientation, event.sensor.getType());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private class MagneticEventHandler implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            prepareMagneticField(event);
            eventListener.getEventData(magneticFieldStrength, event.sensor.getType());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private class WifiEventHandler extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            eventListener.getWifiEventData(wifiScanList, WIFI);
            wifiManager.startScan();
        }
    }
}
