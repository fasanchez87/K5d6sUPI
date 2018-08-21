package com.ingeniapps.dicmax.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.adapter.CategoriaAdapter;
import com.ingeniapps.dicmax.adapter.CiudadadesAdapter;
import com.ingeniapps.dicmax.adapter.EmpresasAdapter;
import com.ingeniapps.dicmax.adapter.OperadorAdapter;
import com.ingeniapps.dicmax.beans.Categoria;
import com.ingeniapps.dicmax.beans.Ciudad;
import com.ingeniapps.dicmax.beans.Empresa;
import com.ingeniapps.dicmax.beans.Operador;
import com.ingeniapps.dicmax.fragment.Categorias;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.util.SimpleDividerItemDecoration;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OperadoresRecargas extends AppCompatActivity
{

    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Operador> listadoOperadores;
    public com.ingeniapps.dicmax.vars.vars vars;
    private RecyclerView recycler_view_operadores;
    private OperadorAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    LinearLayout linearHabilitarOperadores;
    RelativeLayout layoutEspera;
    RelativeLayout layoutMacroEsperaOperadores;
    ImageView not_found_ciudades;
    private int pagina;
    Context context;
    private boolean solicitando=false;
    //VERSION DEL APP INSTALADA
    private String versionActualApp;
    private boolean invitado=false;
    private boolean isUpdateApp=false;
    private Typeface copperplateGothicLight;

    private ProgressDialog progressDialog;

    CardView cardViewCategorias;

    EditText editTextBusquedaCiudades;
    TextView editTextNumCiudades;
    private String idCiudad;
    private String nomCiudad;

    DividerItemDecoration mDividerItemDecoration;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operadores_recargas);


        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras == null)
            {
                idCiudad = null;
                nomCiudad = null;
                invitado = false;
                isUpdateApp = false;
            }
            else
            {
                idCiudad = extras.getString("idCiudad");
                nomCiudad = extras.getString("nomCiudad");
                invitado = extras.getBoolean("invitado");
                isUpdateApp = extras.getBoolean("isUpdateApp");
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //this line shows back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        sharedPreferences=new gestionSharedPreferences(this);
        listadoOperadores=new ArrayList<Operador>();
        vars=new vars();
        context = this;
        pagina=0;

        copperplateGothicLight = Typeface.createFromAsset(getAssets(), "fonts/AvenirLTStd-Light.ttf");


        not_found_ciudades=(ImageView)findViewById(R.id.not_found_empresas);
        //not_found_noticias.setVisibility(View.VISIBLE);
        layoutEspera=(RelativeLayout)findViewById(R.id.layoutEsperaCiudades);
        //cardViewCategorias=(CardView) getActivity().findViewById(R.id.cardViewCategoria);

        layoutMacroEsperaOperadores=(RelativeLayout)findViewById(R.id.layoutMacroEsperaOperadores);
        linearHabilitarOperadores=(LinearLayout)findViewById(R.id.linearHabilitarOperadores);
        recycler_view_operadores=(RecyclerView) findViewById(R.id.recycler_view_operadores);
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new OperadorAdapter(this,listadoOperadores,new OperadorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Operador operador)
            {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("codOperador",operador.getCodOperador());
                returnIntent.putExtra("nomOperador",operador.getNomOperador());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        recycler_view_operadores.addItemDecoration(new SimpleDividerItemDecoration(this));
        recycler_view_operadores.setHasFixedSize(true);
        recycler_view_operadores.setLayoutManager(mLayoutManager);
        recycler_view_operadores.setItemAnimator(new DefaultItemAnimator());
        recycler_view_operadores.setAdapter(mAdapter);

       /* ImageView buttonBuscar = (ImageView) findViewById(R.id.ivSearch);
        buttonBuscar.setClickable(true);
        buttonBuscar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                progressDialog.show();
                progressDialog.setCancelable(false);

                *//*if(!TextUtils.isEmpty(editTextBusqueda.getText()))
                {*//*
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //OCULTAMOS TECLADO
                imm.hideSoftInputFromWindow(editTextBusquedaCiudades.getWindowToken(), 0);
                WebServiceGetCiudades(editTextBusquedaCiudades.getText().toString(),null);
               *//* }*//*
               *//* else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
                    builder
                            .setTitle("Dicmax")
                            .setMessage("Por favor, ingrese un criterio de busqueda")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {

                                }
                            }).show();
                }*//*
            }
        });*/

        WebServiceGetOperadores();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ControllerSingleton.getInstance().cancelPendingReq("getCiudadesFind");
        ControllerSingleton.getInstance().cancelPendingReq("find");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {

                finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }



    private void WebServiceGetOperadores()
    {
        String _urlWebService = vars.ipServer.concat("/ws/getOpRec");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            if(response.getBoolean("status"))
                            {
                                listadoOperadores.clear();

                                JSONArray listaOperadores = response.getJSONArray("operadores");

                                for (int i = 0; i < listaOperadores.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) listaOperadores.get(i);
                                    Operador operador = new Operador();
                                    operador.setType(jsonObject.getString("type"));
                                    operador.setCodOperador(jsonObject.getString("codOperador"));
                                    operador.setNomOperador(jsonObject.getString("nomOperador"));
                                    operador.setUrlImagen(jsonObject.getString("urlImagen"));
                                    listadoOperadores.add(operador);
                                }

                                layoutMacroEsperaOperadores.setVisibility(View.GONE);
                                linearHabilitarOperadores.setVisibility(View.VISIBLE);
                            }

                            else
                            {

                                Snackbar.make(findViewById(android.R.id.content),
                                        "No se encontraron operadores", Snackbar.LENGTH_LONG).show();

                            }
                        }
                        catch (JSONException e)
                        {
                            layoutMacroEsperaOperadores.setVisibility(View.VISIBLE);
                            linearHabilitarOperadores.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(OperadoresRecargas.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage(e.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();

                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (error instanceof TimeoutError)
                        {
                            layoutMacroEsperaOperadores.setVisibility(View.VISIBLE);
                            linearHabilitarOperadores.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(OperadoresRecargas.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof NoConnectionError)
                        {
                            layoutMacroEsperaOperadores.setVisibility(View.VISIBLE);
                            linearHabilitarOperadores.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(OperadoresRecargas.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Por favor, conectese a la red.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof AuthFailureError)
                        {
                            layoutMacroEsperaOperadores.setVisibility(View.VISIBLE);
                            linearHabilitarOperadores.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(OperadoresRecargas.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de autentificación en la red, favor contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof ServerError)
                        {
                            layoutMacroEsperaOperadores.setVisibility(View.VISIBLE);
                            linearHabilitarOperadores.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(OperadoresRecargas.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error server, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof NetworkError)
                        {
                            layoutMacroEsperaOperadores.setVisibility(View.VISIBLE);
                            linearHabilitarOperadores.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(OperadoresRecargas.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de red, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof ParseError)
                        {
                            layoutMacroEsperaOperadores.setVisibility(View.VISIBLE);
                            linearHabilitarOperadores.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(OperadoresRecargas.this,R.style.AlertDialogTheme));
                            builder
                                    .setMessage("Error de conversión Parser, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "getEmpresasFind");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}


