package com.ingeniapps.dicmax.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.finger.callback.FingerprintDialogCallback;
import com.ingeniapps.dicmax.finger.dialog.FingerprintDialog;
import com.ingeniapps.dicmax.fragment.Cuenta;
import com.ingeniapps.dicmax.helper.AlertasErrores;
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
    TextInputLayout input_layout_editTextNombreContacto;
    TextInputLayout input_layout_editTextNombreCvvvontacto;
    vars vars;

    EditText editTextCedulaPago;
    EditText editTextClavePago;
    private Context context;

    private ImageView imageFinderPago;

    Button botonConfirmarPago;
    Button botonConfirmarPagoDisable;
    private NumberFormat numberFormat;
    private ProgressDialog progressDialog;
    private LinearLayout llHuellaPago;

    String codEmpresa, nomEmpresa, refPago, valPago, numTransaccion, numDocumento, clave;
    gestionSharedPreferences gestionSharedPreferences;
    private Typeface copperplateGothicLight;
    private boolean isHuella;
    AlertasErrores alertarErrores;
    NestedScrollView coordinatorLayoutPago;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        copperplateGothicLight = Typeface.createFromAsset(Pago.this.getAssets(), "fonts/AvenirLTStd-Light.ttf");

        coordinatorLayoutPago=findViewById(R.id.coordinatorLayoutPago);



        context=this;

        vars=new vars();

        imageFinderPago=findViewById(R.id.imageFinderPago);
        llHuellaPago=findViewById(R.id.llHuellaPago);


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
                numDocumento=null;
                clave=null;
            }

            else
            {
                nomEmpresa=extras.getString("nomEmpresa");
                refPago=extras.getString("refPago");
                valPago=extras.getString("valPago");
                codEmpresa=extras.getString("codEmpresa");
                numTransaccion=extras.getString("numTransaccion");

                numDocumento=extras.getString("numDocumento");
                clave=extras.getString("clave");
            }
        }


        input_layout_editTextNombreContacto=(TextInputLayout) findViewById(R.id.input_layout_editTextNombreContacto);
        input_layout_editTextNombreCvvvontacto=(TextInputLayout) findViewById(R.id.input_layout_editTextNombreCvvvontacto);
        input_layout_editTextNombreContacto.setTypeface(copperplateGothicLight);
        input_layout_editTextNombreCvvvontacto.setTypeface(copperplateGothicLight);


        editTextNomEmpresa=(TextView)findViewById(R.id.editTextNomEmpresa);
        editTextNomEmpresa.setText(""+nomEmpresa);
        editTextReferenciaPago=(TextView)findViewById(R.id.editTextReferenciaPago);
        editTextReferenciaPago.setText(""+refPago);
        editTextValorPago=(TextView)findViewById(R.id.editTextValorPago);
        editTextValorPago.setText("$"+numberFormat.format(Double.parseDouble(valPago)));

        editTextCedulaPago=(EditText) findViewById(R.id.editTextCedulaPago);
        editTextCedulaPago.setText(TextUtils.isEmpty(gestionSharedPreferences.getString("cedulaPago"))?null:gestionSharedPreferences.getString("cedulaPago"));
        editTextClavePago=(EditText)findViewById(R.id.editTextClavePago);

        editTextCedulaPago.setTypeface(copperplateGothicLight);
        editTextClavePago.setTypeface(copperplateGothicLight);

        botonConfirmarPago=(Button) findViewById(R.id.botonConfirmarPago);
        botonConfirmarPagoDisable=(Button) findViewById(R.id.botonConfirmarPagoDisable);
        botonConfirmarPago.setTypeface(copperplateGothicLight);
        botonConfirmarPagoDisable.setTypeface(copperplateGothicLight);







        botonConfirmarPago.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                gestionSharedPreferences.putString("cedulaPago",""+editTextCedulaPago.getText().toString());
                botonConfirmarPago.setVisibility(View.GONE);
                botonConfirmarPagoDisable.setVisibility(View.VISIBLE);
                WebServiceValidarPago(editTextCedulaPago.getText().toString(),editTextClavePago.getText().toString(),numTransaccion);
            }
        });

        //set listeners
        editTextCedulaPago.addTextChangedListener(textWatcher);
        editTextClavePago.addTextChangedListener(textWatcher);

        // run once to disable if empty
        checkFieldsForEmptyValues();

        if(gestionSharedPreferences.getBoolean("isHuella"))
        {
            if(FingerprintDialog.isAvailable(Pago.this))
            {
                llHuellaPago.setVisibility(View.VISIBLE);
            }

        }

        imageFinderPago.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(FingerprintDialog.isAvailable(Pago.this))
                {
                     FingerprintDialog.initialize(Pago.this)
                            .title(R.string.fingerprint_title)
                            .message(R.string.fingerprint_message)
                            .callback(new FingerprintDialogCallback()
                            {
                                @Override
                                public void onAuthenticationSucceeded()
                                {
                                    if (!((Activity) context).isFinishing())
                                    {
                                        if(TextUtils.isEmpty(editTextCedulaPago.getText().toString()))
                                        {
                                            alertarErrores=new AlertasErrores("Digite su cédula por favor.", coordinatorLayoutPago,Pago.this);
                                            return;
                                        }

                                        if(!gestionSharedPreferences.getBoolean("isHuella"))
                                        {
                                            if(TextUtils.isEmpty(editTextClavePago.getText().toString()))
                                            {
                                                alertarErrores=new AlertasErrores("Digite su contraseña por favor.", coordinatorLayoutPago,Pago.this);
                                                return;
                                            }
                                        }

                                        gestionSharedPreferences.putString("cedulaPago",""+editTextCedulaPago.getText().toString());
                                        botonConfirmarPago.setVisibility(View.GONE);
                                        botonConfirmarPagoDisable.setVisibility(View.VISIBLE);
                                        WebServiceValidarPago(editTextCedulaPago.getText().toString(),
                                                TextUtils.isEmpty(gestionSharedPreferences.getString("clave"))?editTextClavePago.getText().toString():gestionSharedPreferences.getString("clave"),numTransaccion);
                                    }
                                }
                                @Override
                                public void onAuthenticationCancel()
                                {
                                    //Toast.makeText(Login.this, "Fail", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
            }
        });
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
                            boolean status=response.getBoolean("status");
                            requireDetailPay=response.getBoolean("requireDetailPay");

                            /*if(requireDetailPay)//MUESTRA PANTALLA FORMA DE PAGO
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
                            }*/
                            if(!status)
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
                                                botonConfirmarPago.setVisibility(View.VISIBLE);
                                                botonConfirmarPagoDisable.setVisibility(View.GONE);
                                            }
                                        }).setCancelable(false).show();
                            }
                            else
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

                            botonConfirmarPago.setVisibility(View.VISIBLE);
                            botonConfirmarPagoDisable.setVisibility(View.GONE);

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

                            botonConfirmarPago.setVisibility(View.VISIBLE);
                            botonConfirmarPagoDisable.setVisibility(View.GONE);


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

                            botonConfirmarPago.setVisibility(View.VISIBLE);
                            botonConfirmarPagoDisable.setVisibility(View.GONE);


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

                            botonConfirmarPago.setVisibility(View.VISIBLE);
                            botonConfirmarPagoDisable.setVisibility(View.GONE);


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

                            botonConfirmarPago.setVisibility(View.VISIBLE);
                            botonConfirmarPagoDisable.setVisibility(View.GONE);


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

                            botonConfirmarPago.setVisibility(View.VISIBLE);
                            botonConfirmarPagoDisable.setVisibility(View.GONE);

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

                            botonConfirmarPago.setVisibility(View.VISIBLE);
                            botonConfirmarPagoDisable.setVisibility(View.GONE);


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
