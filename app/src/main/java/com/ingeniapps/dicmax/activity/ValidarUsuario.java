package com.ingeniapps.dicmax.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import com.ingeniapps.dicmax.helper.AlertasErrores;
import com.ingeniapps.dicmax.loader.SpinKitView;
import com.ingeniapps.dicmax.loader.SpriteFactory;
import com.ingeniapps.dicmax.loader.Tipo_Loader;
import com.ingeniapps.dicmax.loader.sprite.Sprite;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValidarUsuario extends AppCompatActivity
{
    EditText edit_text_validar_cedula;
    Button buttonSiguienteValidarCedulaEnable,buttonSiguienteValidarCedulaDisable;
    private String emailUsuario;
    public vars vars;
    AlertasErrores alertarErrores;
    SpinKitView spinKitView;
    LinearLayout linearLoading;
    Context context;
    CoordinatorLayout coordinatorValidarUsuario;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_usuario);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context=this;

        vars=new vars();

        coordinatorValidarUsuario=(CoordinatorLayout)findViewById(R.id.coordinatorValidarUsuario);


        linearLoading=(LinearLayout) findViewById(R.id.linearLoadingValidarUsuario);
        //Configuramos Loader
        spinKitView = (SpinKitView) findViewById(R.id.spin_kit_validar_usuario);
        Tipo_Loader style = Tipo_Loader.values()[2];
        Sprite drawable = SpriteFactory.create(style);
        spinKitView.setIndeterminateDrawable(drawable);

        edit_text_validar_cedula=(EditText)findViewById(R.id.edit_text_validar_cedula);
        edit_text_validar_cedula.addTextChangedListener(new ValidarUsuario.GenericTextWatcher(edit_text_validar_cedula));
        buttonSiguienteValidarCedulaEnable=(Button)findViewById(R.id.buttonSiguienteValidarCedulaEnable);
        buttonSiguienteValidarCedulaDisable=(Button)findViewById(R.id.buttonSiguienteValidarCedulaDisable);
        buttonSiguienteValidarCedulaEnable.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String cedula=edit_text_validar_cedula.getText().toString();

                WebServiceValidarUsuario(cedula);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    private void WebServiceValidarUsuario(final String cedula)
    {
        String _urlWebService=vars.ipServer.concat("/ws/forgotPass");

        buttonSiguienteValidarCedulaEnable.setVisibility(View.GONE);
        buttonSiguienteValidarCedulaDisable.setVisibility(View.VISIBLE);
        linearLoading.setVisibility(View.VISIBLE);
        edit_text_validar_cedula.setEnabled(false);

        JsonObjectRequest jsonObjReq=new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
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
                                    Intent intent=new Intent(ValidarUsuario.this,ObtenerClave.class);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }
                            }
                            else
                            {
                                if (!((Activity) context).isFinishing())
                                {
                                    alertarErrores=new AlertasErrores(""+message, coordinatorValidarUsuario,ValidarUsuario.this);
                                    buttonSiguienteValidarCedulaEnable.setVisibility(View.VISIBLE);
                                    buttonSiguienteValidarCedulaDisable.setVisibility(View.GONE);
                                    linearLoading.setVisibility(View.INVISIBLE);
                                    edit_text_validar_cedula.setEnabled(true);
                                }
                            }

                        }
                        catch (JSONException e)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                alertarErrores=new AlertasErrores(""+e.getMessage().toString(), coordinatorValidarUsuario,ValidarUsuario.this);
                            }
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError) {
                            if (!((Activity) context).isFinishing()) {
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorValidarUsuario, ValidarUsuario.this);
                            }
                        } else if (error instanceof NoConnectionError) {
                            if (!((Activity) context).isFinishing()) {
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorValidarUsuario, ValidarUsuario.this);
                            }
                        } else if (error instanceof AuthFailureError) {
                            if (!((Activity) context).isFinishing()) {
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorValidarUsuario, ValidarUsuario.this);
                            }
                        } else if (error instanceof ServerError) {
                            if (!((Activity) context).isFinishing()) {
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorValidarUsuario, ValidarUsuario.this);
                            }
                        } else if (error instanceof NetworkError) {
                            if (!((Activity) context).isFinishing()) {
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorValidarUsuario, ValidarUsuario.this);
                            }
                        } else if (error instanceof ParseError) {
                            if (!((Activity) context).isFinishing()) {
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorValidarUsuario, ValidarUsuario.this);
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
                headers.put("usuario", cedula);
                return headers;
            }

        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "VALIDATE_USUARIO");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ControllerSingleton.getInstance().cancelPendingReq("VALIDATE_USUARIO");
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
            if(TextUtils.isEmpty(edit_text_validar_cedula.getText()))
            {
                buttonSiguienteValidarCedulaDisable.setVisibility(View.VISIBLE);
                buttonSiguienteValidarCedulaEnable.setVisibility(View.GONE);
                return;
            }

            buttonSiguienteValidarCedulaDisable.setVisibility(View.GONE);
            buttonSiguienteValidarCedulaEnable.setVisibility(View.VISIBLE);
        }
    }
}
