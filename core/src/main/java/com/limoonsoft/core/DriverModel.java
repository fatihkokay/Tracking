package com.limoonsoft.core;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.limoonsoft.data.PersistenceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@DatabaseTable(tableName = "Driver")
public class DriverModel implements PersistenceManager.Modal {
    public static final String FIELD_NAME_ROUTE_LIST = "RouteList";

    public  DriverModel(){

    }

    public  DriverModel(String Username,String Password) {
        setUsername(Username);
        setPassword(Password);
    }

    @DatabaseField(id = true)
    private int Id;
    @DatabaseField
    private int FirmId;
    @DatabaseField
    private String Code;
    @DatabaseField
    private String Name;
    @DatabaseField
    private String Surname;
    @DatabaseField
    private int Active;
    @DatabaseField
    private String Username;
    @DatabaseField
    private String Password;
    @DatabaseField
    private String DevinceId;
    @DatabaseField
    private String FirmName;
    @ForeignCollectionField(columnName = FIELD_NAME_ROUTE_LIST, eager = true)
    private Collection<RouteModel> RouteList;

    public static String getFieldNameRouteList() {
        return FIELD_NAME_ROUTE_LIST;
    }

    @Override
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

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public int getActive() {
        return Active;
    }

    public void setActive(int active) {
        Active = active;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDevinceId() {
        return DevinceId;
    }

    public void setDevinceId(String devinceId) {
        DevinceId = devinceId;
    }

    public String getFirmName() {
        return FirmName;
    }

    public void setFirmName(String firmName) {
        FirmName = firmName;
    }

    public List<RouteModel> getRouteList() {
        return new ArrayList<>(RouteList);
    }

    public void setRouteList(List<RouteModel> routeList) {
        RouteList = routeList;
    }

    public String getNameSurname(){
        return Name +" "+ Surname;
    }
}
