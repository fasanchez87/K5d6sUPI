package com.ingeniapps.dicmax.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.loader.SpinKitView;
import com.ingeniapps.dicmax.loader.SpriteFactory;
import com.ingeniapps.dicmax.loader.Tipo_Loader;
import com.ingeniapps.dicmax.loader.sprite.Sprite;

import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;

public class Splash extends AppCompatActivity
{
    private static final long SPLASH_SCREEN_DELAY = 3000;
    Timer timer;
    SpinKitView spinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Fabric.with(this,new Crashlytics());
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        //getActionBar().hide();
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Configuramos Loader
        spinKitView = (SpinKitView) findViewById(R.id.spin_kit);
        Tipo_Loader style = Tipo_Loader.values()[2];
        Sprite drawable = SpriteFactory.create(style);
        spinKitView.setIndeterminateDrawable(drawable);

        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(Splash.this, Prelogin.class);
                startActivity(intent);
                finish();
            }
        };

        // Simulate a long loading process on application startup.
        timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        timer.cancel();

    }
}