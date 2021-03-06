package com.ingeniapps.dicmax.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ingeniapps.dicmax.Text.FontStylerView;
import com.ingeniapps.dicmax.activity.Buscar;
import com.ingeniapps.dicmax.adapter.CategoriaAdapter;
import com.ingeniapps.dicmax.beans.Categoria;
import com.ingeniapps.dicmax.beans.Compromiso;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.util.SimpleDividerItemDecoration;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.adapter.CompromisoAdapter;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Compromisos extends Fragment
{

    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Compromiso> listadoCompromisos;
    private vars vars;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //not_found_categorias=(ImageView)getActivity().findViewById(R.id.not_found_categorias);
        //not_found_noticias.setVisibility(View.VISIBLE);
        //layoutEspera=(RelativeLayout)getActivity().findViewById(R.id.layoutEsperaCategorias);
        //cardViewCategorias=(CardView) getActivity().findViewById(R.id.cardViewCategoria);

        layoutMacroEsperaCompromisos=(RelativeLayout)getActivity().findViewById(R.id.layoutMacroEsperaCompromisos);
        linearHabilitarCompromisos=(LinearLayout)getActivity().findViewById(R.id.linearHabilitarCompromisos);
        linearSinCompromisos=(LinearLayout)getActivity().findViewById(R.id.linearSinCompromisos);
        recycler_view_compromisos=(RecyclerView) getActivity().findViewById(R.id.recycler_view_compromisos);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mAdapter = new CompromisoAdapter(getActivity(),listadoCompromisos,new CompromisoAdapter.OnItemClickListener()
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

        recycler_view_compromisos.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
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

        copperplateGothicLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AvenirLTStd-Light.ttf");
        editTextNumPagosPendientes=(FontStylerView)getActivity().findViewById(R.id.editTextNumPagosPendientes);
        editTextNumPagosPendientes.setTypeface(copperplateGothicLight);

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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this.getActivity(),R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this.getActivity(),R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this.getActivity(),R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this.getActivity(),R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this.getActivity(),R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this.getActivity(),R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Compromisos.this.getActivity(),R.style.AlertDialogTheme));
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
