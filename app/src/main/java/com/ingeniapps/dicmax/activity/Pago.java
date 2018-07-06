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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Pago extends AppCompatActivity
{

    TextView editTextNomEmpresa;
    TextView editTextReferenciaPago;
    TextView editTextValorPago;
    vars vars;

    EditText editTextCedulaPago;
    EditText editTextClavePago;
    private Context context;

    Button botonConfirmarPago;
    Button botonConfirmarPagoDisable;
    private NumberFormat numberFormat;
    private ProgressDialog progressDialog;

    String codEmpresa, nomEmpresa, refPago, valPago, numTransaccion;
    gestionSharedPreferences gestionSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        context=this;

        vars=new vars();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //this line shows back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        numberFormat = NumberFormat.getNumberInstance(Locale.GERMAN);


        gestionSharedPreferences=new gestionSharedPreferences(this);


        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                finish();
            }

        });

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras == null)
            {
                nomEmpresa=null;
                refPago=null;
                valPago=null;
                codEmpresa=null;
                numTransaccion=null;
            }

            else
            {
                nomEmpresa=extras.getString("nomEmpresa");
                refPago=extras.getString("refPago");
                valPago=extras.getString("valPago");
                codEmpresa=extras.getString("codEmpresa");
                numTransaccion=extras.getString("numTransaccion");
            }
        }


        editTextNomEmpresa=(TextView)findViewById(R.id.editTextNomEmpresa);
        editTextNomEmpresa.setText(""+nomEmpresa);
        editTextReferenciaPago=(TextView)findViewById(R.id.editTextReferenciaPago);
        editTextReferenciaPago.setText(""+refPago);
        editTextValorPago=(TextView)findViewById(R.id.editTextValorPago);
        editTextValorPago.setText("$"+numberFormat.format(Double.parseDouble(valPago)));

        editTextCedulaPago=(EditText) findViewById(R.id.editTextCedulaPago);
        editTextCedulaPago.setText(TextUtils.isEmpty(gestionSharedPreferences.getString("cedulaPago"))?null:gestionSharedPreferences.getString("cedulaPago"));
        editTextClavePago=(EditText)findViewById(R.id.editTextClavePago);



        botonConfirmarPago=(Button) findViewById(R.id.botonConfirmarPago);
        botonConfirmarPagoDisable=(Button) findViewById(R.id.botonConfirmarPagoDisable);
        botonConfirmarPago.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                gestionSharedPreferences.putString("cedulaPago",""+editTextCedulaPago.getText().toString());
                WebServiceValidarPago(editTextCedulaPago.getText().toString(),editTextClavePago.getText().toString(),numTransaccion);
            }
        });

        //set listeners
        editTextCedulaPago.addTextChangedListener(textWatcher);
        editTextClavePago.addTextChangedListener(textWatcher);

        // run once to disable if empty
        checkFieldsForEmptyValues();










    }

    //TextWatcher
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private  void checkFieldsForEmptyValues()
    {

        String s1 = editTextCedulaPago.getText().toString();
        String s2 = editTextClavePago.getText().toString();

        if(s1.equals("") && s2.equals(""))
        {
            botonConfirmarPago.setVisibility(View.GONE);
            botonConfirmarPagoDisable.setVisibility(View.VISIBLE);
        }

        else if(!s1.equals("")&&s2.equals("")){
            botonConfirmarPago.setVisibility(View.GONE);
            botonConfirmarPagoDisable.setVisibility(View.VISIBLE);
        }

        else if(!s2.equals("")&&s1.equals(""))
        {
            botonConfirmarPago.setVisibility(View.GONE);
            botonConfirmarPagoDisable.setVisibility(View.VISIBLE);
        }

        else
        {
            botonConfirmarPago.setVisibility(View.VISIBLE);
            botonConfirmarPagoDisable.setVisibility(View.GONE);
        }
    }


    private boolean requireDetailPay=false;

    private void WebServiceValidarPago(final String numDocumento, final String clave, final String numTransaccion)
    {
        //WebService Datos de Pago
        progressDialog = new ProgressDialog(new ContextThemeWrapper(Pago.this,R.style.AppCompatAlertDialogStyle));
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
                            requireDetailPay=response.getBoolean("requireDetailPay");

                            if(requireDetailPay)//MUESTRA PANTALLA FORMA DE PAGO
                            {
                                if (!((Activity) context).isFinishing())
                                {
                                    if(progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();
                                    }
                                }

                                Intent i=new Intent(Pago.this, FormaPago.class);
                                i.putExtra("valPago",""+valPago);
                                i.putExtra("numDocumento",""+numDocumento);
                                i.putExtra("numTransaccion",""+numTransaccion);
                                i.putExtra("clave",""+clave);
                                startActivity(i);
                                finish();
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

                                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pago.this,R.style.AlertDialogTheme));
                                    builder
                                            .setTitle("Kupi")
                                            .setMessage(""+message)
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id)
                                                {
                                                    Pago.this.finish();
                                                }
                                            }).setCancelable(false).show();


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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pago.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pago.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pago.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pago.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pago.this,R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pago.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Pago.this,R.style.AlertDialogTheme));
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
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "getDatosPago");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
