package com.ingeniapps.dicmax.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.volley.ControllerSingleton;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.ingeniapps.dicmax.vars.vars;

public class FormaPago extends AppCompatActivity
{

    private MaterialSpinner materialDesignSpinnerPlazos;
    private MaterialSpinner spinnerPeriodoPago;
    private ArrayList<String> plazos;
    private ArrayList<String> periodos;
    private String valPago;
    private String numDocumento;
    private String numTransaccion;
    private String clave;
    private String codPlazo;
    private String codPeriodo;
    private TextView editTextValorConfirmarPago;
    private NumberFormat numberFormat;
    private Button botonRealizarPago;
    private ProgressDialog progressDialog;
    private vars vars;
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forma_pago);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //this line shows back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FormaPago.this,R.style.AlertDialogTheme));
                builder
                        .setTitle("Kupi")
                        .setMessage("¿Deseas cancelar la transacción ahora?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                finish();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                }).show();



            }

        });

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras == null)
            {
                valPago=null;
                numDocumento=null;
                numTransaccion=null;
                clave=null;
            }

            else
            {
                valPago=extras.getString("valPago");
                numDocumento=extras.getString("numDocumento");
                numTransaccion=extras.getString("numTransaccion");
                clave=extras.getString("clave");
            }
        }

        context=this;

        vars=new vars();

        numberFormat = NumberFormat.getNumberInstance(Locale.GERMAN);
        botonRealizarPago=(Button)findViewById(R.id.botonRealizarPago);


        plazos=new ArrayList<String>();
        periodos=new ArrayList<String>();
        plazos.add("2 Meses");
        plazos.add("1 Mes");
        periodos.add("Quincenal");
        periodos.add("Mensual");

        editTextValorConfirmarPago=(TextView)findViewById(R.id.editTextValorConfirmarPago);
        editTextValorConfirmarPago.setText("$"+numberFormat.format(Double.parseDouble(valPago)));


        materialDesignSpinnerPlazos = (MaterialSpinner)
            findViewById(R.id.spinnerPlazoPago);

        spinnerPeriodoPago = (MaterialSpinner)
                findViewById(R.id.spinnerPeriodoPago);

        materialDesignSpinnerPlazos.setItems("Selecciona plazo","2 Meses", "1 Mes");
        materialDesignSpinnerPlazos.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>()
        {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item)
            {
                //Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                if(TextUtils.equals(item,"1 Mes"))
                {
                    codPlazo="1";
                }
                else
                {
                    codPlazo="2";
                }
            }
        });
        spinnerPeriodoPago.setItems("Selecciona frecuencia", "Quincenal", "Mensual");
        spinnerPeriodoPago.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                if(TextUtils.equals(item,"Quincenal"))
                {
                    codPeriodo="1";
                }
                else
                {
                    codPeriodo="2";
                }
            }
        });

        botonRealizarPago.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                WebServiceValidarPago(numDocumento,clave,numTransaccion,codPlazo,codPeriodo);
            }
        });






    }

    private boolean status=false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FormaPago.this,R.style.AlertDialogTheme));
            builder
                    .setTitle("Kupi")
                    .setMessage("¿Deseas cancelar la transacción ahora?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            finish();
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                }
            }).show();
        }

        return false;
    }


    private void WebServiceValidarPago(final String numDocumento, final String clave, final String numTransaccion, final String codPlazo, final String codPeriodo)
    {
        //WebService Datos de Pago
        progressDialog = new ProgressDialog(new ContextThemeWrapper(FormaPago.this,R.style.AppCompatAlertDialogStyle));
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Validando...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        String _urlWebService = vars.ipServer.concat("/ws/validatePay");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            String message=response.getString("message");
                            status=response.getBoolean("status");

                            if(status)//MUESTRA PANTALLA FORMA DE PAGO
                            {
                                if (!((Activity) context).isFinishing())
                                {
                                    if(progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();
                                    }
                                }


                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FormaPago.this,R.style.AlertDialogTheme));
                                builder
                                        .setTitle("Kupi")
                                        .setMessage(""+message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                finish();
                                            }
                                        }).setCancelable(false).show();

                            }
                            else//FORMA DE PAGO NORMAL
                            {
                                if (!((Activity) context).isFinishing())
                                {
                                    if(progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();
                                    }
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FormaPago.this,R.style.AlertDialogTheme));
                                builder
                                        .setTitle("Kupi")
                                        .setMessage(""+message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                            }
                                        }).show();


                            }


                        }
                        catch (JSONException e)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                if(progressDialog.isShowing())
                                {
                                    progressDialog.dismiss();
                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FormaPago.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FormaPago.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FormaPago.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FormaPago.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FormaPago.this,R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FormaPago.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FormaPago.this,R.style.AlertDialogTheme));
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
                headers.put("numDocumento", numDocumento);
                headers.put("clvUsuario", clave);
                headers.put("numTransaccion", numTransaccion);
                headers.put("codPlazo", codPlazo);
                headers.put("codFrecuencia", codPeriodo);
                headers.put("fcmToken", ""+FirebaseInstanceId.getInstance().getToken());
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "getDatosPago");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }



}
