package com.ingeniapps.dicmax.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.activity.CambioClave;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Contacto extends Fragment
{

    EditText editTextNombreContacto,editTextEmailContacto,editTextCelularContacto,editTextMensajeContacto;
    TextInputLayout input_layout_editTextNombreContacto;
    TextInputLayout input_layout_clave_editTextEmailContacto;
    TextInputLayout input_layout_clave_editTextCelularContacto;
    TextInputLayout input_layout_editTextMensajeContacto;
    vars vars;
    private ProgressDialog progressDialog;
    private Context context;
    private Typeface copperplateGothicLight;
    private MenuItem menuItem;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        vars=new vars();
        context=getActivity();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        copperplateGothicLight = Typeface.createFromAsset(Contacto.this.getActivity().getAssets(), "fonts/AvenirLTStd-Light.ttf");
        editTextNombreContacto=(EditText)getActivity().findViewById(R.id.editTextNombreContacto);
        input_layout_editTextNombreContacto=(TextInputLayout) getActivity().findViewById(R.id.input_layout_editTextNombreContacto);

        input_layout_editTextNombreContacto.setTypeface(copperplateGothicLight);
        editTextNombreContacto.setTypeface(copperplateGothicLight);
        editTextNombreContacto.addTextChangedListener(new Contacto.GenericTextWatcher(editTextNombreContacto));




        editTextEmailContacto=(EditText)getActivity().findViewById(R.id.editTextEmailContacto);
        input_layout_clave_editTextEmailContacto=(TextInputLayout) getActivity().findViewById(R.id.input_layout_clave_editTextEmailContacto);
        input_layout_clave_editTextEmailContacto.setTypeface(copperplateGothicLight);
        editTextEmailContacto.setTypeface(copperplateGothicLight);
        editTextEmailContacto.addTextChangedListener(new Contacto.GenericTextWatcher(editTextEmailContacto));





        editTextCelularContacto=(EditText)getActivity().findViewById(R.id.editTextCelularContacto);
        input_layout_clave_editTextCelularContacto=(TextInputLayout) getActivity().findViewById(R.id.input_layout_clave_editTextCelularContacto);
        input_layout_clave_editTextCelularContacto.setTypeface(copperplateGothicLight);
        editTextCelularContacto.setTypeface(copperplateGothicLight);
        editTextCelularContacto.addTextChangedListener(new Contacto.GenericTextWatcher(editTextCelularContacto));



        editTextMensajeContacto=(EditText)getActivity().findViewById(R.id.editTextMensajeContacto);
        input_layout_editTextMensajeContacto=(TextInputLayout) getActivity().findViewById(R.id.input_layout_editTextMensajeContacto);
        input_layout_editTextMensajeContacto.setTypeface(copperplateGothicLight);
        editTextMensajeContacto.setTypeface(copperplateGothicLight);
        editTextMensajeContacto.addTextChangedListener(new Contacto.GenericTextWatcher(editTextMensajeContacto));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacto, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_contacto, menu);
         menuItem=menu.findItem(R.id.menu_enviar_contacto);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id == R.id.menu_enviar_contacto)
        {
            if(TextUtils.isEmpty(editTextNombreContacto.getText().toString()))
            {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Por favor, digita tu nombre.", Snackbar.LENGTH_LONG).show();
                return false;
            }
            else
            if(TextUtils.isEmpty(editTextEmailContacto.getText().toString())||!(isValidEmail(editTextEmailContacto.getText().toString())))
            {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Por favor, un email valido.", Snackbar.LENGTH_LONG).show();
                return false;
            }

            else
            if(TextUtils.isEmpty(editTextCelularContacto.getText().toString()))
            {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Por favor, digita tu teléfono.", Snackbar.LENGTH_LONG).show();
                return false;
            }

            else
            if(TextUtils.isEmpty(editTextMensajeContacto.getText().toString()))
            {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Por favor, digita tu mensaje.", Snackbar.LENGTH_LONG).show();
                return false;
            }
            else
            {
                WebServiceContacto(editTextNombreContacto.getText().toString(),editTextEmailContacto.getText().toString(),editTextCelularContacto.getText().toString(),editTextMensajeContacto.getText().toString());
                return true;
            }


        }

        return super.onOptionsItemSelected(item);
    }

    public final static boolean isValidEmail(CharSequence target)
    {
        if (target == null)
        {
            return false;
        }
        else
        {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ControllerSingleton.getInstance().cancelPendingReq("getContacto");
    }


    private void WebServiceContacto(final String nombre, final String email, final String celular, final String mensaje)
    {

        progressDialog = new ProgressDialog(new ContextThemeWrapper(Contacto.this.getActivity(),R.style.AppCompatAlertDialogStyle));
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Un momento...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        String _urlWebService = vars.ipServer.concat("/ws/sendMail");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                            String message = response.getString("message");

                            if(status)
                            {
                                if (!((Activity) context).isFinishing())
                                {
                                    if (progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();

                                    }
                                }
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        ""+message, Snackbar.LENGTH_LONG).show();
                                editTextNombreContacto.setText(null);
                                editTextEmailContacto.setText(null);
                                editTextCelularContacto.setText(null);
                                editTextMensajeContacto.setText(null);
                            }

                            else
                            {
                                if (!((Activity) context).isFinishing())
                                {
                                    if (progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();

                                    }
                                }

                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        ""+message, Snackbar.LENGTH_LONG).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            //progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (error instanceof TimeoutError)
                        {
                            if (!((Activity) context).isFinishing())
                            {
                                if (progressDialog.isShowing())
                                {
                                    progressDialog.dismiss();

                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Contacto.this.getActivity(),R.style.AlertDialogTheme));
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
                if (!((Activity) context).isFinishing())
                {
                    if (progressDialog.isShowing())
                    {
                        progressDialog.dismiss();

                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Contacto.this.getActivity(),R.style.AlertDialogTheme));
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
                if (!((Activity) context).isFinishing())
                {
                    if (progressDialog.isShowing())
                    {
                        progressDialog.dismiss();

                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Contacto.this.getActivity(),R.style.AlertDialogTheme));
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
                if (!((Activity) context).isFinishing())
                {
                    if (progressDialog.isShowing())
                    {
                        progressDialog.dismiss();

                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Contacto.this.getActivity(),R.style.AlertDialogTheme));
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
                if (!((Activity) context).isFinishing())
                {
                    if (progressDialog.isShowing())
                    {
                        progressDialog.dismiss();

                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Contacto.this.getActivity(),R.style.AlertDialogTheme));
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

                if (!((Activity) context).isFinishing())
                {
                    if (progressDialog.isShowing())
                    {
                        progressDialog.dismiss();

                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Contacto.this.getActivity(),R.style.AlertDialogTheme));
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
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("nomContacto", ""+nombre);
                headers.put("desCorreo", ""+email);
                headers.put("numCelular", ""+celular);
                headers.put("desMensaje", ""+mensaje);
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "getContacto");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private class GenericTextWatcher implements TextWatcher
    {
        private View view;

        private GenericTextWatcher(View view)
        {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable)
        {
            if(TextUtils.isEmpty(editTextNombreContacto.getText().toString()))
            {
                menuItem.setEnabled(false);
                return;
            }

            if(TextUtils.isEmpty(editTextEmailContacto.getText().toString()))
            {
                menuItem.setEnabled(false);
                return;
            }



            if(TextUtils.isEmpty(editTextCelularContacto.getText().toString()))
            {
                menuItem.setEnabled(false);
                return;
            }

            if(TextUtils.isEmpty(editTextMensajeContacto.getText().toString()))
            {
                menuItem.setEnabled(false);
                return;
            }

            if(!isValidEmail(editTextEmailContacto.getText().toString()))
            {
                menuItem.setEnabled(false);
                return;
            }

            menuItem.setEnabled(true);
        }
    }






}
