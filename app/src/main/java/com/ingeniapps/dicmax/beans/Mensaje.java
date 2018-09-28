package com.ingeniapps.dicmax.beans;

/**
 * Created by Ingenia Applications on 14/09/2018.
 */

public class Mensaje
{
    private String codPush;
    private String titMensaje;
    private String desMensaje;
    private String urlImagen;

    public String getCodPush() {
        return codPush;
    }

    public void setCodPush(String codPush) {
        this.codPush = codPush;
    }

    public String getTitMensaje() {
        return titMensaje;
    }

    public void setTitMensaje(String titMensaje) {
        this.titMensaje = titMensaje;
    }

    public String getDesMensaje() {
        return desMensaje;
    }

    public void setDesMensaje(String desMensaje) {
        this.desMensaje = desMensaje;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getFecEnvio() {
        return fecEnvio;
    }

    public void setFecEnvio(String fecEnvio) {
        this.fecEnvio = fecEnvio;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String fecEnvio;
    private String type;


}
