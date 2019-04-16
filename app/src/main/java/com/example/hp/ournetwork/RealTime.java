package com.example.hp.ournetwork;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RealTime extends AppCompatActivity  implements AdapterView.OnItemSelectedListener, SensorEventListener,
        View.OnClickListener {

    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private int j= 0;
    private boolean started=false;
    private SensorManager sensorManager;
    private Button btnStart, btnStop,reg;
    private ArrayList<AccelData> sensorData;
    SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
    int record ;


    private  Firebase myRef;
    private  DatabaseReference myRef1;

    FirebaseDatabase database;
    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_real_time);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // we get graph view instance
        Spinner spinner= findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this , R.array.axes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        GraphView graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(1000);
        //   viewport.setScrollable(true);
        viewport.setMinY(0);
        viewport.setMaxY(10);
        viewport.setMaxYAxisSize(2);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorData=new ArrayList<AccelData>();
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isval){
                if(isval){
                    return sdf.format(new Date((long) value));
                }else{
                    return super.formatLabel(value,isval);

                }
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (started == true) {
            sensorManager.unregisterListener(this);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();

        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {

                // we add 100 new entries
                for (int i = 0; i < 100; i++) {


                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (started) {

                                Random rn = new Random();
                                final long date = new Date().getTime();
                                double val= new Double(0.15 + rn.nextInt(9 - 0 + 1) * 0.172);
                                DataPoint pointx = new DataPoint(date,val);


                                // here, we choose to display max 10 points on the viewport and we scroll to end
                                series.appendData(pointx, true, 30);
                            }

                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }

            }
        }).start();

    }


    @Override
    public void onSensorChanged(final SensorEvent  event) {
        if (started) {
           

            //series.appendData(new DataPoint(date,event.values[record] ), true, 1000);
            //addEntry();

            // yourMethod();

            //series.appendData(new DataPoint(date,event.values[0] ), true, 10);




            //long timestamp = System.currentTimeMillis();
            //AccelData data = new AccelData(date, x, y, z);
            //sensorData.add(data);
            //AccelData data = new AccelData(timestamp, x, y, z);
            //sensorData.add(data);



        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);

                // save prev data if available
                started = true;
                Sensor accel = sensorManager
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, accel,
                        10000000);
                break;
            case R.id.btnStop:
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);

                started = false;
                sensorManager.unregisterListener(this);
                //layout.removeAllViews();
                //openChart();

                // show data in chart
                break;



            default:
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text= parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
        record = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    // add random data to graph

}
