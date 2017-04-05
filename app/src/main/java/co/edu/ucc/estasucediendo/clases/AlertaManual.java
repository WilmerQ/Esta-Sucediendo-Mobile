package co.edu.ucc.estasucediendo.clases;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;


/**
 * Created by wilme on 9/02/2017.
 */

public class AlertaManual implements Serializable {

    public AlertaManual() {
        Calendar date;
        date = Calendar.getInstance(TimeZone.getTimeZone("America/Bogota"));
        this.hora = date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + ":" + date.get(Calendar.SECOND);
    }

    private String nombrePersona;
    private String celular;
    private String correo;
    private String hora;
    private String tipoEvento;
    private Double latitud;
    private Double longitud;
    private String imagen;
    private byte[] video;
    private byte[] audio;

    public String getNombrePersona() {
        return nombrePersona;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public byte[] getVideo() {
        return video;
    }

    public void setVideo(byte[] video) {
        this.video = video;
    }

    public byte[] getAudio() {
        return audio;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }
}
