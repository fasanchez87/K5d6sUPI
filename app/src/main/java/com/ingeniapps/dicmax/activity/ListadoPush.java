package com.ingeniapps.dicmax.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.adapter.CategoriaAdapter;
import com.ingeniapps.dicmax.adapter.MensajesAdapter;
import com.ingeniapps.dicmax.beans.Categoria;
import com.ingeniapps.dicmax.beans.Mensaje;
import com.ingeniapps.dicmax.fragment.Categorias;
import com.ingeniapps.dicmax.fragment.FragmentIntentIntegrator;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListadoPush extends AppCompatActivity
{
    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Mensaje> listadoMensajes;
    public com.ingeniapps.dicmax.vars.vars vars;
    private RecyclerView recycler_view_mensajes;
    private MensajesAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    LinearLayout linearHabilitarMensajes;
    RelativeLayout layoutMacroEsperaNotificaciones;
    Context context;
    //VERSION DEL APP INSTALADA
    private String versionActualApp;
    private Typeface copperplateGothicLight;
    private MenuItem menuItem;
    private Boolean guardarSesion;

    EditText editTextBuscarInicio;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_push);
        sharedPreferences=new gestionSharedPreferences(getApplicationContext());
        listadoMensajes=new ArrayList<Mensaje>();
        vars=new vars();
        context = this;
        guardarSesion=sharedPreferences.getBoolean("GuardarSesion");

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

        layoutMacroEsperaNotificaciones=findViewById(R.id.layoutMacroEsperaNotificaciones);
        linearHabilitarMensajes=findViewById(R.id.linearHabilitarMensajes);
        recycler_view_mensajes=findViewById(R.id.recycler_view_mensajes);
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new MensajesAdapter(this,listadoMensajes,new MensajesAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(Mensaje mensaje)
            {
                Intent i=new Intent(ListadoPush.this,DetallePush.class);
                i.putExtra("codPush",mensaje.getCodPush());
                i.putExtra("titulo",mensaje.getTitMensaje());
                i.putExtra("mensaje",mensaje.getDesMensaje());
                i.putExtra("urlImagen",mensaje.getUrlImagen());
                i.putExtra("fecha",mensaje.getFecEnvio());
                startActivity(i);
            }
        });

        recycler_view_mensajes.setHasFixedSize(true);
        recycler_view_mensajes.setLayoutManager(mLayoutManager);
        recycler_view_mensajes.setItemAnimator(new DefaultItemAnimator());
        recycler_view_mensajes.setAdapter(mAdapter);

        copperplateGothicLight = Typeface.createFromAsset(getAssets(), "fonts/AvenirLTStd-Light.ttf");

        //VERSION APP
        try
        {
            versionActualApp=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        WebServiceGetMensajesPush(sharedPreferences.getString("codUsuario"));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void WebServiceGetMensajesPush(final String codUsuario)
    {
        listadoMensajes.clear();
        String _urlWebService = vars.ipServer.concat("/ws/getNotificaciones");

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
                                JSONArray listaMensajes = response.getJSONArray("mensajes");

                                for (int i = 0; i < listaMensajes.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) listaMensajes.get(i);
                                    Mensaje mensaje = new Mensaje();
                                    mensaje.setType(jsonObject.getString("type"));//type==evento
                                    mensaje.setTitMensaje(jsonObject.getString("titMensaje"));
                                    mensaje.setDesMensaje(jsonObject.getString("desMensaje"));
                                    mensaje.setFecEnvio(jsonObject.getString("fecTimeStamp"));
                                    mensaje.setUrlImagen(jsonObject.getString("urlImagen"));
                                    listadoMensajes.add(mensaje);
                                }

                                layoutMacroEsperaNotificaciones.setVisibility(View.GONE);
                                linearHabilitarMensajes.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (JSONException e)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ListadoPush.this,R.style.AlertDialogTheme));
                                builder
                                        .setMessage(e.getMessage().toString())
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                            }
                                        }).show();
                            }

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

                            if (!((Activity) context).isFinishing()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ListadoPush.this, R.style.AlertDialogTheme));
                                builder
                                        .setMessage("Error de conexión, sin respuesta del servidor.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        }).show();
                            }
                        }

                        else

                        if (error instanceof NoConnectionError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ListadoPush.this,R.style.AlertDialogTheme));
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
                        }

                        else

                        if (error instanceof AuthFailureError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ListadoPush.this,R.style.AlertDialogTheme));
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
                        }

                        else

                        if (error instanceof ServerError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ListadoPush.this,R.style.AlertDialogTheme));
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
                        }

                        else

                        if (error instanceof NetworkError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ListadoPush.this,R.style.AlertDialogTheme));
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
                        }

                        else

                        if (error instanceof ParseError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ListadoPush.this,R.style.AlertDialogTheme));
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
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("codUsuario",codUsuario);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "getCategorias");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
