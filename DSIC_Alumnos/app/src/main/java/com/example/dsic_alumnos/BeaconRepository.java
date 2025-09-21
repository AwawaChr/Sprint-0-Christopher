package com.example.dsic_alumnos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BeaconRepository {
    private DBHelper dbHelper;

    public BeaconRepository(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * Inserta sincronamente (retorna id) — usar desde un hilo de fondo.
     */
    public long insertarCodigo(String codigo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_CODIGO, codigo);
        long id = db.insert(DBHelper.TABLE_BEACONS, null, values);
        db.close();
        return id;
    }
}
