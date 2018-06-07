package com.ingeniapps.dicmax.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.loader.SpinKitView;
import com.ingeniapps.dicmax.loader.SpriteFactory;
import com.ingeniapps.dicmax.loader.Tipo_Loader;
import com.ingeniapps.dicmax.loader.sprite.Sprite;

public class Login extends AppCompatActivity
{

    Button buttonIngresar;
    Button buttonIngresarDisable;
    TextInputLayout input_layout_usuario;
    TextInputLayout input_layout_clave;
    EditText editTextUsuario;
    EditText editTextClave;
    SpinKitView spinKitView;
    LinearLayout linearLoading;
    private Typeface copperplateGothicLight;

    CoordinatorLayout coordinatorLayoutLogin;


    public static final int SNACKBAR_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Estilo tipo letra boton.
        buttonIngresar=(Button)findViewById(R.id.buttonIngresar);
        linearLoading=(LinearLayout) findViewById(R.id.linearLoading);
        buttonIngresarDisable=(Button)findViewById(R.id.buttonIngresarDisable);
        copperplateGothicLight = Typeface.createFromAsset(getAssets(), "fonts/AvenirLTStd-Light.ttf");
        buttonIngresar.setTypeface(copperplateGothicLight);
        buttonIngresarDisable.setTypeface(copperplateGothicLight);

        coordinatorLayoutLogin=(CoordinatorLayout)findViewById(R.id.coordinatorLayoutLogin);

        input_layout_usuario=(TextInputLayout)findViewById(R.id.input_layout_usuario);
        input_layout_clave=(TextInputLayout)findViewById(R.id.input_layout_clave);
        editTextUsuario=(EditText)findViewById(R.id.editTextUsuario);
        input_layout_usuario.setTypeface(copperplateGothicLight);
        editTextUsuario.setTypeface(copperplateGothicLight);

        input_layout_clave=(TextInputLayout)findViewById(R.id.input_layout_clave);
        editTextClave=(EditText)findViewById(R.id.editTextClave);
        input_layout_clave.setTypeface(copperplateGothicLight);
        editTextClave.setTypeface(copperplateGothicLight);

        //Configuramos Loader
        spinKitView = (SpinKitView) findViewById(R.id.spin_kit_login);
        Tipo_Loader style = Tipo_Loader.values()[2];
        Sprite drawable = SpriteFactory.create(style);
        spinKitView.setIndeterminateDrawable(drawable);

        buttonIngresar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                buttonIngresar.setVisibility(View.GONE);

                buttonIngresarDisable.setVisibility(View.VISIBLE);
                linearLoading.setVisibility(View.VISIBLE);
                editTextUsuario.setEnabled(false);
                editTextClave.setEnabled(false);

                alertarError("Verifique su usuario o contraseña");
            }
        });



    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
           Intent i=new Intent(Login.this, Prelogin.class);
           startActivity(i);
           finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void alertarError(String message)
    {
        final Snackbar customSnackBar = Snackbar.make(coordinatorLayoutLogin, "", SNACKBAR_DURATION);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) customSnackBar.getView();

        View customsnackView = getLayoutInflater().inflate(R.layout.snack_bar_layout, null);

        TextView tv_text = (TextView) customsnackView.findViewById(R.id.tv_text);
        tv_text.setTypeface(copperplateGothicLight);
        tv_text.setText(""+message);
        ImageView iv_error = (ImageView) customsnackView.findViewById(R.id.iv_error);

        // We can also customize the above controls

        layout.setPadding(0,0,0,0);
        layout.addView(customsnackView, 0);

        customSnackBar.show();
    }


}
