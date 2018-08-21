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
import com.ingeniapps.dicmax.beans.Categoria;
import com.ingeniapps.dicmax.beans.Ciudad;
import com.ingeniapps.dicmax.beans.Empresa;
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

public class BuscarCiudad extends AppCompatActivity
{

    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Ciudad> listadoCiudades;
    public com.ingeniapps.dicmax.vars.vars vars;
    private RecyclerView recycler_view_ciudades;
    private CiudadadesAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    LinearLayout linearHabilitarCiudades;
    RelativeLayout layoutEspera;
    RelativeLayout layoutMacroEsperaCiudades;
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
        setContentView(R.layout.activity_buscar_ciudad);

        invitado=false;
        isUpdateApp=false;

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

        progressDialog = new ProgressDialog(new android.support.v7.view.ContextThemeWrapper(BuscarCiudad.this,R.style.AppCompatAlertDialogStyle));
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Un momento...");

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
                if(invitado)
                {
                    Intent i=new Intent(BuscarCiudad.this, Prelogin.class);
                    startActivity(i);
                    finish();
                }
                else
                if(isUpdateApp)
                {
                    Intent i=new Intent(BuscarCiudad.this, Inicio.class);
                    startActivity(i);
                    finish();
                }
                else
                if(!invitado)
                {
                    finish();
                }
            }
        });

        sharedPreferences=new gestionSharedPreferences(this);
        listadoCiudades=new ArrayList<Ciudad>();
        vars=new vars();
        context = this;
        pagina=0;

        copperplateGothicLight = Typeface.createFromAsset(getAssets(), "fonts/AvenirLTStd-Light.ttf");


        editTextNumCiudades=(TextView)findViewById(R.id.editTextNumCiudades);
        editTextBusquedaCiudades=(EditText)findViewById(R.id.editTextBusquedaCiudad);
        editTextBusquedaCiudades.setTypeface(copperplateGothicLight);
        //editTextBusquedaCiudades.setOnSearchActionListener(this);
        editTextBusquedaCiudades.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mAdapter.getFilter().filter(editable.toString());
            }

        });


        not_found_ciudades=(ImageView)findViewById(R.id.not_found_empresas);
        //not_found_noticias.setVisibility(View.VISIBLE);
        layoutEspera=(RelativeLayout)findViewById(R.id.layoutEsperaCiudades);
        //cardViewCategorias=(CardView) getActivity().findViewById(R.id.cardViewCategoria);

        layoutMacroEsperaCiudades=(RelativeLayout)findViewById(R.id.layoutMacroEsperaCiudades);
        linearHabilitarCiudades=(LinearLayout)findViewById(R.id.linearHabilitarCiudades);
        recycler_view_ciudades=(RecyclerView) findViewById(R.id.recycler_view_ciudades);
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new CiudadadesAdapter(this,listadoCiudades,new CiudadadesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ciudad ciudad)
            {

                if(invitado)
                {
                    Intent i=new Intent(BuscarCiudad.this, Inicio.class);
                    i.putExtra("codCiudad",ciudad.getCodCiudad());
                    sharedPreferences.remove("codCiudad");
                    sharedPreferences.putString("codCiudad",ciudad.getCodCiudad());
                    sharedPreferences.remove("nomCiudad");
                    sharedPreferences.putString("nomCiudad",ciudad.getNomCiudad());
                    startActivity(i);
                    finish();
                }
                else
                if(isUpdateApp)
                {
                    Intent i=new Intent(BuscarCiudad.this, Inicio.class);
                    i.putExtra("codCiudad",ciudad.getCodCiudad());
                    sharedPreferences.remove("codCiudad");
                    sharedPreferences.putString("codCiudad",ciudad.getCodCiudad());
                    sharedPreferences.remove("nomCiudad");
                    sharedPreferences.putString("nomCiudad",ciudad.getNomCiudad());
                    startActivity(i);
                    finish();
                }
                else
                if(!invitado)
                {
                    Intent returnIntent = new Intent();
                    sharedPreferences.remove("nomCiudad");
                    sharedPreferences.remove("codCiudad");
                    sharedPreferences.putString("codCiudad",ciudad.getCodCiudad());
                    sharedPreferences.putString("nomCiudad",ciudad.getNomCiudad());
                    returnIntent.putExtra("codCiudad",ciudad.getCodCiudad());
                    returnIntent.putExtra("nomCiudad",ciudad.getNomCiudad());
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }

            }
        });

        recycler_view_ciudades.addItemDecoration(new SimpleDividerItemDecoration(this));
        recycler_view_ciudades.setHasFixedSize(true);
        recycler_view_ciudades.setLayoutManager(mLayoutManager);
        recycler_view_ciudades.setItemAnimator(new DefaultItemAnimator());
        recycler_view_ciudades.setAdapter(mAdapter);

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




        WebServiceGetCiudades(null,TextUtils.isEmpty(idCiudad)?null:idCiudad);

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ControllerSingleton.getInstance().cancelPendingReq("getCiudadesFind");
        ControllerSingleton.getInstance().cancelPendingReq("find");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(invitado)
            {
                Intent i=new Intent(BuscarCiudad.this, Prelogin.class);
                startActivity(i);
                finish();
            }
            else
            if(isUpdateApp)
            {
                Intent i=new Intent(BuscarCiudad.this, Inicio.class);
                startActivity(i);
                finish();
            }
            else
            if(!invitado)
            {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }



    private void WebServiceGetCiudades(final String busqueda, final String categoria)
    {
        String _urlWebService = vars.ipServer.concat("/ws/getCiudades");

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
                                listadoCiudades.clear();

                                JSONArray listaCiudades = response.getJSONArray("ciudades");

                                for (int i = 0; i < listaCiudades.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) listaCiudades.get(i);
                                    Ciudad ciudad = new Ciudad();
                                    ciudad.setType(jsonObject.getString("type"));
                                    ciudad.setCodCiudad(jsonObject.getString("codCiudad"));
                                    ciudad.setNomCiudad(jsonObject.getString("nomCiudad"));
                                    editTextNumCiudades.setText(listaCiudades.length()+" Ciudades Encontradas");
                                    listadoCiudades.add(ciudad);
                                }

                                if (!((Activity) context).isFinishing())
                                {
                                    if (progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();

                                    }
                                }

                                layoutMacroEsperaCiudades.setVisibility(View.GONE);
                                linearHabilitarCiudades.setVisibility(View.VISIBLE);
                            }

                            else
                            {


                                Snackbar.make(findViewById(android.R.id.content),
                                        "No se encontraron ciudades asociadas a su busqueda", Snackbar.LENGTH_LONG).show();


                                if (!((Activity) context).isFinishing())
                                {
                                    if (progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();

                                    }
                                }

                            }
                        }
                        catch (JSONException e)
                        {
                            layoutMacroEsperaCiudades.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_ciudades.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(BuscarCiudad.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCiudades.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_ciudades.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(BuscarCiudad.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCiudades.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_ciudades.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(BuscarCiudad.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCiudades.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_ciudades.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(BuscarCiudad.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCiudades.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_ciudades.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(BuscarCiudad.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCiudades.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_ciudades.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(BuscarCiudad.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCiudades.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_ciudades.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(BuscarCiudad.this,R.style.AlertDialogTheme));
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
                headers.put("buscar", TextUtils.isEmpty(busqueda)?"":busqueda);
                headers.put("categoria", TextUtils.isEmpty(categoria)?"":categoria);
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "getEmpresasFind");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}


