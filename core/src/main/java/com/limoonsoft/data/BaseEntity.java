package com.limoonsoft.data;

import android.content.Context;

public class BaseEntity<E extends PersistenceManager.Modal> extends PersistenceManager<E> {
    public BaseEntity(Context context, Class c) {
        super(context, c);
    }
}
