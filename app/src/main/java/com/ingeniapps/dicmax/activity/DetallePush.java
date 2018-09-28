package com.ingeniapps.dicmax.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;

public class DetallePush extends AppCompatActivity
{

    private String codPush;
    private String titulo;
    private String mensaje;
    private String urlImagen;
    private String fecha;

    private ImageView imagenPushDetalle;
    private TextView tvTituloPushDetalle;
    private TextView tvMensajePushDetalle;
    private TextView fecExpiraPushDetalle;
    private boolean isNotifyPush=false;
    private gestionSharedPreferences sharedPrefences;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_push);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //this line shows back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);

        sharedPrefences=new gestionSharedPreferences(this);

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras == null)
            {
                codPush=null;
                titulo=null;
                mensaje=null;
                urlImagen=null;
                fecha=null;
                isNotifyPush=false;
            }

            else
            {
                codPush=extras.getString("codPush");
                titulo=extras.getString("titulo");
                mensaje=extras.getString("mensaje");
                urlImagen=extras.getString("urlImagen");
                fecha=extras.getString("fecha");
                isNotifyPush=extras.getBoolean("isNotifyPush");
            }
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(isNotifyPush)
                {
                    if(sharedPrefences.getBoolean("isActivePrincipal"))
                    {
                        finish();
                    }
                    else
                    {
                        Intent i=new Intent(DetallePush.this, Inicio.class);
                        startActivity(i);
                        isNotifyPush=false;
                        finish();
                    }
                }
                else
                {
                    finish();
                }
            }
        });

        Log.i("pagauno",""+urlImagen);

        imagenPushDetalle=findViewById(R.id.imagenPushDetalle);
        tvTituloPushDetalle=findViewById(R.id.tvTituloPushDetalle);
        tvMensajePushDetalle=findViewById(R.id.tvMensajePushDetalle);
        fecExpiraPushDetalle=findViewById(R.id.fecExpiraPushDetalle);

        Glide.with(DetallePush.this).
                load(urlImagen).
                thumbnail(0.5f).into(imagenPushDetalle);

        tvTituloPushDetalle.setText(""+titulo);
        tvMensajePushDetalle.setText(""+mensaje);

        long timestamp = Long.parseLong(fecha) * 1000L;
        CharSequence fecha = DateUtils.getRelativeTimeSpanString(timestamp,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        fecExpiraPushDetalle.setText(fecha);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(isNotifyPush)
            {
                if(!sharedPrefences.getBoolean("isActivePrincipal"))
                {
                    Log.i("notificado","notificado por push - principal no esta activa");
                    Intent i=new Intent(DetallePush.this, Inicio.class);
                    startActivity(i);
                    isNotifyPush=false;
                    DetallePush.this.finish();
                }
                else
                {
                    Log.i("notificado","notificado por push - principal activa");
                    DetallePush.this.finish();
                }
            }
            else
            {
                DetallePush.this.finish();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}
