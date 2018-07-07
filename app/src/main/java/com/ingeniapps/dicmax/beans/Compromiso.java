package com.ingeniapps.dicmax.beans;

/**
 * Created by Ingenia Applications on 7/07/2018.
 */

public class Compromiso
{

    private String numCompromiso;

    public String getNumCompromiso() {
        return numCompromiso;
    }

    public void setNumCompromiso(String numCompromiso) {
        this.numCompromiso = numCompromiso;
    }

    public String getFecCompro() {
        return fecCompro;
    }

    public void setFecCompro(String fecCompro) {
        this.fecCompro = fecCompro;
    }

    public String getValPendiente() {
        return valPendiente;
    }

    public void setValPendiente(String valPendiente) {
        this.valPendiente = valPendiente;
    }

    public String getNumDias() {
        return numDias;
    }

    public void setNumDias(String numDias) {
        this.numDias = numDias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String fecCompro;
    private String valPendiente;
    private String numDias;
    private String type;


}
