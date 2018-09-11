package com.limoonsoft.core;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.limoonsoft.data.PersistenceManager;

import java.sql.Date;

@DatabaseTable(tableName = "RouteStudent")
public class RouteStudentModel implements PersistenceManager.Modal {
    @DatabaseField(id = true)
    private int Id;
    @DatabaseField
    private int RouteId;
    @DatabaseField
    private int StudentId;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private RouteModel Route;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private StudentModel Student;
    @DatabaseField
    private String Distance;
    @DatabaseField
    private boolean CallStatus;
    @DatabaseField
    private Date CallDate;
    @DatabaseField
    private boolean Status;

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

    public RouteModel getRoute() {
        return Route;
    }

    public void setRoute(RouteModel route) {
        Route = route;
    }

    public StudentModel getStudent() {
        return Student;
    }

    public void setStudent(StudentModel student) {
        Student = student;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public boolean isCallStatus() {
        return CallStatus;
    }

    public void setCallStatus(boolean callStatus) {
        CallStatus = callStatus;
    }

    public Date getCallDate() {
        return CallDate;
    }

    public void setCallDate(Date callDate) {
        CallDate = callDate;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }
}
