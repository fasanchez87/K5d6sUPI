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
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.ingeniapps.dicmax.activity.*;
import com.ingeniapps.dicmax.adapter.CompromisoAdapter;
import com.ingeniapps.dicmax.beans.Compromiso;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.util.SimpleDividerItemDecoration;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Cuenta extends Fragment
{
    private FontStylerView editTextCupoBono, editTextCupo, editTextTotalCupo, editTextNombre, editTextCedula, editTextCelular, editTextEmail,comPagos;
    private Button buttonCambioClave;
    private gestionSharedPreferences sharedPreferences;
    private vars vars;

    LinearLayout linearHabilitarPagos;
    RelativeLayout layoutMacroEsperaCuenta;

    Context context;
    private NumberFormat numberFormat=NumberFormat.getNumberInstance(Locale.GERMAN);
    private Typeface copperplateGothicLight;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cuenta, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPreferences=new gestionSharedPreferences(getActivity().getApplicationContext());
        vars=new vars();
        context = getActivity();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        copperplateGothicLight = Typeface.createFromAsset(Cuenta.this.getActivity().getAssets(), "fonts/AvenirLTStd-Light.ttf");


        layoutMacroEsperaCuenta=(RelativeLayout)getActivity().findViewById(R.id.layoutMacroEsperaCuenta);
        linearHabilitarPagos=(LinearLayout)getActivity().findViewById(R.id.linearHabilitarPagos);

        editTextCupoBono=(FontStylerView) getActivity().findViewById(R.id.editTextCupoBono);
        editTextCupo=(FontStylerView) getActivity().findViewById(R.id.editTextCupo);
        editTextTotalCupo=(FontStylerView) getActivity().findViewById(R.id.editTextTotalCupo);
        editTextNombre=(FontStylerView) getActivity().findViewById(R.id.editTextNombre);
        editTextCedula=(FontStylerView) getActivity().findViewById(R.id.editTextCedula);
        editTextCelular=(FontStylerView) getActivity().findViewById(R.id.editTextCelular);
        editTextEmail=(FontStylerView) getActivity().findViewById(R.id.editTextEmail);
        comPagos=(FontStylerView) getActivity().findViewById(R.id.comPagos);
        comPagos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i=new Intent(Cuenta.this.getActivity(), com.ingeniapps.dicmax.activity.Compromisos.class);
                startActivity(i);
            }
        });


        buttonCambioClave=(Button) getActivity().findViewById(R.id.buttonCambioClave);
        buttonCambioClave.setTypeface(copperplateGothicLight);

        buttonCambioClave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i=new Intent(Cuenta.this.getActivity(), CambioClave.class);
                startActivity(i);
            }
        });

        WebServiceGetDataUser();
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


    private void WebServiceGetDataUser()
    {
        String _urlWebService = vars.ipServer.concat("/ws/getDataUser");

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
                                editTextCupoBono.setText("$"+numberFormat.format(Double.parseDouble(response.getString("valBono"))));
                                editTextCupo.setText("$"+numberFormat.format(Double.parseDouble(response.getString("valCupo"))));
                                editTextTotalCupo.setText("$"+numberFormat.format(Double.parseDouble(response.getString("valTotal"))));

                                editTextNombre.setText(response.getString("nomCompleto"));
                                editTextCedula.setText(response.getString("numDocumento"));
                                editTextCelular.setText(response.getString("telUsuario"));
                                editTextEmail.setText(response.getString("emaUsuario"));

                                layoutMacroEsperaCuenta.setVisibility(View.GONE);
                                linearHabilitarPagos.setVisibility(View.VISIBLE);
                            }

                            else
                            {
                                layoutMacroEsperaCuenta.setVisibility(View.VISIBLE);
                                linearHabilitarPagos.setVisibility(View.GONE);

                            }
                        }
                        catch (JSONException e)
                        {
                            layoutMacroEsperaCuenta.setVisibility(View.VISIBLE);
                            linearHabilitarPagos.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Cuenta.this.getActivity(),R.style.AlertDialogTheme));
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
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (error instanceof TimeoutError)
                        {
                            layoutMacroEsperaCuenta.setVisibility(View.VISIBLE);
                            linearHabilitarPagos.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Cuenta.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCuenta.setVisibility(View.VISIBLE);
                            linearHabilitarPagos.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Cuenta.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCuenta.setVisibility(View.VISIBLE);
                            linearHabilitarPagos.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Cuenta.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCuenta.setVisibility(View.VISIBLE);
                            linearHabilitarPagos.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Cuenta.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCuenta.setVisibility(View.VISIBLE);
                            linearHabilitarPagos.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Cuenta.this.getActivity(),R.style.AlertDialogTheme));
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
                            layoutMacroEsperaCuenta.setVisibility(View.VISIBLE);
                            linearHabilitarPagos.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Cuenta.this.getActivity(),R.style.AlertDialogTheme));
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

