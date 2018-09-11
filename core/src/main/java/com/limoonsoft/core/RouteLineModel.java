package com.limoonsoft.core;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.limoonsoft.data.PersistenceManager;

import java.sql.Date;

/**
 * Created by Fatih on 03.03.2018.
 */

@DatabaseTable(tableName = "RouteLine")
public class RouteLineModel implements PersistenceManager.Modal {
    @DatabaseField(id=true)
    private int Id ;
    @DatabaseField
    private int RouteId ;
    @DatabaseField
    private int StudentId;
    @DatabaseField
    private int LineType;
    @DatabaseField
    private double Longitude ;
    @DatabaseField
    private double Latitute ;
    @DatabaseField
    private boolean Status;
    @DatabaseField
    private Date CreateDateTime;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private StudentModel Student;

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

    public int getLineType() {
        return LineType;
    }

    public void setLineType(int lineType) {
        LineType = lineType;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitute() {
        return Latitute;
    }

    public void setLatitute(double latitute) {
        Latitute = latitute;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public Date getCreateDateTime() {
        return CreateDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        CreateDateTime = createDateTime;
    }

    public StudentModel getStudent() {
        return Student;
    }

    public void setStudent(StudentModel student) {
        Student = student;
    }
}
