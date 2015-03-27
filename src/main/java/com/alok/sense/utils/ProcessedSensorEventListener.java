package com.alok.sense.utils;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by Alok on 3/26/2015.
 */
public interface ProcessedSensorEventListener {

    public void getEventData(float values[], int sensorType);
    public void getWifiEventData(List<ScanResult> scanResults, int sensorType);
}
