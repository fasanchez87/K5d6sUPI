package com.ingeniapps.dicmax.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.integration.android.IntentIntegrator;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.app.Config;
import com.ingeniapps.dicmax.beans.Empresa;
import com.ingeniapps.dicmax.fragment.Categorias;
import com.ingeniapps.dicmax.fragment.Compromisos;
import com.ingeniapps.dicmax.fragment.Contacto;
import com.ingeniapps.dicmax.fragment.Cuenta;
import com.ingeniapps.dicmax.fragment.Datos;
import com.ingeniapps.dicmax.fragment.FragmentIntentIntegrator;
import com.ingeniapps.dicmax.fragment.Home;
import com.ingeniapps.dicmax.fragment.Pagos;
import com.ingeniapps.dicmax.fragment.Puntos;
import com.ingeniapps.dicmax.helper.BottomNavigationViewHelper;
import com.ingeniapps.dicmax.qrscanner.MaterialBarcodeScanner;
import com.ingeniapps.dicmax.qrscanner.MaterialBarcodeScannerBuilder;
import com.ingeniapps.dicmax.volley.ControllerSingleton;
import com.ingeniapps.dicmax.vars.vars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class Inicio extends AppCompatActivity
{
    public String currentVersion = null;
    private Context context;
    Dialog dialog;
    private String html="";
    private String versionPlayStore="";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String tokenFCM;
    private vars vars;
    public static final String BARCODE_KEY = "BARCODE";
    private Barcode barcodeResult;
    private ProgressDialog progressDialog;
    gestionSharedPreferences gestionSharedPreferences;
    gestionSharedPreferences gestionSharedHuella;
    private Boolean guardarSesion;
    private String idCiudad;

    private String indicaPush;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BroadcastReceiver broadcast_reciever;

    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "androidHive";
    private Cipher cipher;
    private TextView textView;
    private String numDocumento,clave;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializing both Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);


      /*  //VALIDACION DE HUELLA
        // Check whether the device has a Fingerprint sensor.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (fingerprintManager.isHardwareDetected())
            {
                Toast.makeText(Inicio.this, "Sensor huella detectado.", Toast.LENGTH_SHORT).show();
                // Checks whether fingerprint permission is set on manifest
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Fingerprint authentication permission not enabled", Toast.LENGTH_SHORT).show();
                } else {
                    // Check whether at least one fingerprint is registered
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        Toast.makeText(context, "Register at least one fingerprint in Settings", Toast.LENGTH_SHORT).show();
                    } else {
                        // Checks whether lock screen security is enabled or not
                        if (!keyguardManager.isKeyguardSecure()) {
                            Toast.makeText(context, "Lock screen security not enabled in Settings", Toast.LENGTH_SHORT).show();
                        } else {
                            generateKey();
                            if (cipherInit())
                            {
                                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                FingerprintHandler helper = new FingerprintHandler(this);
                                helper.startAuth(fingerprintManager, cryptoObject);
                            }
                        }
                    }
                }
            }*/


                gestionSharedPreferences = new gestionSharedPreferences(this);
                gestionSharedHuella = new gestionSharedPreferences(this);
                guardarSesion = gestionSharedPreferences.getBoolean("GuardarSesion");

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

                context = Inicio.this;
                vars = new vars();

                broadcast_reciever = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context arg0, Intent intent) {
                        String action = intent.getAction();
                        if (action.equals("finish_activity")) {
                            finish();
                            // DO WHATEVER YOU WANT.
                        }
                    }
                };
                registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));

                BarcodeDetector detector =
                        new BarcodeDetector.Builder(getApplicationContext())
                                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                                .build();

                if (savedInstanceState != null) {
                    Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
                    if (restoredBarcode != null) {
                        //result.setText(restoredBarcode.rawValue);
                        barcodeResult = restoredBarcode;
                    }
                }

                if (savedInstanceState == null) {
                    Bundle extras = getIntent().getExtras();
                    if (extras == null) {
                        idCiudad = null;
                        indicaPush = null;
                        numDocumento = null;
                        clave = null;
                    } else {
                        idCiudad = extras.getString("codCiudad");
                        indicaPush = extras.getString("indicaPush");
                        numDocumento = extras.getString("numDocumento");
                        clave = extras.getString("claveUsuario");
                    }
                }

                gestionSharedPreferences.putString("clave",""+clave);

                //Toast.makeText(Inicio.this,"codCiudad: "+idCiudad,Toast.LENGTH_LONG).show();





                toolbar.setNavigationIcon(R.drawable.ic_signout);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (guardarSesion == false) {
                            gestionSharedPreferences.clear();
                            Intent i = new Intent(Inicio.this, Login.class);
                            startActivity(i);
                            finish();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(new android.support.v7.view.ContextThemeWrapper(Inicio.this, R.style.AlertDialogTheme));
                            builder
                                    .setTitle("Kupi")
                                    .setMessage("¿Deseas cerrar sesión?")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            cerrarSesion();
                                        }
                                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).show();
                        }
                    }
                });

                BottomNavigationView bottomNavigationView = (BottomNavigationView)
                        findViewById(R.id.bottom_navigation);

                BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new Home());
                fragmentTransaction.commit();

                bottomNavigationView.setOnNavigationItemSelectedListener
                        (new BottomNavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                Fragment fragment = null;
                                Class fragmentClass;

                                switch (item.getItemId()) {
                                    case R.id.action_inicio:
                                        fragmentClass = Home.class;
                                        break;
                                    case R.id.action_comercios:
                                        fragmentClass = Categorias.class;
                                        break;
                                    case R.id.action_contacto:
                                        if (guardarSesion == false) {
                                            cargarLogin();
                                        } else {
                                            fragmentClass = Contacto.class;
                                            break;
                                        }
                                    case R.id.action_historial:
                                        if (guardarSesion == false) {
                                            cargarLogin();
                                        } else {
                                            fragmentClass = Pagos.class;
                                            break;
                                        }
                                    case R.id.action_cuenta:
                                        if (guardarSesion == false) {
                                            cargarLogin();
                                        } else {
                                            fragmentClass = Cuenta.class;
                                            break;
                                        }
                                    default:
                                        fragmentClass = Home.class;
                                }

                                try {
                                    fragment = (Fragment) fragmentClass.newInstance();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.frame_layout, fragment);
                                fragmentTransaction.commit();
                                return true;
                            }
                        });


            /*mRegistrationBroadcastReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    // checking for type intent filter
                    if (intent.getAction().equals(Config.PUSH_NOTIFICATION))
                    {
                        if(!(Inicio.this).isFinishing())
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Inicio.this, R.style.AlertDialogTheme));
                            builder
                                    .setTitle("Kupi")
                                    .setMessage("Hola! Tienes un nuevo mensaje justo ahora!")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            *//*bottomNavigationView.setSelectedItemId(R.id.action_historial);
                                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.frame_layout, new HistorialNominaciones());
                                            fragmentTransaction.commit();*//*
                                        }
                                    }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                                    setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    }
                }
            };*/


                try {
                    currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    gestionSharedPreferences.putString("versionApp", currentVersion);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                if (checkPlayServices()) {
                    if (!TextUtils.isEmpty(FirebaseInstanceId.getInstance().getToken())) {
                        tokenFCM = FirebaseInstanceId.getInstance().getToken();
                        Log.i("tokenFCM", "" + tokenFCM);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Inicio.this, R.style.AlertDialogTheme));
                    builder
                            .setTitle("Google Play Services")
                            .setMessage("Se ha encontrado un error con los servicios de Google Play, actualizalo y vuelve a ingresar.")
                            .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            }).setCancelable(false).show().getButton(DialogInterface.BUTTON_POSITIVE).
                            setTextColor(getResources().getColor(R.color.colorPrimary));
            }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("Principal", "onPause");
        gestionSharedPreferences.putBoolean("isActivePrincipal",false);
    }

    private boolean isHuella;

    private void cerrarSesion()
    {
        String _urlWebServiceUpdateToken = vars.ipServer.concat("/ws/closeSession");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebServiceUpdateToken, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            boolean status=response.getBoolean("status");
                            if(status)
                            {
                                isHuella= gestionSharedPreferences.getBoolean("isHuella");
                                gestionSharedPreferences.clear();
                                //CONSERVAMOS LOS DATOS DE INGRESO PARA EL CASO DE LA HUELLA
                                //Toast.makeText(context, "isHuella Cerrar Sesion: "+isHuella, Toast.LENGTH_SHORT).show();
                                if(isHuella)
                                {
                                    gestionSharedPreferences.putBoolean("isHuella",true);
                                    gestionSharedPreferences.putString("numDocumento",""+numDocumento);
                                    gestionSharedPreferences.putString("clave",""+clave);
                                    //Toast.makeText(context, "Clave Inicio: "+clave, Toast.LENGTH_SHORT).show();
                                }
                                Intent i=new Intent(Inicio.this, Login.class);
                                startActivity(i);
                                finish();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //Toast.makeText(getActivity(), "Token FCM: " + "error"+error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("codUsuario",""+gestionSharedPreferences.getString("codUsuario"));
                return headers;
            }
        };
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "tokenFCM");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void updateTokenFCMToServer()
    {
        String _urlWebServiceUpdateToken = vars.ipServer.concat("/ws/updateTokenFCM");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebServiceUpdateToken, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //Toast.makeText(getActivity(), "Token FCM: " + "error"+error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("numDocumento",""+gestionSharedPreferences.getString("numDocumento"));
                headers.put("tokenFCM",""+tokenFCM);
                headers.put("versionApp", currentVersion);
                headers.put("codSistema", "1");
                return headers;
            }
        };
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "tokenFCM");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void cargarLogin()
    {
        Intent intent = new Intent(Inicio.this, Login.class);//CERRAMOS LAS ACTIVIDADES TODAS ANTERIORES CREADAS
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {

            if (guardarSesion==false)
            {
                gestionSharedPreferences.clear();
                Intent i=new Intent(Inicio.this, Login.class);
                i.putExtra("tipoIngreso","invitado");
                startActivity(i);
                finish();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(new android.support.v7.view.ContextThemeWrapper(this, R.style.AlertDialogTheme));
                builder
                        .setTitle("Kupi")
                        .setMessage("¿Deseas salir de la aplicación?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                finish();

                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                    }
                }).show();
            }
        }

        return false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(broadcast_reciever);
        ControllerSingleton.getInstance().cancelPendingReq("tokenFCM");
    }

    private boolean notificaUpdate=false;


    @Override
    public void onResume()
    {
        super.onResume();
        updateTokenFCMToServer();
        gestionSharedPreferences.putBoolean("isActivePrincipal",true);

        //SI LA PERSONA ACTUALIZA EL APP PERO NO SE DESLOGUEA, LE DECIMOS QUE ESCOJA SU CIUDAD DE USO
        if(TextUtils.isEmpty(gestionSharedPreferences.getString("codCiudad")) || TextUtils.isEmpty(gestionSharedPreferences.getString("nomCiudad")))
        {

            if (!((Activity) context).isFinishing())
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Inicio.this,R.style.AlertDialogTheme));
                builder
                        .setTitle("Hola,").setMessage("Debes escoger una ciudad para disfrutar de las mejores marcas y ofertas.")
                        .setPositiveButton("¡Vamos a hacerlo!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Intent intent = new Intent(Inicio.this, BuscarCiudad.class);
                                intent.putExtra("invitado",false);
                                intent.putExtra("isUpdateApp",true);
                                //gestionSharedPreferences.clear();
                                startActivity(intent);
                                finish();
                            }
                        }).setCancelable(false).show();
            }
        }
    }

    private void startScan()
    {
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(Inicio.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Escaneando...")
                .withOnlyQRCodeScanning()
                .withResultListener(new MaterialBarcodeScanner.OnResultListener()
                {
                    @Override
                    public void onResult(Barcode barcode)
                    {
                        barcodeResult = barcode;
                        //result.setText(barcode.rawValue);

                        if(!TextUtils.isEmpty(barcode.rawValue))
                        {
                            //WebService Datos de Pago
                            progressDialog = new ProgressDialog(new ContextThemeWrapper(Inicio.this,R.style.AppCompatAlertDialogStyle));
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Validando...");
                            progressDialog.show();
                            progressDialog.setCancelable(false);

                            WebServiceGetDatosPago(barcode.rawValue.toString());

                        }
                        else
                        {
                            Snackbar.make(findViewById(R.id.activity_main), "No se logró validar el código de factura.",
                                    Snackbar.LENGTH_INDEFINITE)
                                    .show();
                        }

                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable(BARCODE_KEY, barcodeResult);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MaterialBarcodeScanner.RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScan();
            return;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogTheme));
        builder.setTitle("Permiso Cámara")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_busqueda, menu);
        return true;
    }*/


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


    private void WebServiceGetDatosPago(final String codQr)
    {
        String _urlWebService = vars.ipServer.concat("/ws/validateQr");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            String message=response.getString("message");

                            if(response.getBoolean("status"))
                            {

                                if (!((Activity) context).isFinishing())
                                {
                                    if(progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();
                                    }
                                }

                                JSONObject jsonObject=response.getJSONObject("pay");
                                Intent i=new Intent(Inicio.this, Pago.class);
                                i.putExtra("nomEmpresa",""+jsonObject.getString("nomEmpresa"));
                                i.putExtra("refPago",""+jsonObject.getString("refTransaccion"));
                                i.putExtra("valPago",""+jsonObject.getString("valTransaccion"));
                                i.putExtra("codEmpresa",""+jsonObject.getString("codEmpresa"));
                                i.putExtra("numTransaccion",""+jsonObject.getString("numTransaccion"));
                                //VALIDAMOS SI HAY HUELLA Y ENVIAMOS LOS ENROLES
                                i.putExtra("isHuella",gestionSharedPreferences.getBoolean("isHuella"));
                                i.putExtra("numDocumento",""+numDocumento);
                                i.putExtra("clave",""+clave);
                                startActivity(i);
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

                                Snackbar.make(findViewById(R.id.activity_main), ""+message,
                                        Snackbar.LENGTH_LONG)
                                        .show();

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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Inicio.this,R.style.AlertDialogTheme));
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Inicio.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Inicio.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Inicio.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Inicio.this,R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Inicio.this,R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Inicio.this,R.style.AlertDialogTheme));
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
                headers.put("codQr", codQr);
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "getDatosPago");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
