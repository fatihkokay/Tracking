package com.limoonsoft.core;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.limoonsoft.data.PersistenceManager;

@DatabaseTable(tableName = "RouteMovement")
public class RouteMovementModel implements PersistenceManager.Modal {
    @DatabaseField(id = true)
    private int Id;
    @DatabaseField
    private int RouteId;
    @DatabaseField
    private int Status;
    @DatabaseField
    private int SchoolExitStatus;

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

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getSchoolExitStatus() {
        return SchoolExitStatus;
    }

    public void setSchoolExitStatus(int schoolExitStatus) {
        SchoolExitStatus = schoolExitStatus;
    }
}
