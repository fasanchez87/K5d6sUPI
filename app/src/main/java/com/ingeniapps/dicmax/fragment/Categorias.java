package com.ingeniapps.dicmax.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ingeniapps.dicmax.activity.Buscar;
import com.ingeniapps.dicmax.activity.BuscarCiudad;
import com.ingeniapps.dicmax.activity.DetalleEmpresa;
import com.ingeniapps.dicmax.activity.Inicio;
import com.ingeniapps.dicmax.activity.Login;
import com.ingeniapps.dicmax.activity.RecargasElectronicas;
import com.ingeniapps.dicmax.adapter.CategoriaAdapter;
import com.ingeniapps.dicmax.beans.Categoria;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
/*import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;*/
import com.ingeniapps.dicmax.R;
/*import com.ingeniapps.dicmax.adapter.EndlessRecyclerViewScrollListener;
import com.ingeniapps.dicmax.adapter.NoticiaAdapter;*/
import com.ingeniapps.dicmax.app.Config;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.util.NotificationUtils;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Categorias extends Fragment
{
    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Categoria> listadoCategorias;
    public vars vars;
    private RecyclerView recycler_view_categorias;
    private CategoriaAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    LinearLayout linearHabilitarCompras;
    RelativeLayout layoutEspera;
    RelativeLayout layoutMacroEsperaCategorias;
    ImageView not_found_categorias;
    private int pagina;
    Context context;
    private boolean solicitando=false;
    //VERSION DEL APP INSTALADA
    private String versionActualApp;
    private Typeface copperplateGothicLight;
    private MenuItem menuItem;
    private Boolean guardarSesion;

    CardView cardViewCategorias;
    FloatingActionButton botonComprar;

    EditText editTextBuscarInicio;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sharedPreferences=new gestionSharedPreferences(getActivity().getApplicationContext());
        listadoCategorias=new ArrayList<Categoria>();
        vars=new vars();
        context = getActivity();
        guardarSesion=sharedPreferences.getBoolean("GuardarSesion");
        pagina=0;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        not_found_categorias=(ImageView)getActivity().findViewById(R.id.not_found_categorias);
        //not_found_noticias.setVisibility(View.VISIBLE);
        layoutEspera=(RelativeLayout)getActivity().findViewById(R.id.layoutEsperaCategorias);
        //cardViewCategorias=(CardView) getActivity().findViewById(R.id.cardViewCategoria);
        botonComprar=getActivity().findViewById(R.id.botonComprar);

        layoutMacroEsperaCategorias=(RelativeLayout)getActivity().findViewById(R.id.layoutMacroEsperaCategorias);
        linearHabilitarCompras=(LinearLayout)getActivity().findViewById(R.id.linearHabilitarCompras);
        recycler_view_categorias=(RecyclerView) getActivity().findViewById(R.id.recycler_view_categorias);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mAdapter = new CategoriaAdapter(getActivity(),listadoCategorias,new CategoriaAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(Categoria categoria)
            {

                if(TextUtils.equals(categoria.getCodCategoria(),"12")&& !guardarSesion)//SI ES INVITADO LO SACAMOS
                {
                    cargarLogin();
                }
                else
                if(TextUtils.equals(categoria.getCodCategoria(),"12"))//CATEGORIA RECARGAS
                {
                    Intent i=new Intent(Categorias.this.getActivity(),RecargasElectronicas.class);
                    startActivity(i);
                }
                else
                {
                    Intent i=new Intent(Categorias.this.getActivity(),Buscar.class);
                    i.putExtra("idCategoria",categoria.getCodCategoria());
                    i.putExtra("nomCategoria",categoria.getNomCategoria());
                    i.putExtra("codCiudad",sharedPreferences.getString("codCiudad"));
                    startActivity(i);
                }
            }
        });

        recycler_view_categorias.setHasFixedSize(true);
        recycler_view_categorias.setLayoutManager(mLayoutManager);
        recycler_view_categorias.setItemAnimator(new DefaultItemAnimator());
        recycler_view_categorias.setAdapter(mAdapter);

        copperplateGothicLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AvenirLTStd-Light.ttf");
        editTextBuscarInicio=(EditText)getActivity().findViewById(R.id.editTextBuscarInicio);
        //editTextBuscarInicio.setHint("¿Buscas algo en "+sharedPreferences.getString("nomCiudad")+"?");//CIUDAD INICIAL DEL USUARIO
        editTextBuscarInicio.setTypeface(copperplateGothicLight);
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
        });

        botonComprar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentIntentIntegrator integrator = new FragmentIntentIntegrator(Categorias.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Ubique su código de compra");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setOrientationLocked(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        //VERSION APP
        try
        {
            versionActualApp=getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        WebServiceGetCategorias(sharedPreferences.getString("codCiudad"));
    }

    public void cargarLogin()
    {
        Intent intent = new Intent(Categorias.this.getActivity(), Login.class);//CERRAMOS LAS ACTIVIDADES TODAS ANTERIORES CREADAS
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categorias, container, false);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        MenuInflater findMenuItems = getActivity().getMenuInflater();
        findMenuItems.inflate(R.menu.menu_conf_ciudad, menu);
        menuItem=menu.findItem(R.id.menu_location_empresa);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                String result=data.getStringExtra("codCiudad");
                //Toast.makeText(Categorias.this.getActivity(),"Resultado: "+sharedPreferences.getString("codCiudad"),Toast.LENGTH_LONG).show();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
                //WebServiceGetCategorias(sharedPreferences.getString("codCiudad"));
            }
            if (resultCode == Activity.RESULT_CANCELED)
            {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {

            case R.id.menu_location_empresa:

                Intent i = new Intent(Categorias.this.getActivity(), BuscarCiudad.class);
                startActivityForResult(i, 1);
               return false;


            default:
                break;
        }

        return false;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ControllerSingleton.getInstance().cancelPendingReq("getCategorias");
    }


    private void WebServiceGetCategorias(final String codCiudad)
    {
        listadoCategorias.clear();
        String _urlWebService = vars.ipServer.concat("/ws/getCategorias");

        //Toast.makeText(Categorias.this.getActivity(),"CodCiudad es: "+codCiudad,Toast.LENGTH_LONG).show();

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
                                JSONArray listaCategorias = response.getJSONArray("categorias");

                                for (int i = 0; i < listaCategorias.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) listaCategorias.get(i);
                                    Categoria categoria = new Categoria();
                                    categoria.setType(jsonObject.getString("type"));//type==evento
                                    categoria.setNomCategoria(jsonObject.getString("categoria"));
                                    categoria.setCodCategoria(jsonObject.getString("id"));
                                    listadoCategorias.add(categoria);
                                }

                                if (guardarSesion==false && !TextUtils.isEmpty(sharedPreferences.getString("nomCiudad")))//SI ES INVITADO
                                {
                                    editTextBuscarInicio.setHint("¿Buscas algo en "+sharedPreferences.getString("nomCiudad")+"?");//CIUDAD INICIAL DEL USUARIO
                                }
                                else
                                if (guardarSesion==false)//SI ES INVITADO
                                {
                                    editTextBuscarInicio.setHint("¿Buscas algo?");//CIUDAD INICIAL DEL USUARIO
                                }
                                else
                                {
                                    editTextBuscarInicio.setHint("¿Buscas algo en "+sharedPreferences.getString("nomCiudad")+"?");//CIUDAD INICIAL DEL USUARIO
                                }

                                layoutMacroEsperaCategorias.setVisibility(View.GONE);
                                //recycler_view_categorias.setVisibility(View.VISIBLE);
                                linearHabilitarCompras.setVisibility(View.VISIBLE);
                                menuItem.setVisible(true);
                                //botonComprar.setVisibility(View.VISIBLE);
                            }

                            else
                            {
                                layoutMacroEsperaCategorias.setVisibility(View.VISIBLE);
                                layoutEspera.setVisibility(View.GONE);
                                not_found_categorias.setVisibility(View.VISIBLE);

                            }
                        }
                        catch (JSONException e)
                        {
                            layoutMacroEsperaCategorias.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_categorias.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Categorias.this.getActivity(),R.style.AlertDialogTheme));
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
                            not_found_categorias.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Categorias.this.getActivity(),R.style.AlertDialogTheme));
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
                            not_found_categorias.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Categorias.this.getActivity(),R.style.AlertDialogTheme));
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
                            not_found_categorias.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Categorias.this.getActivity(),R.style.AlertDialogTheme));
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
                            not_found_categorias.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Categorias.this.getActivity(),R.style.AlertDialogTheme));
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
                            not_found_categorias.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Categorias.this.getActivity(),R.style.AlertDialogTheme));
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
                            not_found_categorias.setVisibility(View.VISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Categorias.this.getActivity(),R.style.AlertDialogTheme));
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
                headers.put("versionApp",versionActualApp);
                headers.put("codCiudad",codCiudad);//CIUDAD INICIAL DEL USUARIO
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "getCategorias");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
