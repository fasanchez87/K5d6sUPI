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
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.ingeniapps.dicmax.adapter.EmpresasAdapter;
import com.ingeniapps.dicmax.beans.Categoria;
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

public class Buscar extends AppCompatActivity
{

    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Empresa> listadoEmpresas;
    public com.ingeniapps.dicmax.vars.vars vars;
    private RecyclerView recycler_view_empresas;
    private EmpresasAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    LinearLayout linearHabilitarEmpresas;
    RelativeLayout layoutEspera;
    RelativeLayout layoutMacroEsperaEmpresas;
    ImageView not_found_empresas;
    private int pagina;
    Context context;
    private boolean solicitando=false;
    //VERSION DEL APP INSTALADA
    private String versionActualApp;
    private Typeface copperplateGothicLight;


    private ProgressDialog progressDialog;



    CardView cardViewCategorias;

    EditText editTextBusqueda;
    TextView editTextNumEmpresas;
    private String idCategoria;
    private String nomCategoria;

    DividerItemDecoration mDividerItemDecoration;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        progressDialog = new ProgressDialog(new android.support.v7.view.ContextThemeWrapper(Buscar.this,R.style.AppCompatAlertDialogStyle));
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
                finish();
            }
        });

        sharedPreferences=new gestionSharedPreferences(this);
        listadoEmpresas=new ArrayList<Empresa>();
        vars=new vars();
        context = this;
        pagina=0;

        copperplateGothicLight = Typeface.createFromAsset(getAssets(), "fonts/AvenirLTStd-Light.ttf");


        editTextNumEmpresas=(TextView)findViewById(R.id.editTextNumEmpresas);
        editTextBusqueda=(EditText)findViewById(R.id.editTextBusqueda);
        editTextBusqueda.setTypeface(copperplateGothicLight);


        not_found_empresas=(ImageView)findViewById(R.id.not_found_empresas);
        //not_found_noticias.setVisibility(View.VISIBLE);
        layoutEspera=(RelativeLayout)findViewById(R.id.layoutEsperaEmpresas);
        //cardViewCategorias=(CardView) getActivity().findViewById(R.id.cardViewCategoria);

        layoutMacroEsperaEmpresas=(RelativeLayout)findViewById(R.id.layoutMacroEsperaEmpresas);
        linearHabilitarEmpresas=(LinearLayout)findViewById(R.id.linearHabilitarEmpresas);
        recycler_view_empresas=(RecyclerView) findViewById(R.id.recycler_view_empresas);
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new EmpresasAdapter(this,listadoEmpresas,new EmpresasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Empresa empresa)
            {
                Intent i=new Intent(Buscar.this,DetalleEmpresa.class);
                i.putExtra("idEmpresa",empresa.getIdEmpresa());
                startActivity(i);
            }
        });

        recycler_view_empresas.addItemDecoration(new SimpleDividerItemDecoration(this));
        recycler_view_empresas.setHasFixedSize(true);
        recycler_view_empresas.setLayoutManager(mLayoutManager);
        recycler_view_empresas.setItemAnimator(new DefaultItemAnimator());
        recycler_view_empresas.setAdapter(mAdapter);

        ImageView buttonBuscar = (ImageView) findViewById(R.id.ivSearch);
        buttonBuscar.setClickable(true);
        buttonBuscar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                progressDialog.show();
                progressDialog.setCancelable(false);

                /*if(!TextUtils.isEmpty(editTextBusqueda.getText()))
                {*/
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //OCULTAMOS TECLADO
                    imm.hideSoftInputFromWindow(editTextBusqueda.getWindowToken(), 0);
                    WebServiceGetEmpresas(editTextBusqueda.getText().toString(),null);
               /* }*/
               /* else
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
                }*/
            }
        });

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras == null)
            {
                idCategoria = null;
                nomCategoria = null;
            }
            else
            {
                idCategoria = extras.getString("idCategoria");
                nomCategoria = extras.getString("nomCategoria");
            }
        }


        WebServiceGetEmpresas(null,TextUtils.isEmpty(idCategoria)?null:idCategoria);

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ControllerSingleton.getInstance().cancelPendingReq("getEmpresasFind");
        ControllerSingleton.getInstance().cancelPendingReq("find");
    }


    private void WebServiceGetEmpresas(final String busqueda, final String categoria)
    {

        String _urlWebService = vars.ipServer.concat("/ws/getEmpresas");


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
                                listadoEmpresas.clear();

                                layoutMacroEsperaEmpresas.setVisibility(View.GONE);
                                //recycler_view_categorias.setVisibility(View.VISIBLE);
                                linearHabilitarEmpresas.setVisibility(View.VISIBLE);

                                JSONArray listaEmpresas = response.getJSONArray("empresas");

                                for (int i = 0; i < listaEmpresas.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) listaEmpresas.get(i);
                                    Empresa empresa = new Empresa();
                                    empresa.setIdEmpresa(jsonObject.getString("id"));
                                    empresa.setImagenEmpresa(jsonObject.getString("urlImagen"));
                                    empresa.setNomEmpresa(jsonObject.getString("nombre"));
                                    empresa.setDirEmpresa(jsonObject.getString("direccion"));
                                    empresa.setDescEmpresa(jsonObject.getString("descripcion"));
                                    empresa.setType(jsonObject.getString("type"));
                                    editTextNumEmpresas.setText(TextUtils.isEmpty(nomCategoria)?listaEmpresas.length()+" Convenios":listaEmpresas.length()+" Convenios en "+nomCategoria);
                                    listadoEmpresas.add(empresa);
                                }

                                if (!((Activity) context).isFinishing())
                                {
                                    if (progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();
                                    }
                                }
                            }

                            else
                            {

                                //editTextNumEmpresas.setText("Sin resultados");


                               /* AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
                                builder
                                        .setTitle("Dicmax")
                                        .setMessage("No se encontraron empresas con los críterios de busqueda ingresados, intente de nuevo.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {

                                            }
                                        }).show();*/


                                Snackbar.make(findViewById(android.R.id.content),
                                        "No se encontraron empresas asociadas a su busqueda", Snackbar.LENGTH_LONG).show();


                                if (!((Activity) context).isFinishing())
                                {
                                    if (progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();

                                    }
                                }



                               /* layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                                layoutEspera.setVisibility(View.GONE);
                                not_found_empresas.setVisibility(View.VISIBLE);*/
                            }
                        }
                        catch (JSONException e)
                        {
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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

    private void WebServiceGetEmpresasBusqueda(final String busqueda, final String categoria)
    {

        String _urlWebService = vars.ipServer.concat("/ws/getEmpresas");

        progressDialog = new ProgressDialog(new android.support.v7.view.ContextThemeWrapper(Buscar.this,R.style.AppCompatAlertDialogStyle));
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Un momento...");
        progressDialog.show();
        progressDialog.setCancelable(false);


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

                                listadoEmpresas.clear();

                                layoutMacroEsperaEmpresas.setVisibility(View.GONE);
                                //recycler_view_categorias.setVisibility(View.VISIBLE);
                                linearHabilitarEmpresas.setVisibility(View.VISIBLE);

                                JSONArray listaEmpresas = response.getJSONArray("empresas");

                                for (int i = 0; i < listaEmpresas.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) listaEmpresas.get(i);
                                    Empresa empresa = new Empresa();
                                    empresa.setImagenEmpresa(jsonObject.getString("urlImagen"));
                                    empresa.setNomEmpresa(jsonObject.getString("nombre"));
                                    empresa.setDirEmpresa(jsonObject.getString("direccion"));
                                    empresa.setDescEmpresa(jsonObject.getString("descripcion"));
                                    empresa.setType(jsonObject.getString("type"));
                                    editTextNumEmpresas.setText(listaEmpresas.length()+" Empresas Aliadas");
                                    listadoEmpresas.add(empresa);
                                }

                                if (!((Activity) context).isFinishing())
                                {
                                    if (progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();

                                    }
                                }
                            }

                            else
                            {
                                /*layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                                layoutEspera.setVisibility(View.GONE);
                                not_found_empresas.setVisibility(View.VISIBLE);*/

                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
                                builder
                                        .setTitle("Dicmax")
                                        .setMessage("No se encontraron empresas con los críterios de busqueda ingresados, intente de nuevo.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {

                                            }
                                        }).show();

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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaEmpresas.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empresas.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Buscar.this,R.style.AlertDialogTheme));
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

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "find");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}


