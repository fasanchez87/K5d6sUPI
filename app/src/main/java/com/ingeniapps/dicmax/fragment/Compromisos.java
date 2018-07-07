package com.ingeniapps.dicmax.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.beans.Categoria;
import com.ingeniapps.dicmax.beans.Compromiso;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.vars.vars;

import java.util.ArrayList;

public class Compromisos extends Fragment
{

    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Compromiso> listadoCompromisos;
    private vars vars;

    private RecyclerView recycler_view_compromisos;
    private CategoriaAdapter mAdapter;
  /*  LinearLayoutManager mLayoutManager;
    LinearLayout linearHabilitarCompras;
    RelativeLayout layoutEspera;
    RelativeLayout layoutMacroEsperaCategorias;
    ImageView not_found_categorias;*/
    private int pagina;
    Context context;
    private boolean solicitando=false;
    //VERSION DEL APP INSTALADA
    private String versionActualApp;
    private Typeface copperplateGothicLight;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPreferences=new gestionSharedPreferences(getActivity().getApplicationContext());
        listadoCompromisos=new ArrayList<Compromiso>();
        vars=new vars();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compromisos, container, false);
    }

}
