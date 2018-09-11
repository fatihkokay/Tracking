package com.limoonsoft.core;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.limoonsoft.data.PersistenceManager;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "Student")
public class StudentModel implements PersistenceManager.Modal {

    public StudentModel() {
    }

    @DatabaseField(id = true)
    private int Id;
    @DatabaseField
    private int FirmId;
    @DatabaseField
    private String FirmName;
    @DatabaseField
    private String Name;
    @DatabaseField
    private String Surname;
    @DatabaseField
    private String Phone;
    @DatabaseField
    private String Address;
    @DatabaseField
    private double Distance;
    @DatabaseField
    private int ParentId;
    @DatabaseField
    private int Active;

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

    public String getFirmName() {
        return FirmName;
    }

    public void setFirmName(String firmName) {
        FirmName = firmName;
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


    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }


    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public int getParentId() {
        return ParentId;
    }

    public void setParentId(int parentId) {
        ParentId = parentId;
    }

    public int getActive() {
        return Active;
    }

    public void setActive(int active) {
        Active = active;
    }

    public String getNameSurname() {
        return Name +" "+Surname;
    }
}
