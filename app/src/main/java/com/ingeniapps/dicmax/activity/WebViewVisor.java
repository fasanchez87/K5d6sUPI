package com.ingeniapps.dicmax.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ingeniapps.dicmax.R;

public class WebViewVisor extends AppCompatActivity
{
    private String url;
    WebView myWebView;
    private ProgressDialog progressDialog;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

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

        progressDialog = new ProgressDialog(new android.support.v7.view.ContextThemeWrapper(WebViewVisor.this,R.style.AppCompatAlertDialogStyle));
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        context=this;

        if (savedInstanceState==null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras==null)
            {
                url=null;
            }
            else
            {
                url=extras.getString("url");
            }
        }

        myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            myWebView.getSettings().setMixedContentMode( WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
        }
       // myWebView.setWebViewClient(new WebViewClient());
        //myWebView.loadUrl(url);



        myWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewVisor.this);
                String message = "Error Certificado SSL";
                switch (error.getPrimaryError())
                {
                    case SslError.SSL_UNTRUSTED:
                        message = "El certificado autorizado no es de confianza.";
                        break;
                    case SslError.SSL_EXPIRED:
                        message = "El certificado ha expirado.";
                        break;
                    case SslError.SSL_IDMISMATCH:
                        message = "El nombre de Host del certificado no coincide.";
                        break;
                    case SslError.SSL_NOTYETVALID:
                        message = "El certificado aún no es válido.";
                        break;
                }
                message += " Deseas continuar?";

                builder.setTitle("Error Certificado SSL");
                builder.setMessage(message);
                builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();

            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url)
            {
                if (!((Activity) context).isFinishing())
                {
                    if (progressDialog.isShowing())
                    {
                        progressDialog.dismiss();

                    }
                }
            }
    });

    myWebView.loadUrl(url);


    }




}
