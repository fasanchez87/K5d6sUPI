package com.ingeniapps.dicmax.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.Text.FontStylerView;
import com.ingeniapps.dicmax.beans.Ciudad;
import com.ingeniapps.dicmax.beans.Operador;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import java.util.ArrayList;
import java.util.List;

public class OperadorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Operador> listadoOperadores;

    public final int TYPE_OPERADOR=0;
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
        void onItemClick(Operador operador);
    }

    private final OperadorAdapter.OnItemClickListener listener;

    public OperadorAdapter(Activity activity, ArrayList<Operador> listadoOperadores, OperadorAdapter.OnItemClickListener listener)
    {
        this.activity=activity;
        this.listadoOperadores=listadoOperadores;
        vars=new vars();
        sharedPreferences=new gestionSharedPreferences(this.activity);
        this.listener=listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType==TYPE_OPERADOR)
        {
            return new OperadorHolder(inflater.inflate(R.layout.operador_layout_row,parent,false));
        }
        else
        {
            return new LoadHolder(inflater.inflate(R.layout.operador_layout_row,parent,false));
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

        if(getItemViewType(position)==TYPE_OPERADOR)
        {
            ((OperadorHolder)holder).bindData(listadoOperadores.get(position));
        }
    }

    @Override
    public int getItemViewType(int position)
    {

       if(listadoOperadores.get(position).getType().equals("operador"))
        {
            return TYPE_OPERADOR;
        }
        else
        {
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount()
    {
        return listadoOperadores.size();
    }

    public class OperadorHolder extends RecyclerView.ViewHolder
    {
        public FontStylerView nombreOperador;
        public ImageView imgOperador;

        public OperadorHolder(View view)
        {
            super(view);
            nombreOperador=(FontStylerView) view.findViewById(R.id.nombreOperador);
            imgOperador=(ImageView) view.findViewById(R.id.imgOperador);
        }

        void bindData(final Operador operador)
        {
            nombreOperador.setText(operador.getNomOperador());

            Glide.with(activity).
                    load(operador.getUrlImagen().toString()).
                    thumbnail(0.5f).into(imgOperador);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    listener.onItemClick(operador);
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

    public List<Operador> getOperadorList()
    {
        return listadoOperadores;
    }

}
