package com.limoonsoft.data;

import android.location.Location;
import android.location.LocationManager;

import java.util.Date;

/**
 * Created by Fatih on 03.03.2018.
 */

public class Position {

    public  Position(){

    }

    public Position(String deviceId, Location location,double battery){
        setDeviceId(deviceId);
        setTime(new Date(location.getTime()));
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
        setAltitude(location.getAltitude());
        setSpeed(location.getSpeed() * 1.943844);
        setCourse(location.getBearing());
        setBattery(battery);

        if (location.getProvider() != null && !location.getProvider().equals(LocationManager.GPS_PROVIDER)){
            setAccuracy(location.getAccuracy());
        }
    }

    private long id;
    private String deviceId;
    private Date time;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private double course;
    private double accuracy;
    private double battery;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getCourse() {
        return course;
    }

    public void setCourse(double course) {
        this.course = course;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getBattery() {
        return battery;
    }

    public void setBattery(double battery) {
        this.battery = battery;
    }
}
