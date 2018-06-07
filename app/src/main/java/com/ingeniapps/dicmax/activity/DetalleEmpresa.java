package com.ingeniapps.dicmax.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.bumptech.glide.Glide;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.beans.Empresa;
import com.ingeniapps.dicmax.volley.ControllerSingleton;
import com.ingeniapps.dicmax.vars.vars;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yanzhenjie.permission.SettingService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetalleEmpresa extends AppCompatActivity implements RationaleListener {

    private String idEmpresa;
    private ImageView imagenEmpresaDetalleEmpresa;
    private ImageView imagenPromocionDetalleEmpresa;
    private TextView editTextNombreDetalleEmpresa;
    private TextView editTextDireccionDetalleEmpresa;
    private TextView editTextDescripcionDetalleEmpresa;
    private Button botonComprarDetalleEmpresa;
    private Button botonLlegarDetalleEmpresa;
    private Button botonWebDetalleEmpresa;
    private Button botonLlamarDetalleEmpresa;
    vars vars;

    private String datoLlegarEmpresa;
    private String datoLlamarEmpresa;
    private String datoWebEmpresa;
    private String latitudEmpresa;


    private String descripcionEmpresa;

    private String longitudEmpresa;


    private String datoComprarEmpresa;

    private ProgressDialog progressDialog;
    private Context context;

    private static final int REQUEST_PHONE_CALL = 1;
    private static final int REQUEST_CODE_SETTING = 300;

    private RelativeLayout layoutMacroEspera;
    private LinearLayout llDetalle;

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_empresa);

        context=this;

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

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



        vars = new vars();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                idEmpresa = null;
            } else {
                idEmpresa = extras.getString("idEmpresa");
            }
        }

        layoutMacroEspera=(RelativeLayout)findViewById(R.id.layoutMacroEspera);
        llDetalle=(LinearLayout)findViewById(R.id.llDetalle);

        imagenEmpresaDetalleEmpresa = (ImageView) findViewById(R.id.imagenEmpresaDetalleEmpresa);
        imagenPromocionDetalleEmpresa = (ImageView) findViewById(R.id.imagenPromocionDetalleEmpresa);
        editTextNombreDetalleEmpresa = (TextView) findViewById(R.id.editTextNombreDetalleEmpresa);
        editTextDireccionDetalleEmpresa = (TextView) findViewById(R.id.editTextDireccionDetalleEmpresa);
        editTextDescripcionDetalleEmpresa = (TextView) findViewById(R.id.editTextDescripcionDetalleEmpresa);

        botonComprarDetalleEmpresa = (Button) findViewById(R.id.botonComprarDetalleEmpresa);
        botonLlegarDetalleEmpresa = (Button) findViewById(R.id.botonLlegarDetalleEmpresa);
        botonWebDetalleEmpresa = (Button) findViewById(R.id.botonWebDetalleEmpresa);
        botonLlamarDetalleEmpresa = (Button) findViewById(R.id.botonLlamarDetalleEmpresa);

        WebServiceGetEmpresas(idEmpresa);

        botonComprarDetalleEmpresa = (Button) findViewById(R.id.botonComprarDetalleEmpresa);
        botonComprarDetalleEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetalleEmpresa.this, WebViewVisor.class);
                i.putExtra("url", getDatoComprarEmpresa());
                startActivity(i);
            }
        });

        botonLlegarDetalleEmpresa = (Button) findViewById(R.id.botonLlegarDetalleEmpresa);
        botonLlegarDetalleEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetalleEmpresa.this, WebViewVisor.class);
                String url = "http://maps.google.com/?q=" + getLatitudEmpresa() + "," + getLongitudEmpresa();
                i.putExtra("url", url);
                startActivity(i);
            }
        });

        botonWebDetalleEmpresa = (Button) findViewById(R.id.botonWebDetalleEmpresa);
        botonWebDetalleEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetalleEmpresa.this, WebViewVisor.class);
                i.putExtra("url", getDatoWebEmpresa());
                startActivity(i);
            }
        });


        botonLlamarDetalleEmpresa = (Button) findViewById(R.id.botonLlamarDetalleEmpresa);
        botonLlamarDetalleEmpresa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (ContextCompat.checkSelfPermission(DetalleEmpresa.this, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED)
                    {
                        //PERMISO TELEFONO
                        AndPermission.with(DetalleEmpresa.this)
                                .requestCode(REQUEST_PHONE_CALL)
                                .permission(
                                        // Multiple permissions, array form.
                                        Manifest.permission.CALL_PHONE
                                        )
                                .callback(permissionListener)
                                .rationale(new RationaleListener()
                                {
                                    @Override
                                    public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(DetalleEmpresa.this, R.style.AlertDialogTheme));
                                        builder
                                                .setTitle("Permiso de Télefono")
                                                .setMessage("Para lograr llamar al comercio es necesario que concedas permisos de llamada, para ello presiona el botón ACEPTAR.")
                                                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        rationale.resume();
                                                    }
                                                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                rationale.cancel();
                                            }
                                        }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                                                setTextColor(getResources().getColor(R.color.colorPrimary));
                                    }
                                })
                                .start();
                    }
                    else
                    {
                        llamar(getDatoLlamarEmpresa());
                    }
                }
                else
                {
                    llamar(getDatoLlamarEmpresa());
                }
            }
        });


    }

    private void llamar(String number)
    {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(phoneIntent);
    }

    private PermissionListener permissionListener = new PermissionListener()
    {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions)
        {
            switch (requestCode)
            {
                case REQUEST_PHONE_CALL:
                {
                    llamar(getDatoLlamarEmpresa());
                    break;
                }
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions)
        {
            switch (requestCode)
            {
                case REQUEST_PHONE_CALL:
                {
                    Toast.makeText(DetalleEmpresa.this, "Acceso denegado a llamadas, por favor habilite el permiso.", Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                    break;
                }
            }

            if (AndPermission.hasAlwaysDeniedPermission(DetalleEmpresa.this, deniedPermissions))
            {

                if(requestCode==100)
                {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(DetalleEmpresa.this,R.style.AlertDialogTheme));
                    builder
                            .setTitle("Permiso de Llamadas Telefónicas")
                            .setMessage("Vaya! parece que has denegado el acceso a llamadas. Presiona el botón Permitir, " +
                                    "selecciona la opción Accesos y habilita la opción de Permiso de llamar.")
                            .setPositiveButton("PERMITIR", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    SettingService settingService = AndPermission.defineSettingDialog(DetalleEmpresa.this, REQUEST_CODE_SETTING);
                                    settingService.execute();
                                }
                            }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {

                        }
                    }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                            setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                else

                if(requestCode==101)
                {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(DetalleEmpresa.this,R.style.AlertDialogTheme));
                    builder
                            .setTitle("Permiso de Llamadas Télefonicas")
                            .setMessage("Vaya! parece que has denegado el acceso a llamadas. Presiona el botón Permitir, " +
                                    "selecciona la opción Accesos y habilita la opción de Permiso de llamar.")
                            .setPositiveButton("PERMITIR", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    SettingService settingService = AndPermission.defineSettingDialog(DetalleEmpresa.this, REQUEST_CODE_SETTING);
                                    settingService.execute();
                                }
                            }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {

                        }
                    }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                            setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        }
    };

    public String getLatitudEmpresa() {
        return latitudEmpresa;
    }

    public void setLatitudEmpresa(String latitudEmpresa) {
        this.latitudEmpresa = latitudEmpresa;
    }

    public String getLongitudEmpresa() {
        return longitudEmpresa;
    }

    public void setLongitudEmpresa(String longitudEmpresa) {
        this.longitudEmpresa = longitudEmpresa;
    }

    public String getDatoComprarEmpresa() {
        return datoComprarEmpresa;
    }

    public void setDatoComprarEmpresa(String datoComprarEmpresa) {
        this.datoComprarEmpresa = datoComprarEmpresa;
    }

    public String getDescripcionEmpresa() {
        return descripcionEmpresa;
    }

    public void setDescripcionEmpresa(String descripcionEmpresa) {
        this.descripcionEmpresa = descripcionEmpresa;
    }


    public String getDatoLlegarEmpresa() {
        return datoLlegarEmpresa;
    }

    public void setDatoLlegarEmpresa(String datoLlegarEmpresa) {
        this.datoLlegarEmpresa = datoLlegarEmpresa;
    }

    public String getDatoLlamarEmpresa() {
        return datoLlamarEmpresa;
    }

    public void setDatoLlamarEmpresa(String datoLlamarEmpresa) {
        this.datoLlamarEmpresa = datoLlamarEmpresa;
    }

    public String getDatoWebEmpresa() {
        return datoWebEmpresa;
    }

    public void setDatoWebEmpresa(String datoWebEmpresa) {
        this.datoWebEmpresa = datoWebEmpresa;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ControllerSingleton.getInstance().cancelPendingReq("findEmpresa");
    }


    private void WebServiceGetEmpresas(final String id)
    {

        String _urlWebService = vars.ipServer.concat("/ws/getEmpresa");

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
                                JSONObject empresa = response.getJSONObject("empresa");

                                setDatoWebEmpresa(empresa.getString("urlWeb"));
                                setDatoComprarEmpresa(empresa.getString("urlComprar"));
                                setDatoLlamarEmpresa(empresa.getString("telefono"));
                                setLatitudEmpresa(empresa.getString("latitud"));
                                setLongitudEmpresa(empresa.getString("longitud"));
                                setDescripcionEmpresa(empresa.getString("descripcion"));

                                if(empresa.getString("urlImagen").equals(""))
                                {
                                    imagenEmpresaDetalleEmpresa.setImageResource(R.drawable.logo);
                                }

                                else
                                {
                                    Glide.with(DetalleEmpresa.this).
                                            load(empresa.getString("urlImagen")).
                                            thumbnail(0.5f).into(imagenEmpresaDetalleEmpresa);
                                }

                                if(empresa.getString("urlImagenAux").equals(""))
                                {
                                    imagenPromocionDetalleEmpresa.setVisibility(View.GONE);
                                }

                                else
                                {
                                    Glide.with(DetalleEmpresa.this).
                                            load(empresa.getString("urlImagenAux")).
                                            thumbnail(0.5f).into(imagenPromocionDetalleEmpresa);
                                }

                                if(empresa.getString("descripcion").equals(""))
                                {
                                    editTextDescripcionDetalleEmpresa.setVisibility(View.GONE);
                                }

                                else
                                {
                                    editTextDescripcionDetalleEmpresa.setText(empresa.getString("descripcion"));
                                }

                                editTextNombreDetalleEmpresa.setText(empresa.getString("nombre"));
                                editTextDireccionDetalleEmpresa.setText(empresa.getString("direccion"));

                                layoutMacroEspera.setVisibility(View.GONE);
                                llDetalle.setVisibility(View.VISIBLE);
                            }

                            else
                            {
                                if (!((Activity) context).isFinishing())
                                {
                                    if (progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();

                                    }
                                }
                            }
                        }
                        catch (JSONException e)
                        {


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleEmpresa.this,R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleEmpresa.this,R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleEmpresa.this,R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleEmpresa.this,R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleEmpresa.this,R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleEmpresa.this,R.style.AlertDialogTheme));
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetalleEmpresa.this,R.style.AlertDialogTheme));
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
                headers.put("id",id);
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "findEmpresa");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void showRequestPermissionRationale(int requestCode, Rationale rationale)
    {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture)
    {

    }
}
