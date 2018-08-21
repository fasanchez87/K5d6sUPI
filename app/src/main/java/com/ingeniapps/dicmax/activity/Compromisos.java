package com.ingeniapps.dicmax.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.ingeniapps.dicmax.Text.FontStylerView;
import com.ingeniapps.dicmax.adapter.CompromisoAdapter;
import com.ingeniapps.dicmax.beans.Compromiso;
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

public class Compromisos extends AppCompatActivity
{

    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Compromiso> listadoCompromisos;
    private com.ingeniapps.dicmax.vars.vars vars;

    private RecyclerView recycler_view_compromisos;
    private CompromisoAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    LinearLayout linearHabilitarCompromisos;
    LinearLayout linearSinCompromisos;
    RelativeLayout layoutMacroEsperaCompromisos;
    //ImageView not_found_categorias;
    private int pagina;
    Context context;
    private boolean solicitando=false;
    //VERSION DEL APP INSTALADA
    private String versionActualApp;
    private Typeface copperplateGothicLight;
    private FontStylerView editTextNumPagosPendientes;
    DividerItemDecoration mDividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compromisos);

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

        sharedPreferences=new gestionSharedPreferences(getApplicationContext());
        listadoCompromisos=new ArrayList<Compromiso>();
        vars=new vars();
        context = this;

        layoutMacroEsperaCompromisos=(RelativeLayout)findViewById(R.id.layoutMacroEsperaCompromisos);
        linearHabilitarCompromisos=(LinearLayout)findViewById(R.id.linearHabilitarCompromisos);
        linearSinCompromisos=(LinearLayout)findViewById(R.id.linearSinCompromisos);
        recycler_view_compromisos=(RecyclerView) findViewById(R.id.recycler_view_compromisos);
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new CompromisoAdapter(this,listadoCompromisos,new CompromisoAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(Compromiso compromiso)
            {
                /*Intent i=new Intent(Categorias.this.getActivity(),Buscar.class);
                i.putExtra("idCategoria",categoria.getCodCategoria());
                i.putExtra("nomCategoria",categoria.getNomCategoria());
                startActivity(i);*/
            }
        });

        recycler_view_compromisos.addItemDecoration(new SimpleDividerItemDecoration(this));
        recycler_view_compromisos.setHasFixedSize(true);
        recycler_view_compromisos.setLayoutManager(mLayoutManager);
        recycler_view_compromisos.setItemAnimator(new DefaultItemAnimator());
        recycler_view_compromisos.setAdapter(mAdapter);


      /*  EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager)
        {
            @Override
            public void onLoadMore(final int page, final int totalItemsCount, final RecyclerView view)
            {
                final int curSize = mAdapter.getItemCount();

                view.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(!solicitando)
                        {
                            WebServiceGetNoticiasMore();
                        }
                    }
                });
            }
        };*/

        copperplateGothicLight = Typeface.createFromAsset(getAssets(), "fonts/AvenirLTStd-Light.ttf");
        editTextNumPagosPendientes=(FontStylerView)findViewById(R.id.editTextNumPagosPendientes);
        editTextNumPagosPendientes.setTypeface(copperplateGothicLight);

        //VERSION APP
        try
        {
            versionActualApp=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }


//        recycler_view_noticias.addOnScrollListener(scrollListener);
        //WebServiceGetNoticias();

        WebServiceGetCompromisos();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //updateTokenFCMToServer(tokenFCM);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ControllerSingleton.getInstance().cancelPendingReq("getCategorias");
    }


    private void WebServiceGetCompromisos()
    {
        listadoCompromisos.clear();
        String _urlWebService = vars.ipServer.concat("/ws/getCompromisos");

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
                                JSONArray listaCompromisos = response.getJSONArray("compromisos");
                                editTextNumPagosPendientes.setText(""+listaCompromisos.length()+" Pagos Pendientes");

                                for (int i = 0; i < listaCompromisos.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) listaCompromisos.get(i);
                                    Compromiso compromiso = new Compromiso();
                                    compromiso.setType(jsonObject.getString("type"));//type==evento
                                    compromiso.setFecCompro(jsonObject.getString("fecCompro"));//type==evento
                                    compromiso.setValPendiente(jsonObject.getString("valPendiente"));//type==evento
                                    compromiso.setNumDias(jsonObject.getString("numDias"));//type==evento
                                    listadoCompromisos.add(compromiso);
                                }

                                layoutMacroEsperaCompromisos.setVisibility(View.GONE);
                                linearHabilitarCompromisos.setVisibility(View.VISIBLE);
                            }

                            else
                            {
                                layoutMacroEsperaCompromisos.setVisibility(View.GONE);
                                linearHabilitarCompromisos.setVisibility(View.GONE);
                                linearSinCompromisos.setVisibility(View.VISIBLE);

                            }
                        }
                        catch (JSONException e)
                        {
                            layoutMacroEsperaCompromisos.setVisibility(View.GONE);
                            linearHabilitarCompromisos.setVisibility(View.GONE);
                            linearSinCompromisos.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCompromisos.setVisibility(View.GONE);
                            linearHabilitarCompromisos.setVisibility(View.GONE);
                            linearSinCompromisos.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCompromisos.setVisibility(View.GONE);
                            linearHabilitarCompromisos.setVisibility(View.GONE);
                            linearSinCompromisos.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCompromisos.setVisibility(View.GONE);
                            linearHabilitarCompromisos.setVisibility(View.GONE);
                            linearSinCompromisos.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCompromisos.setVisibility(View.GONE);
                            linearHabilitarCompromisos.setVisibility(View.GONE);
                            linearSinCompromisos.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCompromisos.setVisibility(View.GONE);
                            linearHabilitarCompromisos.setVisibility(View.GONE);
                            linearSinCompromisos.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this,R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCompromisos.setVisibility(View.GONE);
                            linearHabilitarCompromisos.setVisibility(View.GONE);
                            linearSinCompromisos.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this,R.style.AlertDialogTheme));
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
                headers.put("codUsuario",sharedPreferences.getString("codUsuario"));
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "getCategorias");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
