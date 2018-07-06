package com.ingeniapps.dicmax.helper;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ingeniapps.dicmax.R;

public class AlertasErrores
{
    private String message;
    private View view;
    private Activity context;
    private Typeface copperplateGothicLight;
    private int SNACKBAR_DURATION = 4000;

    public AlertasErrores(String message, View view, Activity context)
    {
        this.message=message;
        this.view=view;
        this.context=context;
        showMessage();
    }

    public void showMessage()
    {
        final Snackbar customSnackBar = Snackbar.make(view,null,SNACKBAR_DURATION);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) customSnackBar.getView();
        View customsnackView = context.getLayoutInflater().inflate(R.layout.snack_bar_layout, null);

        TextView tv_text = (TextView) customsnackView.findViewById(R.id.tv_text);
        tv_text.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/AvenirLTStd-Light.ttf"));
        tv_text.setText(""+message);
        ImageView iv_error = (ImageView) customsnackView.findViewById(R.id.iv_error);
        // We can also customize the above controls
        layout.setPadding(0,0,0,0);
        layout.addView(customsnackView, 0);
        customSnackBar.show();
    }
}




