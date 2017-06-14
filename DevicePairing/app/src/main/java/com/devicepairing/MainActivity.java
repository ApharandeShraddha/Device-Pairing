package com.devicepairing;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener {


    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    LocationTracker gps;

    // UI
    Button pairBtn;
    Button acceptBtn;
    Button unPairBtn;
    Button closeBtn;
    TextView pairStatus;


    Boolean pairPressed = false;
    long counter=1;
    Boolean acceptPressed = false;
    double pairLat;
    double pairLong;
    double acceptLat;
    double acceptLong;

    Boolean workingDevice1 = false;
    Boolean workingDevice2 = false ;
    boolean unpairClicked1=false;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase.removeValue();

        mDatabase.child("isPaired").setValue("No");
        mDatabase.child("unPaireClicked").setValue("No");

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(MainActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        pairBtn = (Button)findViewById(R.id.pairBtn);
        acceptBtn = (Button)findViewById(R.id.acceptbtn);
        unPairBtn = (Button)findViewById(R.id.unPairBtn);
        closeBtn = (Button)findViewById(R.id.closeBtn);

        pairStatus= (TextView)findViewById(R.id.pairStatus);

        pairBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                workingDevice1 = true;
                workingDevice2 = false;
                pairPressed = true;
                acceptPressed=false;
                senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                senSensorManager.registerListener(MainActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);



                gps = new LocationTracker(MainActivity.this,"Pair",mDatabase);

                if(gps.canGetLocation()){

                    pairLat = gps.getLatitude();
                    pairLong = gps.getLongitude();

                    //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    long currentDateMS = new Date().getTime();
                    int longTime = (int) (currentDateMS / 1000);
                    mDatabase.child("pairLat").setValue(pairLat);
                    mDatabase.child("pairLong").setValue(pairLong);
                    //pairLoc.setText("Pair Location: Lattitude="+pairLat+" Longitude="+pairLong);
                }else{

                    gps.showSettingsAlert();
                }

                DatabaseReference childref = mDatabase.child("isPaired");
                childref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String isPaired = dataSnapshot.getValue(String.class);

                        if(isPaired.equals("Yes"))
                            pairStatus.setText("Paired");


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                workingDevice2 = true;
                workingDevice1 = false;
                acceptPressed = true;
                pairPressed = false;
                senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                senSensorManager.registerListener(MainActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


                gps = new LocationTracker(MainActivity.this,"Accept",mDatabase);

                if(gps.canGetLocation()){

                    acceptLat= gps.getLatitude();
                    acceptLong = gps.getLongitude();


                    mDatabase.child("acceptLat").setValue(acceptLat);
                    mDatabase.child("acceptLong").setValue(acceptLong);

                }else{

                    gps.showSettingsAlert();
                }





                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String unpairClicked = String.valueOf(dataSnapshot.child("unPaireClicked").getValue());
                        String isPaired =String.valueOf(dataSnapshot.child("isPaired").getValue());

                        String pairLatDb = String.valueOf(dataSnapshot.child("pairLat").getValue());
                        String pairLongDb = String.valueOf(dataSnapshot.child("pairLong").getValue());
                        String acceptLatDb = String.valueOf(dataSnapshot.child("acceptLat").getValue());
                        String acceptLongDb = String.valueOf(dataSnapshot.child("acceptLong").getValue());

                        String pairXDb = String.valueOf(dataSnapshot.child("pairX").getValue());
                        String pairYDb = String.valueOf(dataSnapshot.child("pairY").getValue());
                        String pairZDb = String.valueOf(dataSnapshot.child("pairZ").getValue());
                        String acceptXDb = String.valueOf(dataSnapshot.child("acceptX").getValue());
                        String acceptYDb = String.valueOf(dataSnapshot.child("acceptY").getValue());
                        String acceptZDb = String.valueOf(dataSnapshot.child("acceptZ").getValue());

                        if(!"".equals(pairLatDb) &&
                                !"".equals(pairLongDb) &&
                                !"".equals(acceptLatDb) &&
                                !"".equals(acceptLongDb) &&
                                !"".equals(pairXDb) &&
                                !"".equals(pairYDb) &&
                                !"".equals(pairZDb) &&
                                !"".equals(acceptXDb) &&
                                !"".equals(acceptYDb) &&
                                !"".equals(acceptZDb)
                                ) {

                            double xDiff = Double.valueOf(pairXDb) - Double.valueOf(acceptXDb);
                            double yDiff = Double.valueOf(pairYDb) - Double.valueOf(acceptYDb);
                            double zDiff = Double.valueOf(pairZDb) - Double.valueOf(acceptZDb);

                            //details.setText("Pair Details:\nLat:"+pairLatDb+", Long:"+pairLongDb+"\nx:"+pairXDb+",y:"+pairYDb+",z:"+pairZDb+"\nAccept Details:\nLat:"+acceptLatDb+", Long:"+acceptLongDb+"\nx:"+acceptXDb+",y:"+acceptYDb+",z:"+acceptZDb+"\nDiffx"+xDiff+",Diffy"+yDiff+",Diffz"+zDiff);


                            if ( pairLatDb.equals(acceptLatDb) && pairLongDb.equals(acceptLongDb) && xDiff < 20 && yDiff < 20 && zDiff < 20) {
                                mDatabase.child("isPaired").setValue("Yes");
                            }


                            if(isPaired.equals("Yes"))
                                pairStatus.setText("Paired");


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });



            }
        });

        unPairBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                pairStatus.setText("Un-Paired");



            }

        });

        closeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //mDatabase.removeValue();
                finish();
                System.exit(0);
            }

        });


    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        counter++;
        if (counter==5) {

            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];


                if (workingDevice1) {
                    mDatabase.child("pairX").setValue(x);
                    mDatabase.child("pairY").setValue(y);
                    mDatabase.child("pairZ").setValue(z);
                } else if (workingDevice2) {
                    mDatabase.child("acceptX").setValue(x);
                    mDatabase.child("acceptY").setValue(y);
                    mDatabase.child("acceptZ").setValue(z);
                }

            }

            counter=0;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }


}
