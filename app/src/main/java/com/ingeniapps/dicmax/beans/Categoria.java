package com.ingeniapps.dicmax.beans;

/**
 * Created by Ingenia Applications on 24/09/2017.
 */

public class Categoria
{

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public String getCodCategoria() {
        return codCategoria;
    }

    public void setCodCategoria(String codCategoria) {
        this.codCategoria = codCategoria;
    }

    public String getNomCategoria() {
        return nomCategoria;
    }

    public void setNomCategoria(String nomCategoria) {
        this.nomCategoria = nomCategoria;
    }

    String codCategoria, nomCategoria;

    public Categoria()
    {

    }

    public Categoria(String codCategoria, String nomCategoria)
    {
        this.codCategoria=codCategoria;
        this.nomCategoria=nomCategoria;
    }
}
