package com.example.ecgdisplay;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import android.graphics.DashPathEffect;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;


public class ECG extends AppCompatActivity {
    LineChart linechart;
    String[] axisData = {"Jan", "Feb", "Mar", "Apr"};
    int[] yAxisData = {50, 20, 15, 30};
    String ip_adress = MainActivity.ip_address;
    ArrayList<Entry> entries;
    String[] months;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        linechart = findViewById(R.id.linechart);
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(100);  //1000ms = 1 sec
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GetData();
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();


    }
    public void CreateLineChart(){



        LineDataSet dataSet = new LineDataSet(entries, "Customized values");
        dataSet.enableDashedLine(10f, 5f, 0f);
        dataSet.enableDashedHighlightLine(10f, 5f, 0f);
        dataSet.setColor(Color.DKGRAY);
        dataSet.setCircleColor(Color.DKGRAY);
        dataSet.setLineWidth(1f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawFilled(true);
        dataSet.setFormLineWidth(1f);
        dataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        dataSet.setFormSize(15.f);

       // dataSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

        //****
        // Controlling X axis
        XAxis xAxis = linechart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(months);
        xAxis.setValueFormatter(formatter);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1


        //***
        // Controlling right side of y axis
        YAxis yAxisRight = linechart.getAxisRight();
        yAxisRight.setEnabled(false);

        //***
        // Controlling left side of y axis
        YAxis yAxisLeft = linechart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        // Setting Data
        xAxis.setLabelCount(12);
        LineData data = new LineData(dataSet);
        linechart.setData(data);
        linechart.animateX(0);
        //refresh
        linechart.invalidate();
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
            entries = new ArrayList<>();
            months = new String[5];
            while(rs.next()){
                entries.add(new Entry(check_index, rs.getInt("ecg_x")));
                months[check_index] = rs.getString("datetime");
                Log.e("data from server",String.valueOf(rs.getInt("ecg_x")));
                check_index++;
            }
            CreateLineChart();

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(ECG.this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
