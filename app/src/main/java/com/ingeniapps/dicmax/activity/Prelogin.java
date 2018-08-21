package com.ingeniapps.dicmax.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;

public class Prelogin extends AppCompatActivity
{
    private Button buttonIrComprar;
    private TextView textViewIngresoInvitado;
    private TextView textViewRegistro;
    private String tokenFCM;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private gestionSharedPreferences gestionSharedPreferences;
    private Boolean guardarSesion;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prelogin);
        gestionSharedPreferences=new gestionSharedPreferences(this);
        //COMPROBAMOS LA SESION DEL USUARIO
        guardarSesion=gestionSharedPreferences.getBoolean("GuardarSesion");
        if (guardarSesion==true)
        {
            cargarActivityPrincipal();
        }

        if(checkPlayServices())
        {
            if(!TextUtils.isEmpty(FirebaseInstanceId.getInstance().getToken()))
            {
                tokenFCM=FirebaseInstanceId.getInstance().getToken();
            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Prelogin.this,R.style.AlertDialogTheme));
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

        //Estilo tipo letra boton.
        buttonIrComprar=(Button)findViewById(R.id.buttonIrComprar);
        textViewIngresoInvitado=(TextView)findViewById(R.id.textViewIngresoInvitado);
        textViewRegistro=(TextView)findViewById(R.id.textViewRegistro);
        Typeface copperplateGothicLight = Typeface.createFromAsset(getAssets(), "fonts/AvenirLTStd-Light.ttf");
        buttonIrComprar.setTypeface(copperplateGothicLight);

        buttonIrComprar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i=new Intent(Prelogin.this, Login.class);
                startActivity(i);
                finish();
            }
        });

        textViewIngresoInvitado.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(Prelogin.this, BuscarCiudad.class);
                intent.putExtra("invitado",true);
                gestionSharedPreferences.clear();
                startActivity(intent);
                finish();
            }
        });

        textViewRegistro.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(Prelogin.this, Registro.class);
                startActivity(intent);

            }
        });
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

    public void cargarActivityPrincipal()
    {
        Intent intent = new Intent(Prelogin.this, Inicio.class);
        startActivity(intent);
        Prelogin.this.finish();
    }
}
