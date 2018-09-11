package com.limoonsoft.data;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class PersistenceManager<E extends PersistenceManager.Modal> {
    protected DatabaseHelper helper;
    protected Dao dao;

    public PersistenceManager(Context context, Class c) {
        helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);

        try {
            dao = DaoManager.createDao(helper.getConnectionSource(), c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public final boolean create(E data) {
        if (exists(data.getId())) {
            Log.e(PersistenceManager.class.getName(), "An entry with the same id already exists.");
            return false;
        }

        try {

            dao.create(data);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public final boolean createAll(List<E> data) {
        try {
            dao.create(data);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public final E read(Integer Id) {
        try {
            return (E) dao.queryForId(Id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public final E readFirstInColumnName(String ColumnName, Object Value) {
        try {
            return (E) dao.queryBuilder().where().in(ColumnName, Value).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final List<E> readFirstInColumnNameAll(String ColumnName, Object Value) {
        try {
            return (List<E>) dao.queryBuilder().where().in(ColumnName, Value).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final List<E> readFirstInColumnNameAllOrderBy(String ColumnName, Object Value, String OrderByColumn, boolean Ascending) {
        try {
            QueryBuilder queryBuilder = dao.queryBuilder();
            queryBuilder.where().in(ColumnName, Value);
            queryBuilder.orderBy(OrderByColumn, Ascending);

            return (List<E>) queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final List<E> readAll() {
        try {
            return (List<E>) dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final boolean update(E data) {
        try {
            dao.update(data);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public final boolean delete(Integer Id) {
        try {
            dao.deleteById(Id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public final boolean deleteAll(Class c) {
        try {
            TableUtils.clearTable(helper.getConnectionSource(), c);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public final boolean exists(int Id) {
        try {
            if (Id == 0) {
                return false;
            } else {
                return dao.queryForId(Id) != null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface Modal {
        public int getId();
    }
}