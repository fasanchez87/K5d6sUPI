package com.ingeniapps.dicmax.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.ingeniapps.dicmax.activity.Buscar;
import com.ingeniapps.dicmax.activity.DetalleEmpresa;
import com.ingeniapps.dicmax.activity.Inicio;
import com.ingeniapps.dicmax.adapter.CategoriaAdapter;
import com.ingeniapps.dicmax.adapter.PuntoPagoAdapter;
import com.ingeniapps.dicmax.beans.Categoria;
import com.ingeniapps.dicmax.beans.PuntoPago;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
/*import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;*/
import com.ingeniapps.dicmax.R;
/*import com.ingeniapps.dicmax.adapter.EndlessRecyclerViewScrollListener;
import com.ingeniapps.dicmax.adapter.NoticiaAdapter;*/
import com.ingeniapps.dicmax.app.Config;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.util.NotificationUtils;
import com.ingeniapps.dicmax.util.SimpleDividerItemDecoration;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Puntos extends Fragment
{
    private gestionSharedPreferences sharedPreferences;
    private ArrayList<PuntoPago> listadoPuntosPago;
    public vars vars;
    private RecyclerView recycler_view_puntos_pago;
    private PuntoPagoAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    LinearLayout linearHabilitarCompras;
    RelativeLayout layoutEspera;
    RelativeLayout layoutMacroEsperaCategorias;
    ImageView not_found_puntos_pago;
    private int pagina;
    Context context;
    private boolean solicitando=false;
    //VERSION DEL APP INSTALADA
    private String versionActualApp;

    CardView cardViewCategorias;

    EditText editTextBuscarInicio;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPreferences=new gestionSharedPreferences(getActivity().getApplicationContext());
        listadoPuntosPago=new ArrayList<PuntoPago>();
        vars=new vars();
        context = getActivity();
        pagina=0;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        not_found_puntos_pago=(ImageView)getActivity().findViewById(R.id.not_found_categorias);
        //not_found_noticias.setVisibility(View.VISIBLE);
        layoutEspera=(RelativeLayout)getActivity().findViewById(R.id.layoutEsperaPuntos);
        //cardViewCategorias=(CardView) getActivity().findViewById(R.id.cardViewCategoria);

        layoutMacroEsperaCategorias=(RelativeLayout)getActivity().findViewById(R.id.layoutMacroEsperaPuntos);
        linearHabilitarCompras=(LinearLayout)getActivity().findViewById(R.id.linearHabilitarPuntos);
        recycler_view_puntos_pago=(RecyclerView) getActivity().findViewById(R.id.recycler_view_puntos_pago);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mAdapter = new PuntoPagoAdapter(getActivity(),listadoPuntosPago,new PuntoPagoAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(PuntoPago punto)
            {
                /*Intent i=new Intent(Categorias.this.getActivity(),Buscar.class);
                i.putExtra("idCategoria",categoria.getCodCategoria());
                startActivity(i);*/
            }
        });


        recycler_view_puntos_pago.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recycler_view_puntos_pago.setHasFixedSize(true);
        recycler_view_puntos_pago.setLayoutManager(mLayoutManager);
        recycler_view_puntos_pago.setItemAnimator(new DefaultItemAnimator());
        recycler_view_puntos_pago.setAdapter(mAdapter);

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

      /*  editTextBuscarInicio=(EditText)getActivity().findViewById(R.id.editTextBuscarInicio);
        editTextBuscarInicio.setInputType(InputType.TYPE_NULL);
        editTextBuscarInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i=new Intent(Categorias.this.getActivity(),Buscar.class);
                startActivity(i);
            }
        });
        editTextBuscarInicio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus) {
                    // showMyDialog();
                }
            }
        });*/

        //VERSION APP
        try
        {
            versionActualApp=getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }


//        recycler_view_noticias.addOnScrollListener(scrollListener);
        //WebServiceGetNoticias();

        WebServiceGetPuntos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_puntos_pago, container, false);
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
        ControllerSingleton.getInstance().cancelPendingReq("getPuntos");
    }

    private void WebServiceGetPuntos()
    {
        listadoPuntosPago.clear();
        String _urlWebService = vars.ipServer.concat("/ws/getPuntos");

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
                                layoutMacroEsperaCategorias.setVisibility(View.GONE);
                                //recycler_view_categorias.setVisibility(View.VISIBLE);
                                linearHabilitarCompras.setVisibility(View.VISIBLE);

                                JSONArray puntosPago = response.getJSONArray("empresas");

                                for (int i = 0; i < puntosPago.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) puntosPago.get(i);
                                    PuntoPago punto = new PuntoPago();
                                    punto.setType(jsonObject.getString("type"));//type==evento
                                    punto.setNombre(jsonObject.getString("nombre"));
                                    punto.setDireccion(jsonObject.getString("direccion"));
                                    listadoPuntosPago.add(punto);
                                }
                            }

                            else
                            {
                                layoutMacroEsperaCategorias.setVisibility(View.VISIBLE);
                                layoutEspera.setVisibility(View.GONE);
                                not_found_puntos_pago.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (JSONException e)
                        {
                            layoutMacroEsperaCategorias.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_puntos_pago.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Puntos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCategorias.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_puntos_pago.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Puntos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCategorias.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_puntos_pago.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Puntos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCategorias.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_puntos_pago.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Puntos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCategorias.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_puntos_pago.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Puntos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCategorias.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_puntos_pago.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Puntos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCategorias.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_puntos_pago.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Puntos.this.getActivity(),R.style.AlertDialogTheme));
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
                /*headers.put("versionApp",versionActualApp);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));*/
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "getPuntos");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
