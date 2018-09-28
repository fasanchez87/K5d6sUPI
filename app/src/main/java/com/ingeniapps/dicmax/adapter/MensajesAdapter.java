package com.ingeniapps.dicmax.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.Text.FontStylerView;
import com.ingeniapps.dicmax.beans.Categoria;
import com.ingeniapps.dicmax.beans.Mensaje;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import java.util.ArrayList;
import java.util.List;

public class MensajesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Mensaje> listadoMensajes;

    public final int TYPE_MENSAJE=0;
    public final int TYPE_LOAD=1;
    private gestionSharedPreferences sharedPreferences;
    private Context context;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading=false, isMoreDataAvailable=true;
    vars vars;
    int previousPosition=0;

    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();

    public interface OnItemClickListener
    {
        void onItemClick(Mensaje mensaje);
    }

    private final MensajesAdapter.OnItemClickListener listener;

    public MensajesAdapter(Activity activity, ArrayList<Mensaje> listadoMensajes, MensajesAdapter.OnItemClickListener listener)
    {
        this.activity=activity;
        this.listadoMensajes=listadoMensajes;
        vars=new vars();
        sharedPreferences=new gestionSharedPreferences(this.activity);
        this.listener=listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType==TYPE_MENSAJE)
        {
            return new MensajeHolder(inflater.inflate(R.layout.mensaje_layout_row,parent,false));
        }
        else
        {
            return new LoadHolder(inflater.inflate(R.layout.mensaje_layout_row,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(position >= getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null)
        {
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if(getItemViewType(position)==TYPE_MENSAJE)
        {
            ((MensajeHolder)holder).bindData(listadoMensajes.get(position));
        }
    }

    @Override
    public int getItemViewType(int position)
    {

      if(listadoMensajes.get(position).getType().equals("mensaje"))
        {
            return TYPE_MENSAJE;
        }
        else
        {
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount()
    {
        return listadoMensajes.size();
    }

    public class MensajeHolder extends RecyclerView.ViewHolder
    {
        public FontStylerView tvTituloPush;
        public FontStylerView tvMensajePush;
        public FontStylerView tvFechaMensajePush;

        public MensajeHolder(View view)
        {
            super(view);
            tvTituloPush = view.findViewById(R.id.tvTituloPush);
            tvMensajePush = view.findViewById(R.id.tvMensajePush);
            tvFechaMensajePush = view.findViewById(R.id.tvFechaMensajePush);
        }

        void bindData(final Mensaje mensaje)
        {
            tvTituloPush.setText(mensaje.getTitMensaje());
            tvMensajePush.setText(mensaje.getDesMensaje());
            long timestamp = Long.parseLong(mensaje.getFecEnvio()) * 1000L;
            CharSequence fecha = DateUtils.getRelativeTimeSpanString(timestamp,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            tvFechaMensajePush.setText(fecha);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    listener.onItemClick(mensaje);
                }
            });
        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder
    {
        public LoadHolder(View itemView)
        {
            super(itemView);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable)
    {
        isMoreDataAvailable = moreDataAvailable;
    }
    /* notifyDataSetChanged is final method so we can't override it
        call adapter.notifyDataChanged(); after update the list
        */
    public void notifyDataChanged()
    {
        notifyDataSetChanged();
        isLoading = false;
    }

    public interface OnLoadMoreListener
    {
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener)
    {
        this.loadMoreListener = loadMoreListener;
    }

    public List<Mensaje> getMensajesList()
    {
        return listadoMensajes;
    }

}
