package com.limoonsoft.core;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.limoonsoft.data.PersistenceManager;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "Route")
public class RouteModel  implements PersistenceManager.Modal{
    public static final String FIELD_NAME_FIRM = "Firm";
    public static final String FIELD_NAME_DRIVER = "Driver";

    public  RouteModel(){
        RouteLineList = new ArrayList<>();
        RouteStudentList = new ArrayList<>();
        MovementList = new ArrayList<>();
    }

    @DatabaseField(id = true)
    private int Id;
    @DatabaseField
    private int FirmId;
    @DatabaseField
    private int DriverId;
    @DatabaseField
    private int RouteType;
    @DatabaseField
    private String Name;
    @DatabaseField
    private String StartTime;
    @DatabaseField
    private String SchoolExitTime;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = FIELD_NAME_FIRM)
    private FirmModel Firm;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = FIELD_NAME_DRIVER)
    private DriverModel Driver;
    private RouteMovementModel RouteMovement;
    private List<RouteStudentModel> RouteStudentList;
    private List<RouteLineModel> RouteLineList;
    private List<MovementModel> MovementList;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getFirmId() {
        return FirmId;
    }

    public void setFirmId(int firmId) {
        FirmId = firmId;
    }

    public int getDriverId() {
        return DriverId;
    }

    public void setDriverId(int driverId) {
        DriverId = driverId;
    }

    public int getRouteType() {
        return RouteType;
    }

    public void setRouteType(int routeType) {
        RouteType = routeType;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getSchoolExitTime() {
        return SchoolExitTime;
    }

    public void setSchoolExitTime(String schoolExitTime) {
        SchoolExitTime = schoolExitTime;
    }

    public FirmModel getFirm() {
        return Firm;
    }

    public void setFirm(FirmModel firm) {
        Firm = firm;
    }

    public DriverModel getDriver() {
        return Driver;
    }

    public void setDriver(DriverModel driver) {
        Driver = driver;
    }

    public RouteMovementModel getRouteMovement() {
        return RouteMovement;
    }

    public void setRouteMovement(RouteMovementModel routeMovement) {
        RouteMovement = routeMovement;
    }

    public List<RouteStudentModel> getRouteStudentList() {
        return RouteStudentList;
    }

    public void setRouteStudentList(List<RouteStudentModel> routeStudentList) {
        RouteStudentList = routeStudentList;
    }

    public List<RouteLineModel> getRouteLineList() {
        return RouteLineList;
    }

    public void setRouteLineList(List<RouteLineModel> routeLineList) {
        RouteLineList = routeLineList;
    }

    public List<MovementModel> getMovementList() {
        return MovementList;
    }

    public void setMovementList(List<MovementModel> movementList) {
        MovementList = movementList;
    }
}
