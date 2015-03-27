package com.alok.sense.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

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
    private float[] orientation = new float[3];
    private float[] magneticFieldStrength = new float[3];
    private ProcessedSensorEventListener eventListener;

    public SensorProvider(Activity activity, ProcessedSensorEventListener eventListener) {
        this.activity = activity;
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.eventListener = eventListener;
    }

    public void startSensing() {
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                prepareAccelerometerData(event);
                eventListener.getEventData(linearAcceleration, event.sensor.getType());
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                prepareRotationVector(event);
                eventListener.getEventData(orientation, event.sensor.getType());
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                prepareMagneticField(event);
                eventListener.getEventData(magneticFieldStrength, event.sensor.getType());
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
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
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

        int worldAxisForDeviceAxisX = SensorManager.AXIS_X;
        int worldAxisForDeviceAxisY = SensorManager.AXIS_Z;

        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX,
                worldAxisForDeviceAxisY, adjustedRotationMatrix);

        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        // Convert radians to degrees
        orientation[1] = orientation[1] * -57;
        orientation[2] = orientation[2] * -57;
    }

    private void prepareMagneticField(SensorEvent event) {
        magneticFieldStrength = event.values;
    }
}
