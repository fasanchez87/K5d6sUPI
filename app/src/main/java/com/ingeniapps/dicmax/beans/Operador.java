package com.ingeniapps.dicmax.beans;

/**
 * Created by Ingenia Applications on 20/08/2018.
 */

public class Operador
{
    private String codOperador;
    private String nomOperador;

    public String getCodOperador() {
        return codOperador;
    }

    public void setCodOperador(String codOperador) {
        this.codOperador = codOperador;
    }

    public String getNomOperador() {
        return nomOperador;
    }

    public void setNomOperador(String nomOperador) {
        this.nomOperador = nomOperador;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String urlImagen;
    private String type;
}
