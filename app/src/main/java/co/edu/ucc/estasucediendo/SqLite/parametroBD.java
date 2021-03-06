package co.edu.ucc.estasucediendo.SqLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wilmer on 27/02/2015.
 */


public class parametroBD {

    private funcionesBD funcionesBD;
    private SQLiteDatabase db;

    public parametroBD(Context context) {
        funcionesBD = new funcionesBD(context);
        db = funcionesBD.getWritableDatabase();
    }

    //insertar parametros

    //crear las tablas
    /*public static final String crear_tabla_dispositivos = "CREATE TABLE dispositivos (" +
            "imei TEXT NOT NULL ," +
            "tokengoogle TEXT NOT NULL ," +
            "idproyecto TEXT NOT NULL " +
            ");";*/

    public static final String crear_tabla_usuario = "CREATE TABLE usuario (" +
            "id  NOT NULL ," +
            "nombreusuario TEXT NOT NULL ," +
            "email TEXT NOT NULL ," +
            "celular TEXT NOT NULL ," +
            "PRIMARY KEY(id)" +
            ");";

    /*alter table ejmplo
    public static final String actualizar_gps = "ALTER TABLE gps ADD COLUMN ruta INTEGER; update gps set ruta = 0";*/

    //ContentValues
    private ContentValues generarContentValues_usuario(String nombreusuario, String email, String celular) {
        ContentValues valores = new ContentValues();
        valores.put("id", 1);
        valores.put("nombreusuario", nombreusuario);
        valores.put("email", email);
        valores.put("celular", celular);
        return valores;
    }

    //insert dispositivos
    public long insertar_usuario(String nombreusuario, String email, String celular) {
        return db.insert("usuario", null, generarContentValues_usuario(nombreusuario, email, celular));
    }

    /*public Cursor consultarDispositivo(String imei) {
        return db.rawQuery("SELECT * FROM dispositivos where imei=" + imei, null);
    }


    public Cursor consultarProyecto(String idproyecto, String imei) {
        Cursor cursor = db.rawQuery("SELECT * FROM dispositivos where idproyecto=" + idproyecto + " and imei=" + imei, null);
        return cursor;
    }*/


    public Cursor consultarUsuario() {
        Cursor cursor = db.rawQuery("SELECT * FROM usuario", null);
        return cursor;
    }

    /*public void EliminarDispositvo(String idproyecto) {
        int a = db.delete("dispositivos", "idproyecto=" + idproyecto, null);
    }*/

    //ContentValues
    /*private ContentValues generarContentValues_usuario(String id) {
        ContentValues valores = new ContentValues();
        valores.put("id", id);
        return valores;
    }*/

    /*public Long Reiniciar(String idusuario) {
        eliminartabla();
        return db.insert("usuario", null, generarContentValues_usuario(idusuario));
    }

    public void eliminartabla() {
        db.execSQL("delete from usuario");
    }

    public List<Long> consultarAlertasActivas() {
        Cursor cursor = db.rawQuery("SELECT distinct idproyecto FROM dispositivos", null);
        cursor.moveToFirst();
        List<Long> temp = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            Long aLong;
            aLong = Long.parseLong(cursor.getString(0));
            Log.d("SAT", "operacion" + aLong);
            temp.add(aLong);
            cursor.moveToNext();
        }
        return temp;
    }*/
}



