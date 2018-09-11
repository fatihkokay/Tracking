package com.limoonsoft.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.limoonsoft.core.DriverModel;
import com.limoonsoft.core.FirmModel;
import com.limoonsoft.core.MovementModel;
import com.limoonsoft.core.RouteLineModel;
import com.limoonsoft.core.RouteModel;
import com.limoonsoft.core.RouteMovementModel;
import com.limoonsoft.core.RouteStudentModel;
import com.limoonsoft.core.StudentModel;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, DriverModel.class);
            TableUtils.createTable(connectionSource, FirmModel.class);
            TableUtils.createTable(connectionSource, MovementModel.class);
            TableUtils.createTable(connectionSource, RouteLineModel.class);
            TableUtils.createTable(connectionSource, RouteModel.class);
            TableUtils.createTable(connectionSource, RouteMovementModel.class);
            TableUtils.createTable(connectionSource, RouteStudentModel.class);
            TableUtils.createTable(connectionSource, StudentModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, DriverModel.class,true);
            TableUtils.dropTable(connectionSource, FirmModel.class,true);
            TableUtils.dropTable(connectionSource, MovementModel.class,true);
            TableUtils.dropTable(connectionSource, RouteLineModel.class,true);
            TableUtils.dropTable(connectionSource, RouteModel.class,true);
            TableUtils.dropTable(connectionSource, RouteMovementModel.class,true);
            TableUtils.dropTable(connectionSource, RouteStudentModel.class,true);
            TableUtils.dropTable(connectionSource, StudentModel.class,true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
