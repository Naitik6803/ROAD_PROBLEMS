package com.example.gpstracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    Button b_enable,b_lock;
    Context mContext;
    TextView lat,lon,dist,speed,add;

    FusedLocationProviderClient fusedLocationProviderClient;
    int count=0;

    static final int RESULT_ENABLE=1;
    DevicePolicyManager devicePolicyManager;
    ComponentName componentName;
    int flag=0;
    private static final DecimalFormat df = new DecimalFormat("0.00");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        b_enable=(Button)findViewById(R.id.b_enable);

//        foreground service

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this,Track.class));
        }
        Log.i("start","started service");

        devicePolicyManager=(DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName=new ComponentName(MainActivity.this,Controller.class);

        boolean active=devicePolicyManager.isAdminActive(componentName);
        if(active){
            b_enable.setText("Disable Driving Mode");
        }else{
            b_enable.setText("Enable Driving Mode");
        }



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        lat=(TextView)findViewById(R.id.lat);
        lon=(TextView)findViewById(R.id.lon);
        dist=(TextView)findViewById(R.id.distance);
        speed=(TextView)findViewById(R.id.speed);
        add=(TextView)findViewById(R.id.add);




        b_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean active=devicePolicyManager.isAdminActive(componentName);
                if(active){
                    devicePolicyManager.removeActiveAdmin(componentName);
                    b_enable.setText("Enable Driving Mode");
                }else{
                    Intent intent=new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"You should enable the app");
                    startActivity(intent);
                    b_enable.setText("Disable Driving Mode");
                }
            }
        });




        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000,
                10, locationListenerGPS);
        isLocationEnabled();


        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("distance");

        registerReceiver(broadcastReciever,intentFilter);

    }

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
//            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
//            calculateDanger(latitude,longitude);
            lat.setText("The lattitude is :"+latitude);
            lon.setText("The longitude is :"+longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    protected void onResume(){
        super.onResume();
        isLocationEnabled();
    }

    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
        else{
            getLocation();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                        Toast.makeText(getApplicationContext(),"The latitude is " + addresses.get(0).getLatitude(),Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(),"The Longitude is " + addresses.get(0).getLongitude(),Toast.LENGTH_SHORT).show();
                        double myLat=addresses.get(0).getLatitude();
                        double myLon=addresses.get(0).getLongitude();
                        String s =addresses.get(0).getAddressLine(0) + "\n";
                        lat.setText("The lattitude is :"+myLat);
                        lon.setText("The longitude is :"+myLon);
                        add.setText("Your current location is :\n"+s);
//                        calculateDanger(myLat,myLon);

                    } catch (IOException e) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case RESULT_ENABLE:
                if(resultCode== Activity.RESULT_OK ){
                    b_enable.setText("Dissable");
                    b_lock.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Disabled", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
                return;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service:activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(Service.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    private BroadcastReceiver broadcastReciever=new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                double closest = intent.getDoubleExtra("closest", 0);
                double mySpeed = intent.getDoubleExtra("speed", 0);
                speed.setText("Your speed is "+(int)mySpeed);
                if(closest<1){
                    closest*=1000;
                    dist.setText("Closest accident prone area is at a distance: " + df.format(closest)+ " metres");
                }else{
                    dist.setText("Closest accident prone area is at a distance: " + df.format(closest)+ " km");
                }


                if (closest > 0.5) {
                    count = 0;
                }

                if (closest <= 0.5) {
                    if(count!=1 && mySpeed>35){
                        Toast.makeText(getApplicationContext(),"You are advised to reduce your speed",Toast.LENGTH_SHORT).show();
                        MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.reduce);
                        mp.start();
                    }
                    if (count == 1) {
                        MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.accident);
                        mp.start();
                    }
                    boolean active = devicePolicyManager.isAdminActive(componentName);
                    if (active && count==0) {
                        Toast.makeText(getApplicationContext(), "The screen will be locked in a few seconds, because you are in a accident prone zone. To avoid disable driving mode", Toast.LENGTH_LONG).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                boolean active = devicePolicyManager.isAdminActive(componentName);
                                if (active) {
                                    devicePolicyManager.lockNow();
                                }
                            }
                        }, 10000);
                    }
                    Log.d("close","count value is "+count);
                    count++;
                    if (count == 30) {
                        count = 0;
                    }
                }

            }

        }
    };
};