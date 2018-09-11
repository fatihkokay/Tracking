package com.limoonsoft.core;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.limoonsoft.data.PersistenceManager;

import java.sql.Date;

@DatabaseTable(tableName = "Movement")
public class MovementModel implements PersistenceManager.Modal {
    @DatabaseField(id=true)
    private int Id;
    @DatabaseField
    private int RouteId;
    @DatabaseField
    private int StudentId;
    @DatabaseField
    private Date Date;
    @DatabaseField
    private boolean InCarNotification;
    @DatabaseField
    private Date InCarNotificationDate;
    @DatabaseField
    private boolean InCar;
    @DatabaseField
    private Date InCarDate;
    @DatabaseField
    private boolean InCarSchoolNotification;
    @DatabaseField
    private Date InCarSchoolNotificationDate;
    @DatabaseField
    private boolean InCarSchool;
    @DatabaseField
    private Date InCarSchoolDate;
    @DatabaseField
    private boolean OutCarNotification;
    @DatabaseField
    private Date OutCarNotificationDate;
    @DatabaseField
    private boolean OutCar;
    @DatabaseField
    private Date OutCarDate;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getRouteId() {
        return RouteId;
    }

    public void setRouteId(int routeId) {
        RouteId = routeId;
    }

    public int getStudentId() {
        return StudentId;
    }

    public void setStudentId(int studentId) {
        StudentId = studentId;
    }

    public java.sql.Date getDate() {
        return Date;
    }

    public void setDate(java.sql.Date date) {
        Date = date;
    }

    public boolean isInCarNotification() {
        return InCarNotification;
    }

    public void setInCarNotification(boolean inCarNotification) {
        InCarNotification = inCarNotification;
    }

    public java.sql.Date getInCarNotificationDate() {
        return InCarNotificationDate;
    }

    public void setInCarNotificationDate(java.sql.Date inCarNotificationDate) {
        InCarNotificationDate = inCarNotificationDate;
    }

    public boolean isInCar() {
        return InCar;
    }

    public void setInCar(boolean inCar) {
        InCar = inCar;
    }

    public java.sql.Date getInCarDate() {
        return InCarDate;
    }

    public void setInCarDate(java.sql.Date inCarDate) {
        InCarDate = inCarDate;
    }

    public boolean isInCarSchoolNotification() {
        return InCarSchoolNotification;
    }

    public void setInCarSchoolNotification(boolean inCarSchoolNotification) {
        InCarSchoolNotification = inCarSchoolNotification;
    }

    public java.sql.Date getInCarSchoolNotificationDate() {
        return InCarSchoolNotificationDate;
    }

    public void setInCarSchoolNotificationDate(java.sql.Date inCarSchoolNotificationDate) {
        InCarSchoolNotificationDate = inCarSchoolNotificationDate;
    }

    public boolean isInCarSchool() {
        return InCarSchool;
    }

    public void setInCarSchool(boolean inCarSchool) {
        InCarSchool = inCarSchool;
    }

    public java.sql.Date getInCarSchoolDate() {
        return InCarSchoolDate;
    }

    public void setInCarSchoolDate(java.sql.Date inCarSchoolDate) {
        InCarSchoolDate = inCarSchoolDate;
    }

    public boolean isOutCarNotification() {
        return OutCarNotification;
    }

    public void setOutCarNotification(boolean outCarNotification) {
        OutCarNotification = outCarNotification;
    }

    public java.sql.Date getOutCarNotificationDate() {
        return OutCarNotificationDate;
    }

    public void setOutCarNotificationDate(java.sql.Date outCarNotificationDate) {
        OutCarNotificationDate = outCarNotificationDate;
    }

    public boolean isOutCar() {
        return OutCar;
    }

    public void setOutCar(boolean outCar) {
        OutCar = outCar;
    }

    public java.sql.Date getOutCarDate() {
        return OutCarDate;
    }

    public void setOutCarDate(java.sql.Date outCarDate) {
        OutCarDate = outCarDate;
    }
}
