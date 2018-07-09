package com.ingeniapps.dicmax.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.vision.barcode.Barcode;
import com.ingeniapps.dicmax.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ingeniapps.dicmax.activity.Inicio;
import com.ingeniapps.dicmax.activity.Login;
import com.ingeniapps.dicmax.activity.Pago;
import com.ingeniapps.dicmax.activity.ValidarUsuario;
import com.ingeniapps.dicmax.qrscanner.MaterialBarcodeScanner;
import com.ingeniapps.dicmax.qrscanner.MaterialBarcodeScannerBuilder;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;


public class Home extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener
{
    private SliderLayout mDemoSlider;

    private gestionSharedPreferences sharedPreferences;
    private Barcode barcodeResult;


    RelativeLayout relativeLayoutEsperaCarga;
    LinearLayout relativeLayoutCargaPromos;
    public static final String BARCODE_KEY = "BARCODE";

    private ProgressDialog progressDialog;
    private Boolean guardarSesion;




    private int pagina;
    Context context;
    private boolean solicitando=false;
    //VERSION DEL APP INSTALADA

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    vars vars;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        vars=new vars();
        context = getActivity();
        sharedPreferences=new gestionSharedPreferences(Home.this.getActivity());
        guardarSesion=sharedPreferences.getBoolean("GuardarSesion");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        //setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState != null)
        {
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if(restoredBarcode != null)
            {
                //result.setText(restoredBarcode.rawValue);
                barcodeResult = restoredBarcode;
            }
        }

        mDemoSlider = (SliderLayout)getActivity().findViewById(R.id.slider);

        relativeLayoutEsperaCarga=(RelativeLayout) getActivity().findViewById(R.id.relativeLayoutEsperaCarga);
        relativeLayoutCargaPromos=(LinearLayout) getActivity().findViewById(R.id.relativeLayoutCargaPromos);
        relativeLayoutCargaPromos.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (guardarSesion==false)
                {
                    cargarLogin();
                }
                else
                {
                    startScan();
                }
            }
        });

        HashMap<String,String> url_maps = new HashMap<String, String>();

        WebServiceGetEventos();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable(BARCODE_KEY, barcodeResult);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {


            if (requestCode != MaterialBarcodeScanner.RC_HANDLE_CAMERA_PERM)
            {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
            }
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                startScan();
                return;
            }
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(), R.style.AlertDialogTheme));
            builder.setTitle("Permiso Cámara")
                    .setMessage(R.string.no_camera_permission)
                    .setPositiveButton(android.R.string.ok, listener)
                    .show();


    }

    public void cargarLogin()
    {
        Intent intent = new Intent(Home.this.getActivity(), Login.class);
        startActivity(intent);
        Home.this.getActivity().finish();
    }



    private void startScan()
    {
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(Home.this.getActivity())
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
                            progressDialog = new ProgressDialog(new ContextThemeWrapper(Home.this.getActivity(),R.style.AppCompatAlertDialogStyle));
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Validando...");
                            progressDialog.show();
                            progressDialog.setCancelable(false);

                            WebServiceGetDatosPago(barcode.rawValue.toString());

                        }
                        else
                        {
                            Snackbar.make(getActivity().findViewById(R.id.activity_main), "No se logró validar el código de factura.",
                                    Snackbar.LENGTH_INDEFINITE)
                                    .show();
                        }

                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    /*@Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        //menu.findItem(R.id.menu_busqueda).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }*/

   /* @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
       // inflater.inflate(R.menu.menu_buscar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            *//*case R.id.menu_busqueda:
            {
                Intent buscar=new Intent(fragment_ccomercial.this.getActivity(),Buscar.class);
                startActivity(buscar);
                break;
            }*//*
        }
        return true;
    }*/

    @Override
    public void onResume()
    {
        super.onResume();
        mDemoSlider.startAutoCycle();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mDemoSlider.stopAutoCycle();
    }

    @Override
    public void onStop()
    {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider)
    {
        //Toast.makeText(getActivity(),slider.getBundle().get("codEvento") + "",Toast.LENGTH_SHORT).show();
      /*  Intent i=new Intent(Home.this.getActivity(), DetalleEvento.class);
        i.putExtra("codEvento",""+slider.getBundle().get("codEvento"));
        startActivity(i);*/
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ControllerSingleton.getInstance().cancelPendingReq("getDatosPromos");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }

    @Override
    public void onPageSelected(int position)
    {
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
    }

    private void WebServiceGetEventos()
    {
        String _urlWebService = vars.ipServer.concat("/ws/getPromos");

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
                                JSONArray listaPromos = response.getJSONArray("promos");
                                long timestamp;
                                CharSequence timeAgo;

                                for (int i = 0; i < listaPromos.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) listaPromos.get(i);

                                    timestamp = Long.parseLong(jsonObject.getString("fecExpiraPromo")) * 1000L;
                                    timeAgo = DateUtils.getRelativeTimeSpanString(timestamp,
                                            System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                                    TextSliderView textSliderView = new TextSliderView(Home.this.getActivity());
                                    textSliderView
                                            .description("" + timeAgo)
                                            .image(jsonObject.getString("imgPromo"))
                                            .setScaleType(BaseSliderView.ScaleType.Fit)
                                            .setOnSliderClickListener((BaseSliderView.OnSliderClickListener)Home.this);
                                    //add your extra information
                                    textSliderView.bundle(new Bundle());
                                    textSliderView.getBundle()
                                            .putString("codPromo", jsonObject.getString("codPromo"));
                                    mDemoSlider.addSlider(textSliderView);
                                }

                                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                                mDemoSlider.setDuration(5000);
                                mDemoSlider.addOnPageChangeListener((ViewPagerEx.OnPageChangeListener) Home.this);
                                mDemoSlider.setPresetTransformer("ZoomOut");

                                relativeLayoutEsperaCarga.setVisibility(View.GONE);
                                relativeLayoutCargaPromos.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (JSONException e)
                        {
                            relativeLayoutEsperaCarga.setVisibility(View.VISIBLE);
                            relativeLayoutCargaPromos.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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
                           /* layoutMacroEsperaEmpleados.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empleados.setVisibility(View.VISIBLE);
*/
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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
                            /*layoutMacroEsperaEmpleados.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empleados.setVisibility(View.VISIBLE);*/

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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
                           /* layoutMacroEsperaEmpleados.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empleados.setVisibility(View.VISIBLE);*/

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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
                            /*layoutMacroEsperaEmpleados.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empleados.setVisibility(View.VISIBLE);*/

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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
                           /* layoutMacroEsperaEmpleados.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empleados.setVisibility(View.VISIBLE);*/

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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
                           /* layoutMacroEsperaEmpleados.setVisibility(View.VISIBLE);
                            layoutEspera.setVisibility(View.GONE);
                            not_found_empleados.setVisibility(View.VISIBLE);*/

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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
              /*  headers.put("buscar", TextUtils.isEmpty(busqueda)?"":busqueda);
                headers.put("categoria", TextUtils.isEmpty(codEmpleado)?"":codEmpleado);*/
                //headers.put("MyToken", gestionSharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "getDatosPromos");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                                Intent i=new Intent(Home.this.getActivity(), Pago.class);
                                i.putExtra("nomEmpresa",""+jsonObject.getString("nomEmpresa"));
                                i.putExtra("refPago",""+jsonObject.getString("refTransaccion"));
                                i.putExtra("valPago",""+jsonObject.getString("valTransaccion"));
                                i.putExtra("codEmpresa",""+jsonObject.getString("codEmpresa"));
                                i.putExtra("numTransaccion",""+jsonObject.getString("numTransaccion"));
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

                                Snackbar.make(getActivity().findViewById(R.id.activity_main), ""+message,
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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


                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Home.this.getActivity(),R.style.AlertDialogTheme));
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