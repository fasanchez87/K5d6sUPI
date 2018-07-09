package com.ingeniapps.dicmax.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ingeniapps.dicmax.adapter.PagoAdapter;
import com.ingeniapps.dicmax.beans.Compromiso;
import com.ingeniapps.dicmax.beans.Pago;
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

public class Pagos extends Fragment
{

    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Pago> listadoPagos;
    private vars vars;

    private RecyclerView recycler_view_pagos;
    private PagoAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    LinearLayout linearHabilitarPagos;
    LinearLayout linearSinHistorial;
    RelativeLayout layoutMacroEsperaPagos;
    //ImageView not_found_categorias;
    private int pagina;
    Context context;
    private boolean solicitando=false;
    //VERSION DEL APP INSTALADA
    private String versionActualApp;
    private Typeface copperplateGothicLight;
    private FontStylerView editTextNumPagosRealizados;
    DividerItemDecoration mDividerItemDecoration;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPreferences=new gestionSharedPreferences(getActivity().getApplicationContext());
        listadoPagos=new ArrayList<Pago>();
        vars=new vars();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pagos, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //not_found_categorias=(ImageView)getActivity().findViewById(R.id.not_found_categorias);
        //not_found_noticias.setVisibility(View.VISIBLE);
        //layoutEspera=(RelativeLayout)getActivity().findViewById(R.id.layoutEsperaCategorias);
        //cardViewCategorias=(CardView) getActivity().findViewById(R.id.cardViewCategoria);

        layoutMacroEsperaPagos=(RelativeLayout)getActivity().findViewById(R.id.layoutMacroEsperaPagos);
        linearHabilitarPagos=(LinearLayout)getActivity().findViewById(R.id.linearHabilitarPagos);
        linearSinHistorial=(LinearLayout)getActivity().findViewById(R.id.linearSinHistorial);
        recycler_view_pagos=(RecyclerView) getActivity().findViewById(R.id.recycler_view_pagos);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mAdapter = new PagoAdapter(getActivity(),listadoPagos,new PagoAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(Pago pago)
            {
               /* Intent i=new Intent(Categorias.this.getActivity(),Buscar.class);
                i.putExtra("idCategoria",categoria.getCodCategoria());
                i.putExtra("nomCategoria",categoria.getNomCategoria());
                startActivity(i);*/
            }
        });

        recycler_view_pagos.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recycler_view_pagos.setHasFixedSize(true);
        recycler_view_pagos.setLayoutManager(mLayoutManager);
        recycler_view_pagos.setItemAnimator(new DefaultItemAnimator());
        recycler_view_pagos.setAdapter(mAdapter);


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

        copperplateGothicLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AvenirLTStd-Light.ttf");
        editTextNumPagosRealizados=(FontStylerView)getActivity().findViewById(R.id.editTextNumPagosRealizados);
        editTextNumPagosRealizados.setTypeface(copperplateGothicLight);

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

        WebServiceGetHistorialPagos();
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


    private void WebServiceGetHistorialPagos()
    {
        listadoPagos.clear();
        String _urlWebService = vars.ipServer.concat("/ws/getTransUsr");

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
                                JSONArray listaPagos = response.getJSONArray("historial");
                                editTextNumPagosRealizados.setText(""+listaPagos.length()+" Historiales de Compra");

                                for (int i = 0; i < listaPagos.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) listaPagos.get(i);
                                    Pago pago = new Pago();
                                    pago.setType(jsonObject.getString("type"));//type==evento
                                    pago.setTipTransaccion(jsonObject.getString("tipTransaccion"));//type==evento
                                    pago.setNomEmpresa(jsonObject.getString("nomEmpresa"));//type==evento
                                    pago.setValTransaccion(jsonObject.getString("valTransaccion"));//type==evento
                                    pago.setCodEstado(jsonObject.getString("codEstado"));//type==evento
                                    pago.setNomEstado(jsonObject.getString("nomEstado"));//type==evento
                                    pago.setFecAprobacion(jsonObject.getString("fecAprobacion"));//type==evento
                                    listadoPagos.add(pago);
                                }

                                layoutMacroEsperaPagos.setVisibility(View.GONE);
                                linearHabilitarPagos.setVisibility(View.VISIBLE);
                            }

                            else
                            {
                                layoutMacroEsperaPagos.setVisibility(View.GONE);
                                linearHabilitarPagos.setVisibility(View.GONE);
                                linearSinHistorial.setVisibility(View.VISIBLE);

                            }
                        }
                        catch (JSONException e)
                        {
                            layoutMacroEsperaPagos.setVisibility(View.GONE);
                            linearHabilitarPagos.setVisibility(View.GONE);
                            linearSinHistorial.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pagos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaPagos.setVisibility(View.GONE);
                            linearHabilitarPagos.setVisibility(View.GONE);
                            linearSinHistorial.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pagos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaPagos.setVisibility(View.GONE);
                            linearHabilitarPagos.setVisibility(View.GONE);
                            linearSinHistorial.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pagos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaPagos.setVisibility(View.GONE);
                            linearHabilitarPagos.setVisibility(View.GONE);
                            linearSinHistorial.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pagos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaPagos.setVisibility(View.GONE);
                            linearHabilitarPagos.setVisibility(View.GONE);
                            linearSinHistorial.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pagos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaPagos.setVisibility(View.GONE);
                            linearHabilitarPagos.setVisibility(View.GONE);
                            linearSinHistorial.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pagos.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaPagos.setVisibility(View.GONE);
                            linearHabilitarPagos.setVisibility(View.GONE);
                            linearSinHistorial.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pagos.this.getActivity(),R.style.AlertDialogTheme));
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
