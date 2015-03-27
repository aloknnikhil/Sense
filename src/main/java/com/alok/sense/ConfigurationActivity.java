package com.alok.sense;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.alok.sense.utils.Logger;
import com.alok.sense.utils.ProcessedSensorEventListener;
import com.alok.sense.utils.SensorProvider;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;


public class ConfigurationActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private CheckBox accelerometer;
    private CheckBox rotation;
    private CheckBox magneticField;
    private CheckBox wifi;
    private CheckBox ble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        accelerometer = (CheckBox) findViewById(R.id.set_accelerometer);
        rotation = (CheckBox) findViewById(R.id.set_rotation);
        magneticField = (CheckBox) findViewById(R.id.set_magnetic);
        wifi = (CheckBox) findViewById(R.id.set_wifi);
        ble = (CheckBox) findViewById(R.id.set_ble);

        accelerometer.setChecked(true);
        rotation.setChecked(true);
        magneticField.setChecked(true);
        wifi.setChecked(true);
        ble.setChecked(true);

        accelerometer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SensorProvider.setAccelerometer = isChecked;
            }
        });

        rotation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SensorProvider.setRotation = isChecked;
            }
        });

        magneticField.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SensorProvider.setMagneticField = isChecked;
            }
        });

        wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SensorProvider.setWifi = isChecked;
            }
        });

        ble.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SensorProvider.setBle = isChecked;
            }
        });

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_realtime:
                startActivity(new Intent(this, RealTimeGraphingActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
