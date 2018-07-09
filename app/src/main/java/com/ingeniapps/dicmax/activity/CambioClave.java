package com.ingeniapps.dicmax.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.volley.ControllerSingleton;
import com.ingeniapps.dicmax.vars.vars;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CambioClave extends AppCompatActivity
{
    private EditText edit_text_clave,edit_text_confirmar_clave;
    private Button buttonCambioDisable, buttonCambioClaveEnable;
    private LinearLayout linearLoadingCambioClave;
    private vars vars;
    private Context context;
    private gestionSharedPreferences sharedPreferences;
    CoordinatorLayout coordinatorCambioClave;
    AlertasErrores alertarErrores;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_clave);

        context=this;
        vars=new vars();
        sharedPreferences=new gestionSharedPreferences(getApplicationContext());
        coordinatorCambioClave=(CoordinatorLayout)findViewById(R.id.coordinatorCambioClave);
        linearLoadingCambioClave=(LinearLayout) findViewById(R.id.linearLoadingCambioClave);

        edit_text_clave=(EditText)findViewById(R.id.edit_text_clave);
        edit_text_clave.addTextChangedListener(new CambioClave.GenericTextWatcher(edit_text_clave));

        edit_text_confirmar_clave=(EditText)findViewById(R.id.edit_text_confirmar_clave);
        edit_text_confirmar_clave.addTextChangedListener(new CambioClave.GenericTextWatcher(edit_text_confirmar_clave));

        buttonCambioDisable=(Button)findViewById(R.id.buttonCambioDisable);

        buttonCambioClaveEnable=(Button)findViewById(R.id.buttonCambioClaveEnable);
        buttonCambioClaveEnable.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                WebServiceCambioClave(edit_text_clave.getText().toString(),edit_text_confirmar_clave.getText().toString());
            }
        });

    }

    private void WebServiceCambioClave(final String clave, final String confirmarClave)
    {
        String _urlWebService=vars.ipServer.concat("/ws/changePass");

        buttonCambioClaveEnable.setVisibility(View.GONE);
        buttonCambioDisable.setVisibility(View.VISIBLE);
        linearLoadingCambioClave.setVisibility(View.VISIBLE);
        edit_text_clave.setEnabled(false);
        edit_text_confirmar_clave.setEnabled(false);

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

                                    buttonCambioDisable.setVisibility(View.VISIBLE);
                                    buttonCambioClaveEnable.setVisibility(View.GONE);
                                    linearLoadingCambioClave.setVisibility(View.INVISIBLE);

                                    edit_text_clave.setEnabled(false);
                                    edit_text_confirmar_clave.setEnabled(false);


                                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(CambioClave.this,R.style.AlertDialogTheme));
                                    builder
                                            .setTitle("Cambio Contraseña")
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
                                    alertarErrores=new AlertasErrores(""+message, coordinatorCambioClave,CambioClave.this);

                                    buttonCambioDisable.setVisibility(View.VISIBLE);
                                    buttonCambioClaveEnable.setVisibility(View.GONE);
                                    linearLoadingCambioClave.setVisibility(View.INVISIBLE);
                                    edit_text_clave.setEnabled(true);
                                    edit_text_confirmar_clave.setEnabled(true);
                                }
                            }

                        }
                        catch (JSONException e)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                //alertarErrores=new AlertasErrores(""+e.getMessage().toString(), coordinatorValidarUsuario,ValidarUsuario.this);
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
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorCambioClave, CambioClave.this);
                            }
                        } else if (error instanceof NoConnectionError) {
                            if (!((Activity) context).isFinishing()) {
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorCambioClave, CambioClave.this);
                            }
                        } else if (error instanceof AuthFailureError) {
                            if (!((Activity) context).isFinishing()) {
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorCambioClave, CambioClave.this);
                            }
                        } else if (error instanceof ServerError) {
                            if (!((Activity) context).isFinishing()) {
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorCambioClave, CambioClave.this);
                            }
                        } else if (error instanceof NetworkError) {
                            if (!((Activity) context).isFinishing()) {
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorCambioClave, CambioClave.this);
                            }
                        } else if (error instanceof ParseError) {
                            if (!((Activity) context).isFinishing()) {
                                alertarErrores = new AlertasErrores("" + error.getMessage().toString(), coordinatorCambioClave, CambioClave.this);
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
                headers.put("usuario", sharedPreferences.getString("codUsuario") );
                headers.put("password", clave);
                headers.put("passwordConfirm", confirmarClave);
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

            if(edit_text_clave.getText().toString().length()<=4)
            {
                buttonCambioDisable.setVisibility(View.VISIBLE);
                buttonCambioClaveEnable.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(edit_text_clave.getText()))
            {
                buttonCambioDisable.setVisibility(View.VISIBLE);
                buttonCambioClaveEnable.setVisibility(View.GONE);
                return;
            }

            if(TextUtils.isEmpty(edit_text_confirmar_clave.getText()))
            {
                buttonCambioDisable.setVisibility(View.VISIBLE);
                buttonCambioClaveEnable.setVisibility(View.GONE);
                return;
            }

            if(!TextUtils.equals(edit_text_clave.getText().toString(),edit_text_confirmar_clave.getText().toString()))
            {
                buttonCambioDisable.setVisibility(View.VISIBLE);
                buttonCambioClaveEnable.setVisibility(View.GONE);
                return;
            }

            buttonCambioClaveEnable.setVisibility(View.VISIBLE);
            buttonCambioDisable.setVisibility(View.GONE);
        }
    }
}
