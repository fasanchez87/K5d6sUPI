package com.ingeniapps.dicmax.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.ingeniapps.dicmax.helper.AlertasErrores;
import com.ingeniapps.dicmax.loader.SpinKitView;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity
{
    TextView editTextTerminos;
    Spanned Text;
    private String tokenFCM;
    private boolean checkTerminos;
    EditText editTextNombreUsuario,editTextApellidoUsuario,editTextTelUsuario,editTextEmailUsuario,editTextCedulaUsuario,editTextClave;
    TextInputLayout input_layout_nombre_usuario,input_layout_ape_usuario,input_layout_tel_usuario,input_layout_email_usuario,input_layout_cedula_usuario,input_layout_clave_usuario;
    private Button buttonIngresar,buttonIngresarDisable;
    SpinKitView spin_kit_registro;
    FontStylerView textViewIngresoInvitado;
    private String nombre,apellido,telefono,email,cedula,clave;
    private boolean aceptaTerminos=false;
    AlertasErrores alertarErrores;
    private boolean camposVacios=true;
    private vars vars;
    Context context;
    private ProgressDialog progressDialog;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Typeface copperplateGothicLight;



    CoordinatorLayout coordinatorLayoutRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        checkTerminos=false;
        vars=new vars();
        context=Registro.this;
        copperplateGothicLight = Typeface.createFromAsset(Registro.this.getAssets(), "fonts/AvenirLTStd-Light.ttf");

        input_layout_nombre_usuario=(TextInputLayout) findViewById(R.id.input_layout_nombre_usuario);
        input_layout_ape_usuario=(TextInputLayout) findViewById(R.id.input_layout_ape_usuario);
        input_layout_tel_usuario=(TextInputLayout) findViewById(R.id.input_layout_tel_usuario);
        input_layout_email_usuario=(TextInputLayout) findViewById(R.id.input_layout_email_usuario);
        input_layout_cedula_usuario=(TextInputLayout) findViewById(R.id.input_layout_cedula_usuario);
        input_layout_clave_usuario=(TextInputLayout) findViewById(R.id.input_layout_clave_usuario);

        input_layout_nombre_usuario.setTypeface(copperplateGothicLight);
        input_layout_ape_usuario.setTypeface(copperplateGothicLight);
        input_layout_tel_usuario.setTypeface(copperplateGothicLight);
        input_layout_email_usuario.setTypeface(copperplateGothicLight);
        input_layout_cedula_usuario.setTypeface(copperplateGothicLight);
        input_layout_clave_usuario.setTypeface(copperplateGothicLight);






        if(checkPlayServices())
        {
            if(!TextUtils.isEmpty(FirebaseInstanceId.getInstance().getToken()))
            {
                tokenFCM=FirebaseInstanceId.getInstance().getToken();
            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Registro.this,R.style.AlertDialogTheme));
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

        coordinatorLayoutRegistro=(CoordinatorLayout)findViewById(R.id.coordinatorLayoutRegistro);

        editTextTerminos=(TextView)findViewById(R.id.editTextTerminos);
        editTextTerminos.setTypeface(copperplateGothicLight);

        Text = Html.fromHtml("Click para ver terminos y condiciones. <br />" +
                "<a href='http://www.kupi.com.co/terms'>Acepto los términos y condiciones.</a>");
        editTextTerminos.setMovementMethod(LinkMovementMethod.getInstance());
        editTextTerminos.setText(Text);

        editTextNombreUsuario=(EditText)findViewById(R.id.editTextNombreUsuario);
        editTextNombreUsuario.addTextChangedListener(new GenericTextWatcher(editTextNombreUsuario));

        editTextApellidoUsuario=(EditText)findViewById(R.id.editTextApellidoUsuario);
        editTextApellidoUsuario.addTextChangedListener(new GenericTextWatcher(editTextApellidoUsuario));

        editTextTelUsuario=(EditText)findViewById(R.id.editTextTelUsuario);
        editTextTelUsuario.addTextChangedListener(new GenericTextWatcher(editTextTelUsuario));

        editTextEmailUsuario=(EditText)findViewById(R.id.editTextEmailUsuario);
        editTextEmailUsuario.addTextChangedListener(new GenericTextWatcher(editTextEmailUsuario));

        editTextCedulaUsuario=(EditText)findViewById(R.id.editTextCedulaUsuario);
        editTextCedulaUsuario.addTextChangedListener(new GenericTextWatcher(editTextCedulaUsuario));

        editTextClave=(EditText)findViewById(R.id.editTextClave);
        editTextClave.addTextChangedListener(new GenericTextWatcher(editTextClave));

        buttonIngresar=(Button)findViewById(R.id.buttonIngresar);
        buttonIngresarDisable=(Button)findViewById(R.id.buttonIngresarDisable);

        buttonIngresar.setTypeface(copperplateGothicLight);
        buttonIngresarDisable.setTypeface(copperplateGothicLight);

        editTextNombreUsuario.setTypeface(copperplateGothicLight);
        editTextApellidoUsuario.setTypeface(copperplateGothicLight);
        editTextTelUsuario.setTypeface(copperplateGothicLight);
        editTextEmailUsuario.setTypeface(copperplateGothicLight);
        editTextCedulaUsuario.setTypeface(copperplateGothicLight);
        editTextClave.setTypeface(copperplateGothicLight);


        spin_kit_registro=(SpinKitView)findViewById(R.id.spin_kit_registro);
        textViewIngresoInvitado=(FontStylerView)findViewById(R.id.textViewIngresoInvitado);

        buttonIngresar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                nombre=editTextNombreUsuario.getText().toString();
                apellido=editTextApellidoUsuario.getText().toString();
                telefono=editTextTelUsuario.getText().toString();
                email=editTextEmailUsuario.getText().toString();
                cedula=editTextCedulaUsuario.getText().toString();
                clave=editTextClave.getText().toString();
                WebServiceRegistroUsuario(nombre,apellido,telefono, email,cedula,clave,vars.codSistema);
            }
        });

    }

    public final static boolean isValidEmail(CharSequence target)
    {
        if (target == null)
        {
            return false;
        }
        else
        {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
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
        ControllerSingleton.getInstance().cancelPendingReq("registroUsuario");
    }

    public void checkEventTerminos(View v)
    {
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked())
        {
            aceptaTerminos=true;

            if(!camposVacios)
            {
                buttonIngresarDisable.setVisibility(View.GONE);
                buttonIngresar.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            aceptaTerminos=false;
            buttonIngresarDisable.setVisibility(View.VISIBLE);
            buttonIngresar.setVisibility(View.GONE);
            /*buttonUnableRegistroUsuario.setVisibility(View.VISIBLE);
            buttonRegistroUsuario.setVisibility(View.GONE);*/
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
            if(TextUtils.isEmpty(editTextNombreUsuario.getText()))
            {
                camposVacios=true;
                buttonIngresarDisable.setVisibility(View.VISIBLE);
                buttonIngresar.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(editTextApellidoUsuario.getText()))
            {
                camposVacios=true;
                buttonIngresarDisable.setVisibility(View.VISIBLE);
                buttonIngresar.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(editTextTelUsuario.getText()))
            {
                camposVacios=true;
                buttonIngresarDisable.setVisibility(View.VISIBLE);
                buttonIngresar.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(editTextEmailUsuario.getText()) || !isValidEmail(editTextEmailUsuario.getText()))
            {
                camposVacios=true;
                buttonIngresarDisable.setVisibility(View.VISIBLE);
                buttonIngresar.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(editTextCedulaUsuario.getText()))
            {
                camposVacios=true;
                buttonIngresarDisable.setVisibility(View.VISIBLE);
                buttonIngresar.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(editTextClave.getText()) || editTextClave.getText().toString().length()<4)
            {
                camposVacios=true;
                buttonIngresarDisable.setVisibility(View.VISIBLE);
                buttonIngresar.setVisibility(View.GONE);
                return;
            }

            camposVacios=false;

            if(!aceptaTerminos)
            {
                buttonIngresarDisable.setVisibility(View.VISIBLE);
                buttonIngresar.setVisibility(View.GONE);
            }

            if(aceptaTerminos)
            {
                buttonIngresarDisable.setVisibility(View.GONE);
                buttonIngresar.setVisibility(View.VISIBLE);
            }
        }
    }

    private void WebServiceRegistroUsuario(final String nomUsuario, final String apeUsuario, final String telUsuario, final String emaUsuario,
                                           final String numDocumento, final String claveUsuario, final String codSistema)
    {
        String _urlWebService=vars.ipServer.concat("/ws/insertUser");

        progressDialog = new ProgressDialog(new ContextThemeWrapper(Registro.this,R.style.AppCompatAlertDialogStyle));
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Un momento...");
        progressDialog.show();
        progressDialog.setCancelable(false);

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
                                    progressDialog.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Registro.this,R.style.AlertDialogTheme));
                                    builder
                                            .setTitle("Registro Usuario")
                                            .setMessage(""+message)
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

                            }
                            else
                            if (!status)
                            {
                                if (!((Activity) context).isFinishing())
                                {
                                    progressDialog.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Registro.this,R.style.AlertDialogTheme));
                                    builder
                                            .setTitle("Registro Usuario")
                                            .setMessage(""+message)
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id)
                                                {
                                                }
                                            }).setCancelable(true).show().getButton(DialogInterface.BUTTON_POSITIVE).
                                            setTextColor(getResources().getColor(R.color.colorPrimary));

                                }
                            }
                        }
                        catch (JSONException e)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Registro.this,R.style.AlertDialogTheme));
                                builder
                                        .setTitle("Registro Usuario")
                                        .setMessage(e.getMessage().toString())
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                            }
                                        }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                                        setTextColor(getResources().getColor(R.color.colorPrimary));
                                e.printStackTrace();
                            }
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
                                progressDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Registro.this,R.style.AlertDialogTheme));
                                builder
                                        .setTitle("Registro Usuario")
                                        .setMessage(error.getMessage().toString())
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                            }
                                        }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                                        setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                        }
                        else
                        if (error instanceof NoConnectionError)
                        {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Registro.this,R.style.AlertDialogTheme));
                            builder
                                    .setTitle("Registro Usuario")
                                    .setMessage(error.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                                    setTextColor(getResources().getColor(R.color.colorPrimary));
                        }

                        else

                        if (error instanceof AuthFailureError)
                        {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Registro.this,R.style.AlertDialogTheme));
                            builder
                                    .setTitle("Registro Usuario")
                                    .setMessage(error.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                                    setTextColor(getResources().getColor(R.color.colorPrimary));
                        }

                        else

                        if (error instanceof ServerError)
                        {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Registro.this,R.style.AlertDialogTheme));
                            builder
                                    .setTitle("Registro Usuario")
                                    .setMessage(error.getLocalizedMessage())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                                    setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                        else
                        if (error instanceof NetworkError)
                        {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Registro.this,R.style.AlertDialogTheme));
                            builder
                                    .setTitle("Registro Usuario")
                                    .setMessage(error.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                                    setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                        else
                        if (error instanceof ParseError)
                        {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Registro.this,R.style.AlertDialogTheme));
                            builder
                                    .setTitle("Registro Usuario")
                                    .setMessage(error.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                                    setTextColor(getResources().getColor(R.color.colorPrimary));
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
                headers.put("nomUsuario", nomUsuario);
                headers.put("apeUsuario", apeUsuario);
                headers.put("telUsuario", telUsuario);
                headers.put("emaUsuario", emaUsuario);
                headers.put("numDocumento", numDocumento);
                headers.put("claveUsuario", claveUsuario);
                headers.put("codSistema", codSistema);
                headers.put("fcmToken", ""+tokenFCM);
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "registroUsuario");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
