package com.ingeniapps.dicmax.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ingeniapps.dicmax.R;

public class Prelogin extends AppCompatActivity
{
    private Button buttonIrComprar;
    private TextView textViewIngresoInvitado;
    private TextView textViewRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prelogin);

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
                Intent intent = new Intent(Prelogin.this, Inicio.class);
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
}
