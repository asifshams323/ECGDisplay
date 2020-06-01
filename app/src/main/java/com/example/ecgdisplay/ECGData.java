package com.example.ecgdisplay;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ECGData extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private  Sensor sensors;
    String ip_adress = MainActivity.ip_address;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    int check_interval = 0;
    int current_value = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecgdata);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for(int i=0; i<sensors.size(); i++){
            Log.d(TAG, "onCreate: Sensor "+ i + ": " + sensors.get(i).toString());
        }

        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        mChart = (LineChart) findViewById(R.id.chart5);

        // enable description text


        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        feedMultiple();
//        Thread t = new Thread() {
//            @Override
//            public void run() {
//                while (!isInterrupted()) {
//                    try {
//                        Thread.sleep(100);  //1000ms = 1 sec
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                GetData();
//                            }
//                        });
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//
//        t.start();

    }

    private void addEntry(SensorEvent event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            final int random = new Random().nextInt(600) + 100;
            if(check_interval == 0) {
                data.addEntry(new Entry(set.getEntryCount(), (float) 0), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) 50), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) 90), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) 40), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) 0), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) random), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) -50), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) 0), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) 0), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) 0), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) 70), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) 30), 0);
                data.addEntry(new Entry(set.getEntryCount(), (float) 0), 0);
                if(random > 640){


                    new AlertDialog.Builder(ECGData.this)
                            .setTitle("Alert! Ecg is abnormal")
                            .setMessage("Suggestion:\n" +
                                    "May have high blood pressure use Vasodilators such as nitroprusside and nitroglycerin.\n" +
                                    "May have electrolyte imbalances provide him fluids, electrolyte-containing beverages, or medications to restore electrolytes.\n" +
                                    "May have Irregular heart rhythm place pacemaker on chest to control abnormal heart rhythms\n")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();


                }
                else{

                }
            }
            else{
                data.addEntry(new Entry(set.getEntryCount(), (float) 0), 0);
            }
            if(check_interval == 50){
                check_interval = 0;
            }
            else{
                check_interval ++;
            }

        //    data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);
            Log.e("Sensor data", String.valueOf(random));
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(50);
         //    mChart.setVisibleYRange(30, YAxis.AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.parseColor("#3c8079"));
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        mSensorManager.unregisterListener(this);

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(plotData){
            addEntry(event);
            plotData = false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(ECGData.this);
        thread.interrupt();
        super.onDestroy();
    }
    public void GetData(){
        try {
            String mysqlurl = "jdbc:mysql://"+ip_adress+":3306/" + "ecg_data";
            Class.forName("com.mysql.jdbc.Driver");
            Connection mysqlcon = DriverManager.getConnection(mysqlurl, "ecg", "ecg");
            Statement stmt = mysqlcon.createStatement();
            String query = "SELECT * FROM ecg_value ORDER BY id DESC LIMIT 5";
            ResultSet rs = stmt.executeQuery(query);
            int check_index = 0;
            rs.next();
            current_value = rs.getInt("ecg_x");


        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(ECGData.this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
