package com.alok.sense;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.alok.sense.utils.Logger;
import com.alok.sense.utils.ProcessedSensorEventListener;
import com.alok.sense.utils.SensorProvider;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;


public class ConfigurationActivity extends Activity implements ProcessedSensorEventListener {

    private Logger logger;
    private SensorProvider sensorProvider;

    LinearLayout layoutAccelerometer;
    GraphViewSeries seriesAccelerometerX;
    GraphView graphViewAccelerometer;
    double lastAccelerometerXAxis = 0;
    int xview = 10;
    private GraphView graphViewRotationSensor;
    private GraphViewSeries seriesRotationPitch;
    private LinearLayout layoutRotationSensor;
    private GraphViewSeries seriesMagneticFieldX;
    private GraphView graphViewMagneticField;
    private LinearLayout layoutMagneticField;
    private GraphViewSeries seriesAccelerometerY;
    private GraphViewSeries seriesAccelerometerZ;
    private GraphViewSeries seriesMagneticFieldZ;
    private GraphViewSeries seriesRotationRoll;
    private GraphViewSeries seriesMagneticFieldY;
    private double lastMagneticFieldXAxis = 0;
    private double lastRotationXAxis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        setupAccelerometerGraph();
        setupMagneticField();
        setupRotationSensorGraph();

        logger = new Logger(this);
        logger.initializeLoggingSession();
        sensorProvider = new SensorProvider(this, this);
        sensorProvider.startSensing();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getEventData(float values[], int sensorType) {

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                logger.writeToLog("Sensor Type: Accelerometer");
                logger.writeToLog("[X] : " + values[0]);
                logger.writeToLog("[Y] : " + values[1]);
                logger.writeToLog("[Z] : " + values[2]);
                seriesAccelerometerX.appendData(new GraphView.GraphViewData(lastAccelerometerXAxis, values[0]), true, 20);
                seriesAccelerometerY.appendData(new GraphView.GraphViewData(lastAccelerometerXAxis, values[1]), true, 20);
                seriesAccelerometerZ.appendData(new GraphView.GraphViewData(lastAccelerometerXAxis, values[2]), true, 20);

                lastAccelerometerXAxis += 1;
                graphViewAccelerometer.setViewPort(lastAccelerometerXAxis - xview, xview);
                layoutAccelerometer.removeView(graphViewAccelerometer);
                layoutAccelerometer.addView(graphViewAccelerometer);

                break;

            case Sensor.TYPE_ROTATION_VECTOR:
                logger.writeToLog("Sensor Type: Rotation Vector");
                logger.writeToLog("[Pitch] : " + values[1]);
                logger.writeToLog("[Roll] : " + values[2]);
                seriesRotationPitch.appendData(new GraphView.GraphViewData(lastRotationXAxis, values[0]), true, 20);
                seriesRotationRoll.appendData(new GraphView.GraphViewData(lastRotationXAxis, values[1]), true, 20);

                lastRotationXAxis += 1;
                graphViewRotationSensor.setViewPort(lastRotationXAxis - xview, xview);
                layoutRotationSensor.removeView(graphViewRotationSensor);
                layoutRotationSensor.addView(graphViewRotationSensor);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                logger.writeToLog("Sensor Type: Magnetic Field Vector");
                logger.writeToLog("[X] : " + values[0]);
                logger.writeToLog("[Y] : " + values[1]);
                logger.writeToLog("[Z] : " + values[2]);
                seriesMagneticFieldX.appendData(new GraphView.GraphViewData(lastMagneticFieldXAxis, values[0]), true, 20);
                seriesMagneticFieldY.appendData(new GraphView.GraphViewData(lastMagneticFieldXAxis, values[1]), true, 20);
                seriesMagneticFieldZ.appendData(new GraphView.GraphViewData(lastMagneticFieldXAxis, values[2]), true, 20);

                lastMagneticFieldXAxis += 1;
                graphViewMagneticField.setViewPort(lastMagneticFieldXAxis - xview, xview);
                layoutMagneticField.removeView(graphViewMagneticField);
                layoutMagneticField.addView(graphViewMagneticField);

                break;
        }
    }

    private void setupAccelerometerGraph()  {
        seriesAccelerometerX = new GraphViewSeries("",
                new GraphViewSeries.GraphViewSeriesStyle(Color.argb(255, 255, 255, 255), 10),//color and thickness of the line
                new GraphView.GraphViewData[]{new GraphView.GraphViewData(0, 0)});
        seriesAccelerometerY = new GraphViewSeries("",
                new GraphViewSeries.GraphViewSeriesStyle(Color.argb(255, 255, 255, 255), 10),//color and thickness of the line
                new GraphView.GraphViewData[]{new GraphView.GraphViewData(0, 0)});
        seriesAccelerometerZ = new GraphViewSeries("",
                new GraphViewSeries.GraphViewSeriesStyle(Color.argb(255, 255, 255, 255), 10),//color and thickness of the line
                new GraphView.GraphViewData[]{new GraphView.GraphViewData(0, 0)});

        graphViewAccelerometer = new LineGraphView(
                this // context
                , "" // heading
        );

        //graphViewAccelerometer.setBackgroundColor(Color.argb(255, 255, 255, 255));
        ((LineGraphView) graphViewAccelerometer).setDrawBackground(false);
        graphViewAccelerometer.addSeries(seriesAccelerometerX);
        graphViewAccelerometer.addSeries(seriesAccelerometerY);
        graphViewAccelerometer.addSeries(seriesAccelerometerZ);
        graphViewAccelerometer.setScrollable(true);
        graphViewAccelerometer.setViewPort(0, xview);
        graphViewAccelerometer.setShowLegend(false);
        graphViewAccelerometer.getGraphViewStyle().setGridColor(Color.argb(0, 255, 255, 255));
        graphViewAccelerometer.setManualYAxis(true);
        graphViewAccelerometer.setManualYAxisBounds(5, -5);
        graphViewAccelerometer.getGraphViewStyle().setHorizontalLabelsColor(Color.TRANSPARENT);
        graphViewAccelerometer.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
        graphViewAccelerometer.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.VERTICAL);

        layoutAccelerometer = (LinearLayout) findViewById(R.id.accelerometer);
        layoutAccelerometer.addView(graphViewAccelerometer);
    }

    private void setupRotationSensorGraph()  {
        seriesRotationPitch = new GraphViewSeries("",
                new GraphViewSeries.GraphViewSeriesStyle(Color.argb(255, 255, 255, 255), 10),//color and thickness of the line
                new GraphView.GraphViewData[]{new GraphView.GraphViewData(0, 0)});
        seriesRotationRoll = new GraphViewSeries("",
                new GraphViewSeries.GraphViewSeriesStyle(Color.argb(255, 255, 255, 255), 10),//color and thickness of the line
                new GraphView.GraphViewData[]{new GraphView.GraphViewData(0, 0)});

        graphViewRotationSensor = new LineGraphView(
                this // context
                , "" // heading
        );

        //graphViewRotationSensor.setBackgroundColor(Color.argb(255, 255, 255, 255));
        ((LineGraphView) graphViewRotationSensor).setDrawBackground(false);
        graphViewRotationSensor.addSeries(seriesRotationPitch);
        graphViewRotationSensor.addSeries(seriesRotationRoll);
        graphViewRotationSensor.setScrollable(true);
        graphViewRotationSensor.setViewPort(0, xview);
        graphViewRotationSensor.setShowLegend(false);
        graphViewRotationSensor.getGraphViewStyle().setGridColor(Color.argb(0, 255, 255, 255));
        graphViewRotationSensor.setManualYAxis(true);
        graphViewRotationSensor.setManualYAxisBounds(100, -100);
        graphViewRotationSensor.getGraphViewStyle().setHorizontalLabelsColor(Color.TRANSPARENT);
        graphViewRotationSensor.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
        graphViewRotationSensor.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.VERTICAL);

        layoutRotationSensor = (LinearLayout) findViewById(R.id.rotation);
        layoutRotationSensor.addView(graphViewRotationSensor);
    }

    private void setupMagneticField()  {
        seriesMagneticFieldX = new GraphViewSeries("",
                new GraphViewSeries.GraphViewSeriesStyle(Color.argb(255, 255, 255, 255), 10),//color and thickness of the line
                new GraphView.GraphViewData[]{new GraphView.GraphViewData(0, 0)});
        seriesMagneticFieldY = new GraphViewSeries("",
                new GraphViewSeries.GraphViewSeriesStyle(Color.argb(255, 255, 255, 255), 10),//color and thickness of the line
                new GraphView.GraphViewData[]{new GraphView.GraphViewData(0, 0)});
        seriesMagneticFieldZ = new GraphViewSeries("",
                new GraphViewSeries.GraphViewSeriesStyle(Color.argb(255, 255, 255, 255), 10),//color and thickness of the line
                new GraphView.GraphViewData[]{new GraphView.GraphViewData(0, 0)});

        graphViewMagneticField = new LineGraphView(
                this // context
                , "" // heading
        );

        //graphViewMagneticField.setBackgroundColor(Color.argb(255, 255, 255, 255));
        ((LineGraphView) graphViewMagneticField).setDrawBackground(false);
        graphViewMagneticField.addSeries(seriesMagneticFieldX);
        graphViewMagneticField.addSeries(seriesMagneticFieldY);
        graphViewMagneticField.addSeries(seriesMagneticFieldZ);
        graphViewMagneticField.setScrollable(true);
        graphViewMagneticField.setViewPort(0, xview);
        graphViewMagneticField.setShowLegend(false);
        graphViewMagneticField.getGraphViewStyle().setGridColor(Color.argb(0, 255, 255, 255));
        graphViewMagneticField.setManualYAxis(true);
        graphViewMagneticField.setManualYAxisBounds(100, -100);
        graphViewMagneticField.getGraphViewStyle().setHorizontalLabelsColor(Color.TRANSPARENT);
        graphViewMagneticField.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
        graphViewMagneticField.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.VERTICAL);

        layoutMagneticField = (LinearLayout) findViewById(R.id.magnetic_field);
        layoutMagneticField.addView(graphViewMagneticField);
    }

}
