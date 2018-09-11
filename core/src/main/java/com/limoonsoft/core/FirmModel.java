package com.limoonsoft.core;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.limoonsoft.data.PersistenceManager;

import java.util.List;

@DatabaseTable(tableName = "Firm")
public class FirmModel implements PersistenceManager.Modal {
    @DatabaseField(id=true)
    private int Id;
    private List<RouteModel> RouteList;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public List<RouteModel> getRouteList() {
        return RouteList;
    }

    public void setRouteList(List<RouteModel> routeList) {
        RouteList = routeList;
    }
}
