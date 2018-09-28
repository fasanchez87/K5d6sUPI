package com.ingeniapps.dicmax.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.helper.AlertasErrores;
import com.ingeniapps.dicmax.volley.ControllerSingleton;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RecargasElectronicas extends AppCompatActivity
{

    private EditText editTextOperador,editTextTelefono,editTextValorRecarga,editTextCedulaRecarga,editTextValorClaveRecarga;
    private TextInputLayout input_layout_operador,input_layout_telefono,input_layout_valor_recarga,input_layout_cedula_recarga,input_layout_clave_recarga;
    private String codCiudad, nomCiudad,dirRecogida,latRecogida,lonRecogida,latCiudad,lonCiudad,codPostal, codOperador, nomOperador;
    private Button buttonRecargarEnable,buttonRecargarDisable;
    private Typeface copperplateGothicLight;
    private NumberFormat numberFormat=NumberFormat.getNumberInstance(Locale.GERMAN);
    private vars vars;
    private ProgressDialog progressDialog;
    private LinearLayout linearLoadingRecarga;
    private Context context;
    AlertasErrores alertarErrores;
    private NestedScrollView scrollRecargas;
    private gestionSharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recargas_electronicas);
        copperplateGothicLight = Typeface.createFromAsset(getAssets(), "fonts/AvenirLTStd-Light.ttf");
        sharedPreferences=new gestionSharedPreferences(RecargasElectronicas.this);

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

        vars=new vars();
        context=RecargasElectronicas.this;

        buttonRecargarEnable=(Button) findViewById(R.id.buttonRecargarEnable);
        scrollRecargas=(NestedScrollView) findViewById(R.id.scrollRecargas);
        linearLoadingRecarga=(LinearLayout) findViewById(R.id.linearLoadingRecarga);
        buttonRecargarEnable.setTypeface(copperplateGothicLight);

        buttonRecargarDisable=(Button) findViewById(R.id.buttonRecargarDisable);
        buttonRecargarDisable.setTypeface(copperplateGothicLight);

        input_layout_operador=(TextInputLayout) findViewById(R.id.input_layout_operador);
        input_layout_operador.setTypeface(copperplateGothicLight);
        input_layout_telefono=(TextInputLayout) findViewById(R.id.input_layout_telefono);
        //input_layout_telefono.setHintEnabled(false);
        input_layout_telefono.setTypeface(copperplateGothicLight);
        input_layout_valor_recarga=(TextInputLayout) findViewById(R.id.input_layout_valor_recarga);
        input_layout_valor_recarga.setTypeface(copperplateGothicLight);
        input_layout_cedula_recarga=(TextInputLayout) findViewById(R.id.input_layout_cedula_recarga);
        input_layout_cedula_recarga.setTypeface(copperplateGothicLight);
        input_layout_clave_recarga= findViewById(R.id.input_layout_clave_recarga);
        input_layout_clave_recarga.setTypeface(copperplateGothicLight);

        editTextOperador=(EditText)findViewById(R.id.editTextOperador);
        editTextOperador.setTypeface(copperplateGothicLight);
        editTextOperador.addTextChangedListener(new GenericTextWatcher(editTextOperador));
        editTextOperador.setFocusable(false);
        editTextOperador.setClickable(true);
        editTextOperador.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(RecargasElectronicas.this, OperadoresRecargas.class);
                startActivityForResult(intent,1);
            }
        });


        editTextTelefono=(EditText)findViewById(R.id.editTextTelefono);
       // editTextTelefono.setCursorVisible(false);
        editTextTelefono.setTypeface(copperplateGothicLight);
        editTextTelefono.addTextChangedListener(new GenericTextWatcher(editTextOperador));


        editTextValorRecarga=(EditText)findViewById(R.id.editTextValorRecarga);
        editTextValorRecarga.setTypeface(copperplateGothicLight);
        editTextValorRecarga.addTextChangedListener(new GenericTextWatcher(editTextOperador));


        editTextCedulaRecarga=(EditText)findViewById(R.id.editTextCedulaRecarga);
        editTextCedulaRecarga.setText(sharedPreferences.getString("numDocumento"));
        editTextCedulaRecarga.setTypeface(copperplateGothicLight);
        editTextCedulaRecarga.addTextChangedListener(new GenericTextWatcher(editTextOperador));


        editTextValorClaveRecarga=(EditText)findViewById(R.id.editTextValorClaveRecarga);
        editTextValorClaveRecarga.setTypeface(copperplateGothicLight);
        editTextValorClaveRecarga.addTextChangedListener(new GenericTextWatcher(editTextOperador));

        buttonRecargarEnable.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new android.view.ContextThemeWrapper(RecargasElectronicas.this,R.style.AlertDialogTheme));
                builder
                        .setTitle("Confirmación Recarga")
                        .setMessage("¿ Estás seguro de recargar el valor de "+numberFormat.format(Double.parseDouble(editTextValorRecarga.getText().toString()))+" ?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                WebServiceRecargar();

                                                      }
                        }) .setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                }).setCancelable(true).show().getButton(DialogInterface.BUTTON_POSITIVE).
                        setTextColor(getResources().getColor(R.color.colorPrimary));


            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                codOperador = data.getStringExtra("codOperador");
                nomOperador = data.getStringExtra("nomOperador");
                input_layout_telefono.setHintEnabled(true);
                editTextTelefono.setCursorVisible(true);
                editTextOperador.setText(nomOperador);
            }

            if (resultCode == Activity.RESULT_CANCELED)
            {
                //Toast.makeText(RecargasElectronicas.this, "Cancel: ", Toast.LENGTH_LONG).show();
            }
        }

    }

    private class GenericTextWatcher implements TextWatcher
    {
        private View view;

        private GenericTextWatcher(View view)
        {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable)
        {
            if(TextUtils.isEmpty(editTextOperador.getText()))
            {
                buttonRecargarDisable.setVisibility(View.VISIBLE);
                buttonRecargarEnable.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(editTextTelefono.getText()))
            {
                buttonRecargarDisable.setVisibility(View.VISIBLE);
                buttonRecargarEnable.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(editTextValorRecarga.getText()))
            {
                buttonRecargarDisable.setVisibility(View.VISIBLE);
                buttonRecargarEnable.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(editTextCedulaRecarga.getText()))
            {
                buttonRecargarDisable.setVisibility(View.VISIBLE);
                buttonRecargarEnable.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(editTextValorClaveRecarga.getText()))
            {
                buttonRecargarDisable.setVisibility(View.VISIBLE);
                buttonRecargarEnable.setVisibility(View.GONE);
                return;
            }

            buttonRecargarDisable.setVisibility(View.GONE);
            buttonRecargarEnable.setVisibility(View.VISIBLE);
        }
    }

    private void WebServiceRecargar()
    {
        String _urlWebService=vars.ipServer.concat("/ws/doRecharge");

        buttonRecargarEnable.setVisibility(View.GONE);
        buttonRecargarDisable.setVisibility(View.VISIBLE);
        linearLoadingRecarga.setVisibility(View.VISIBLE);
        editTextOperador.setEnabled(false);
        editTextTelefono.setEnabled(false);
        editTextValorRecarga.setEnabled(false);
        editTextCedulaRecarga.setEnabled(false);
        editTextValorClaveRecarga.setEnabled(false);

        JsonObjectRequest jsonObjReq=new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status=response.getBoolean("status");
                            String message=response.getString("message");

                            if(status)
                            {
                                if (!((Activity) context).isFinishing())
                                {
                                    buttonRecargarEnable.setVisibility(View.GONE);
                                    buttonRecargarDisable.setVisibility(View.VISIBLE);
                                    linearLoadingRecarga.setVisibility(View.INVISIBLE);

                                    editTextOperador.setEnabled(false);
                                    editTextTelefono.setEnabled(false);
                                    editTextValorRecarga.setEnabled(false);
                                    editTextCedulaRecarga.setEnabled(false);
                                    editTextValorClaveRecarga.setEnabled(false);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(RecargasElectronicas.this,R.style.AlertDialogTheme));
                                    builder
                                            .setTitle("Estado Recarga")
                                            .setMessage(message)
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id)
                                                {
                                                    finish();
                                                }
                                            }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                                            setTextColor(getResources().getColor(R.color.colorPrimary));
                                    return;
                                }
                            }
                            else
                            {
                                if (!((Activity) context).isFinishing())
                                {
                                    alertarErrores=new AlertasErrores(""+message, linearLoadingRecarga,RecargasElectronicas.this);

                                    buttonRecargarEnable.setVisibility(View.VISIBLE);
                                    buttonRecargarDisable.setVisibility(View.GONE);
                                    linearLoadingRecarga.setVisibility(View.INVISIBLE);

                                    editTextOperador.setEnabled(true);
                                    editTextTelefono.setEnabled(true);
                                    editTextValorRecarga.setEnabled(true);
                                    editTextCedulaRecarga.setEnabled(true);
                                    editTextValorClaveRecarga.setEnabled(true);
                                }
                            }

                        }
                        catch (JSONException e)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores(""+e.getMessage().toString(), scrollRecargas,RecargasElectronicas.this);

                                buttonRecargarEnable.setVisibility(View.VISIBLE);
                                buttonRecargarDisable.setVisibility(View.GONE);
                                linearLoadingRecarga.setVisibility(View.INVISIBLE);
                                editTextOperador.setEnabled(true);
                                editTextTelefono.setEnabled(true);
                                editTextValorRecarga.setEnabled(true);
                                editTextCedulaRecarga.setEnabled(true);
                                editTextValorClaveRecarga.setEnabled(true);
                            }
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
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores("Tiempo espera conexión agotado, intenta de nuevo.", linearLoadingRecarga,RecargasElectronicas.this);
                                buttonRecargarEnable.setVisibility(View.VISIBLE);
                                buttonRecargarDisable.setVisibility(View.GONE);
                                linearLoadingRecarga.setVisibility(View.INVISIBLE);
                                editTextOperador.setEnabled(true);
                                editTextTelefono.setEnabled(true);
                                editTextValorRecarga.setEnabled(true);
                                editTextCedulaRecarga.setEnabled(true);
                                editTextValorClaveRecarga.setEnabled(true);

                            }
                        }
                        else
                        if (error instanceof NoConnectionError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores("Error conexión datos/internet, intenta de nuevo.", linearLoadingRecarga,RecargasElectronicas.this);
                                buttonRecargarEnable.setVisibility(View.VISIBLE);
                                buttonRecargarDisable.setVisibility(View.GONE);
                                linearLoadingRecarga.setVisibility(View.INVISIBLE);
                                editTextOperador.setEnabled(true);
                                editTextTelefono.setEnabled(true);
                                editTextValorRecarga.setEnabled(true);
                                editTextCedulaRecarga.setEnabled(true);
                                editTextValorClaveRecarga.setEnabled(true);
                            }
                        }
                        else
                        if (error instanceof AuthFailureError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores("Error autorización de petición, intenta de nuevo o contactanos.", linearLoadingRecarga,RecargasElectronicas.this);
                                buttonRecargarEnable.setVisibility(View.VISIBLE);
                                buttonRecargarDisable.setVisibility(View.GONE);
                                linearLoadingRecarga.setVisibility(View.INVISIBLE);
                                editTextOperador.setEnabled(true);
                                editTextTelefono.setEnabled(true);
                                editTextValorRecarga.setEnabled(true);
                                editTextCedulaRecarga.setEnabled(true);
                                editTextValorClaveRecarga.setEnabled(true);
                            }
                        }
                        else
                        if (error instanceof ServerError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores("Error respuesta servidor, intenta de nuevo o contactanos.", linearLoadingRecarga,RecargasElectronicas.this);
                                buttonRecargarEnable.setVisibility(View.VISIBLE);
                                buttonRecargarDisable.setVisibility(View.GONE);
                                linearLoadingRecarga.setVisibility(View.INVISIBLE);
                                editTextOperador.setEnabled(true);
                                editTextTelefono.setEnabled(true);
                                editTextValorRecarga.setEnabled(true);
                                editTextCedulaRecarga.setEnabled(true);
                                editTextValorClaveRecarga.setEnabled(true);
                            }
                        }
                        else
                        if (error instanceof NetworkError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores("Error red de conexión, intenta de nuevo o contactanos.", linearLoadingRecarga,RecargasElectronicas.this);
                                buttonRecargarEnable.setVisibility(View.VISIBLE);
                                buttonRecargarDisable.setVisibility(View.GONE);
                                linearLoadingRecarga.setVisibility(View.INVISIBLE);
                                editTextOperador.setEnabled(true);
                                editTextTelefono.setEnabled(true);
                                editTextValorRecarga.setEnabled(true);
                                editTextCedulaRecarga.setEnabled(true);
                                editTextValorClaveRecarga.setEnabled(true);
                            }
                        }
                        else
                        if (error instanceof ParseError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores("Error codificación respuesta servidor, intenta de nuevo o contactanos.", linearLoadingRecarga,RecargasElectronicas.this);
                                buttonRecargarEnable.setVisibility(View.VISIBLE);
                                buttonRecargarDisable.setVisibility(View.GONE);
                                linearLoadingRecarga.setVisibility(View.INVISIBLE);
                                editTextOperador.setEnabled(true);
                                editTextTelefono.setEnabled(true);
                                editTextValorRecarga.setEnabled(true);
                                editTextCedulaRecarga.setEnabled(true);
                                editTextValorClaveRecarga.setEnabled(true);
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
                headers.put("numDocumento", sharedPreferences.getString("numDocumento") );
                headers.put("codOperador", ""+codOperador );
                headers.put("numCelular", ""+editTextTelefono.getText() );
                headers.put("numTransaccion", "1");
                headers.put("valRecarga", ""+editTextValorRecarga.getText() );
                headers.put("clvUsuario", ""+editTextValorClaveRecarga.getText() );
                headers.put("fcmToken", ""+FirebaseInstanceId.getInstance().getToken());
                headers.put("codSistema", ""+vars.codSistema);
                return headers;
            }

        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "VALIDATE_USUARIO");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }






}
