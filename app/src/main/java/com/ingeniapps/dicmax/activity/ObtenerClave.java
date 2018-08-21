package com.ingeniapps.dicmax.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.ingeniapps.dicmax.R;

public class ObtenerClave extends AppCompatActivity {

   Button buttonRegresarOlvidoClave;
    private Typeface copperplateGothicLight;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtener_clave);

        copperplateGothicLight = Typeface.createFromAsset(ObtenerClave.this.getAssets(), "fonts/AvenirLTStd-Light.ttf");


        buttonRegresarOlvidoClave=findViewById(R.id.buttonRegresarOlvidoClave);
        buttonRegresarOlvidoClave.setTypeface(copperplateGothicLight);

        buttonRegresarOlvidoClave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(ObtenerClave.this,Login.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent=new Intent(ObtenerClave.this,Login.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
