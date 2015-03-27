package com.alok.sense.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by Alok on 3/26/2015.
 */
public class SensorProvider {

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Sensor rotationSensor;
    private final Sensor magneticField;
    private final Activity activity;
    private float gravity[] = new float[3];
    private float linearAcceleration[] = new float[3];
    private float[] orientation;
    private float[] magneticFieldStrength = new float[3];
    private ProcessedSensorEventListener eventListener;

    private SensorEventListener accelerometerEventHandler;
    private SensorEventListener rotationEventHandler;
    private SensorEventListener magneticEventHandler;

    public SensorProvider(Activity activity, ProcessedSensorEventListener eventListener) {
        this.activity = activity;
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.eventListener = eventListener;

        accelerometerEventHandler = new AccelerometerEventHandler();
        rotationEventHandler = new RotationEventHandler();
        magneticEventHandler = new MagneticEventHandler();
    }

    public void startSensing() {
        sensorManager.registerListener(accelerometerEventHandler, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(rotationEventHandler, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(magneticEventHandler, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopSensing()   {
        sensorManager.unregisterListener(accelerometerEventHandler);
        sensorManager.unregisterListener(rotationEventHandler);
        sensorManager.unregisterListener(magneticEventHandler);
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

    private class AccelerometerEventHandler implements SensorEventListener  {
        @Override
        public void onSensorChanged(SensorEvent event) {
            prepareAccelerometerData(event);
            eventListener.getEventData(linearAcceleration, event.sensor.getType());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private class RotationEventHandler implements SensorEventListener  {
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
}
