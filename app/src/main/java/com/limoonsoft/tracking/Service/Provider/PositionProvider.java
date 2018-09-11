package com.limoonsoft.tracking.Service.Provider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.limoonsoft.data.Position;

/**
 * Created by Fatih on 03.03.2018.
 */

public class PositionProvider {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20; //
    private static final long MIN_TIME_BW_UPDATES = 1000; //

    private final Context context;
    private final PositionListener listener;
    private final LocationManager locationManager;

    private String deviceId = "0001";

    public interface PositionListener {
        void onPositionUpdate(Position position);
    }

    public PositionProvider(Context context, PositionListener listener) {
        this.context = context;
        this.listener = listener;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Criteria criteria = new Criteria();
        //Use FINE or COARSE (or NO_REQUIREMENT) here
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.ACCURACY_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        locationManager.requestLocationUpdates(MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,criteria,locationListener,null);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
    }


    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null){
                Log.e("onLocationChanged","Geldi");
                listener.onPositionUpdate(new Position(deviceId, location, getBatteryLevel(context)));
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.e("onStatusChanged",s+" "+i);
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.e("onProviderEnabled",s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.e("onProviderDisabled",s);

        }
    };

    public static double getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent != null) {
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
            return (level * 100.0) / scale;
        }
        return 0;
    }

    public void stop(){
        locationManager.removeUpdates(locationListener);
    }
}
