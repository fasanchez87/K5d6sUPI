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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.Text.FontStylerView;
import com.ingeniapps.dicmax.finger.callback.FingerprintCallback;
import com.ingeniapps.dicmax.finger.callback.FingerprintDialogCallback;
import com.ingeniapps.dicmax.finger.dialog.FingerprintDialog;
import com.ingeniapps.dicmax.helper.AlertasErrores;
import com.ingeniapps.dicmax.loader.SpinKitView;
import com.ingeniapps.dicmax.loader.SpriteFactory;
import com.ingeniapps.dicmax.loader.Tipo_Loader;
import com.ingeniapps.dicmax.loader.sprite.Sprite;
import com.ingeniapps.dicmax.volley.ControllerSingleton;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements FingerprintDialogCallback
{

    vars vars;
    Button buttonIngresar;
    Button buttonIngresarDisable;
    TextInputLayout input_layout_usuario;
    TextInputLayout input_layout_clave;
    EditText editTextUsuario;
    FontStylerView textViewOlvidoClave;
    EditText editTextClave;
    SpinKitView spinKitView;
    LinearLayout linearLoading,llHuellaLogin;
    private Typeface copperplateGothicLight;
    CoordinatorLayout coordinatorLayoutLogin;
    AlertasErrores alertarErrores;
    gestionSharedPreferences gestionSharedPreferences;
    private String tokenFCM;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Context context;
    private String usuario, clave;
    private String tipoIngreso;
    private ImageView imageFinderLogin;
    private Boolean isHuella;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras == null)
            {
                tipoIngreso=null;
            }

            else
            {
                tipoIngreso=extras.getString("tipoIngreso");
            }
        }

        Log.i("tipoIngreso","invitado");


        //Estilo tipo letra boton.
        buttonIngresar=findViewById(R.id.buttonIngresar);
        imageFinderLogin=findViewById(R.id.imageFinderLogin);
        linearLoading=(LinearLayout) findViewById(R.id.linearLoading);
        llHuellaLogin= findViewById(R.id.llHuellaLogin);
        buttonIngresarDisable=(Button)findViewById(R.id.buttonIngresarDisable);
        copperplateGothicLight = Typeface.createFromAsset(getAssets(), "fonts/AvenirLTStd-Light.ttf");
        buttonIngresar.setTypeface(copperplateGothicLight);
        buttonIngresarDisable.setTypeface(copperplateGothicLight);
        context=this;

        gestionSharedPreferences=new gestionSharedPreferences(this);


        vars=new vars();

        coordinatorLayoutLogin=(CoordinatorLayout)findViewById(R.id.coordinatorLayoutLogin);

        input_layout_usuario=(TextInputLayout)findViewById(R.id.input_layout_usuario);
        input_layout_clave=(TextInputLayout)findViewById(R.id.input_layout_clave);
        editTextUsuario=(EditText)findViewById(R.id.editTextUsuario);
        textViewOlvidoClave=(FontStylerView)findViewById(R.id.textViewOlvidoClave);
        textViewOlvidoClave.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(Login.this, ValidarUsuario.class);
                startActivity(intent);
                //finish();
            }
        });

        editTextUsuario.addTextChangedListener(new GenericTextWatcher(editTextUsuario));
        input_layout_usuario.setTypeface(copperplateGothicLight);
        editTextUsuario.setTypeface(copperplateGothicLight);

        input_layout_clave=(TextInputLayout)findViewById(R.id.input_layout_clave);
        editTextClave=(EditText)findViewById(R.id.editTextClave);
        editTextClave.addTextChangedListener(new GenericTextWatcher(editTextClave));
        input_layout_clave.setTypeface(copperplateGothicLight);
        editTextClave.setTypeface(copperplateGothicLight);

        //SI TIENE HUELLA REGISTRADA DEJAMOS LO DEJAMOS PINTADO EN PANTALLA
        if(gestionSharedPreferences.getBoolean("isHuella"))
        {
            editTextUsuario.setText(gestionSharedPreferences.getString("numDocumento"));
        }


        if(FingerprintDialog.isAvailable(Login.this))
        {
            llHuellaLogin.setVisibility(View.VISIBLE);
        }

        imageFinderLogin.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(FingerprintDialog.isAvailable(Login.this))
                {
                    if (!FingerprintDialog.isEnrollFinger(Login.this))
                    {
                        alertarErrores=new AlertasErrores("No se ha detectado una huella enrolada, por favor agregala en tu teléfono.", coordinatorLayoutLogin,Login.this);
                        return;
                    }

                    FingerprintDialog.initialize(Login.this)
                            .title(R.string.fingerprint_title)
                            .message(R.string.fingerprint_message)
                            .callback(new FingerprintDialogCallback()
                            {
                                @Override
                                public void onAuthenticationSucceeded()
                                {

                                    if (!((Activity) context).isFinishing())
                                    {
                                        if(TextUtils.isEmpty(editTextUsuario.getText().toString()))
                                        {
                                            alertarErrores=new AlertasErrores("Digite su cédula por favor.", coordinatorLayoutLogin,Login.this);
                                            return;
                                        }

                                        //Toast.makeText(context, "clave: "+gestionSharedPreferences.getString("clave"), Toast.LENGTH_SHORT).show();

                                        if(!gestionSharedPreferences.getBoolean("isHuella"))
                                        {
                                            if(TextUtils.isEmpty(editTextClave.getText().toString()))
                                            {
                                                alertarErrores=new AlertasErrores("Digite su contraseña por favor.", coordinatorLayoutLogin,Login.this);
                                                return;
                                            }
                                        }


                                        //Toast.makeText(Login.this, "Ok", Toast.LENGTH_SHORT).show();
                                        usuario=editTextUsuario.getText().toString();
                                        clave=editTextClave.getText().toString();
                                        //gestionSharedPreferences.putBoolean("isHuella",true);
                                        WebServiceLogin(usuario,TextUtils.isEmpty(gestionSharedPreferences.getString("clave"))?clave:gestionSharedPreferences.getString("clave"));
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





        //Configuramos Loader
        spinKitView = (SpinKitView) findViewById(R.id.spin_kit_login);
        Tipo_Loader style = Tipo_Loader.values()[2];
        Sprite drawable = SpriteFactory.create(style);
        spinKitView.setIndeterminateDrawable(drawable);

        if(checkPlayServices())
        {
            if(!TextUtils.isEmpty(FirebaseInstanceId.getInstance().getToken()))
            {
                tokenFCM=FirebaseInstanceId.getInstance().getToken();
            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Login.this,R.style.AlertDialogTheme));
            builder
                    .setTitle("Google Play Services")
                    .setMessage("Se ha encontrado un error con los servicios de Google Play, actualizalo y vuelve a ingresar.")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            finish();
                        }
                    }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                    setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        buttonIngresar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                usuario=editTextUsuario.getText().toString();
                clave=editTextClave.getText().toString();
                gestionSharedPreferences.clear();
                WebServiceLogin(usuario,clave);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
                Intent i=new Intent(Login.this, Prelogin.class);
                startActivity(i);
                finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAuthenticationSucceeded() {
        // Logic when fingerprint is recognized
    }

    @Override
    public void onAuthenticationCancel() {
        // Logic when user canceled operation
    }

    private boolean checkPlayServices()
    {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS)
        {
            if(googleAPI.isUserResolvableError(result))
            {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ControllerSingleton.getInstance().cancelPendingReq("login");
    }

    private void WebServiceLogin(final String numDocumento, final String claveUsuario)
    {
        String _urlWebService=vars.ipServer.concat("/ws/login");

        buttonIngresar.setVisibility(View.GONE);
        buttonIngresarDisable.setVisibility(View.VISIBLE);
        linearLoading.setVisibility(View.VISIBLE);
        editTextUsuario.setEnabled(false);
        editTextClave.setEnabled(false);
        textViewOlvidoClave.setEnabled(false);

        JsonObjectRequest jsonObjReq=new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            boolean status=response.getBoolean("status");
                            String message=response.getString("message");

                            if(status)
                            {
                                gestionSharedPreferences.putBoolean("isHuella",true);


                                //OBTENEMOS DATOS DEL USUARIO PARA GUARDAR SU SESION
                                gestionSharedPreferences.putString("codUsuario",""+response.getJSONObject("usuario").getString("codUsuario"));
                                gestionSharedPreferences.putBoolean("GuardarSesion", true);
                               /* //GUARDAMOS LA CLAVE EN CASO DE LA HUELLA GUARDADA
                                if(gestionSharedPreferences.getBoolean("isHuella"))
                                {
                                    gestionSharedPreferences.putString("clave",""+editTextClave.getText().toString());
                                }*/

                                gestionSharedPreferences.putString("codCiudad",""+response.getJSONObject("usuario").getString("codCiudad"));//CIUDAD INICIAL DEL USUARIO
                                gestionSharedPreferences.putString("nomCiudad",""+response.getJSONObject("usuario").getString("nomCiudad"));//CIUDAD INICIAL DEL USUARIO
                                gestionSharedPreferences.putString("nomUsuario",""+response.getJSONObject("usuario").getString("nomUsuario"));
                                gestionSharedPreferences.putString("apeUsuario",""+response.getJSONObject("usuario").getString("apeUsuario"));

                                gestionSharedPreferences.putString("numDocumento",""+response.getJSONObject("usuario").getString("numDocumento"));
                                gestionSharedPreferences.putString("clave",""+claveUsuario);

                                gestionSharedPreferences.putString("emaUsuario",""+response.getJSONObject("usuario").getString("emaUsuario"));
                                gestionSharedPreferences.putString("urlImagen",""+response.getJSONObject("usuario").getString("urlImagen"));//Imagen Usuario
                                gestionSharedPreferences.putString("telUsuario",""+response.getJSONObject("usuario").getString("telUsuario"));//Imagen Usuario*/

                                if (!((Activity) context).isFinishing())
                                {
                                    Intent intent=new Intent(Login.this,Inicio.class);
                                    intent.putExtra("codCiudad",response.getJSONObject("usuario").getString("codCiudad"));
                                    intent.putExtra("numDocumento",editTextUsuario.getText().toString());
                                    intent.putExtra("claveUsuario",TextUtils.isEmpty(editTextClave.getText().toString())?gestionSharedPreferences.getString("clave"):editTextClave.getText().toString());
                                    startActivity(intent);
                                    finish();
                                    return;
                                }
                            }
                            else
                            {
                                if (!((Activity) context).isFinishing())
                                {
                                    alertarErrores=new AlertasErrores(""+message, coordinatorLayoutLogin,Login.this);
                                    buttonIngresar.setVisibility(View.VISIBLE);
                                    buttonIngresarDisable.setVisibility(View.GONE);
                                    linearLoading.setVisibility(View.INVISIBLE);
                                    editTextUsuario.setEnabled(true);
                                    editTextClave.setEnabled(true);
                                    textViewOlvidoClave.setEnabled(true);
                                }
                            }
                        }
                        catch (JSONException e)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores(""+e.getMessage().toString(), coordinatorLayoutLogin,Login.this);
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
                                alertarErrores=new AlertasErrores(""+error.getMessage().toString(), coordinatorLayoutLogin,Login.this);
                            }
                        }
                        else
                        if (error instanceof NoConnectionError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores(""+error.getMessage().toString(), coordinatorLayoutLogin,Login.this);
                            }
                        }

                        else

                        if (error instanceof AuthFailureError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores(""+error.getMessage().toString(), coordinatorLayoutLogin,Login.this);
                            }
                        }

                        else

                        if (error instanceof ServerError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores(""+error.getMessage().toString(), coordinatorLayoutLogin,Login.this);
                            }
                        }
                        else
                        if (error instanceof NetworkError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores(""+error.getMessage().toString(), coordinatorLayoutLogin,Login.this);
                            }
                        }
                        else
                        if (error instanceof ParseError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores(""+error.getMessage().toString(), coordinatorLayoutLogin,Login.this);
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
                headers.put("numDocumento", numDocumento);
                headers.put("claveUsuario", claveUsuario);
                headers.put("fcmToken", ""+tokenFCM);
                headers.put("codSistema", ""+vars.codSistema);
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "login");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
            if(TextUtils.isEmpty(editTextUsuario.getText()))
            {
                buttonIngresarDisable.setVisibility(View.VISIBLE);
                buttonIngresar.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(editTextClave.getText()))
            {
                buttonIngresarDisable.setVisibility(View.VISIBLE);
                buttonIngresar.setVisibility(View.GONE);
                return;
            }

            buttonIngresarDisable.setVisibility(View.GONE);
            buttonIngresar.setVisibility(View.VISIBLE);
        }
    }
}
