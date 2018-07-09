package com.ingeniapps.dicmax.beans;

/**
 * Created by Ingenia Applications on 9/07/2018.
 */

public class Pago
{

    private String tipTransaccion;
    private String fecAprobacion;

    public String getTipTransaccion() {
        return tipTransaccion;
    }

    public void setTipTransaccion(String tipTransaccion) {
        this.tipTransaccion = tipTransaccion;
    }

    public String getFecAprobacion() {
        return fecAprobacion;
    }

    public void setFecAprobacion(String fecAprobacion) {
        this.fecAprobacion = fecAprobacion;
    }

    public String getNomEmpresa() {
        return nomEmpresa;
    }

    public void setNomEmpresa(String nomEmpresa) {
        this.nomEmpresa = nomEmpresa;
    }

    public String getValTransaccion() {
        return valTransaccion;
    }

    public void setValTransaccion(String valTransaccion) {
        this.valTransaccion = valTransaccion;
    }

    public String getCodEstado() {
        return codEstado;
    }

    public void setCodEstado(String codEstado) {
        this.codEstado = codEstado;
    }

    public String getNomEstado() {
        return nomEstado;
    }

    public void setNomEstado(String nomEstado) {
        this.nomEstado = nomEstado;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String nomEmpresa;
    private String valTransaccion;
    private String codEstado;
    private String nomEstado;
    private String type;


}
