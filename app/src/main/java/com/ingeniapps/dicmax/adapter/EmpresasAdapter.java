package com.ingeniapps.dicmax.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.ingeniapps.dicmax.activity.DetalleEmpresa;
import com.ingeniapps.dicmax.beans.Categoria;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.beans.Empresa;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.util.MyAnimationUtils;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import java.util.ArrayList;
import java.util.List;

public class EmpresasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Empresa> listadoEmpresas;

    public final int TYPE_NOTICIA=0;
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
        void onItemClick(Empresa empresa);
    }

    private final EmpresasAdapter.OnItemClickListener listener;

    public EmpresasAdapter(Activity activity, ArrayList<Empresa> listadoEmpresas,EmpresasAdapter.OnItemClickListener listener)
    {
        this.activity=activity;
        this.listadoEmpresas=listadoEmpresas;
        vars=new vars();
        sharedPreferences=new gestionSharedPreferences(this.activity);
        this.listener=listener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType==TYPE_NOTICIA)
        {
            return new EmpresasHolder(inflater.inflate(R.layout.empresas_layout_row,parent,false));
        }
        else
        {
            return new LoadHolder(inflater.inflate(R.layout.empresas_layout_row,parent,false));
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

        if(getItemViewType(position)==TYPE_NOTICIA)
        {
            ((EmpresasHolder)holder).bindData(listadoEmpresas.get(position));
        }

       /* if(position>previousPosition)
        {
            new MyAnimationUtils().animate(holder,true);
        }
        else
        {
            new MyAnimationUtils().animate(holder,false);
        }

        previousPosition=position;*/

    }

    @Override
    public int getItemViewType(int position)
    {

       if(listadoEmpresas.get(position).getType().equals("empresa"))
        {
            Log.i("TYPE","NOTICIA");
            return TYPE_NOTICIA;
        }
        else
        {
            Log.i("TYPE","LOAD");
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount()
    {
        return listadoEmpresas.size();
    }

    public class EmpresasHolder extends RecyclerView.ViewHolder
    {
        public ImageView imagenEmpresa;
        public TextView nomEmpresa;
        public TextView dirEmpresa;
        public TextView descEmpresa;

        public EmpresasHolder(View view)
        {
            super(view);
            imagenEmpresa=(ImageView) view.findViewById(R.id.imagenEmpresa);
            nomEmpresa=(TextView) view.findViewById(R.id.nombreEmpresa);
            dirEmpresa=(TextView) view.findViewById(R.id.direccionEmpresa);
            descEmpresa=(TextView) view.findViewById(R.id.descEmpresa);
        }

        void bindData(final Empresa empresa)
        {
            if(empresa.getImagenEmpresa().equals(""))
            {
                imagenEmpresa.setImageResource(R.drawable.ic_uno);
            }

            else
            {
                Glide.with(activity).
                        load(empresa.getImagenEmpresa().toString()).
                        thumbnail(0.5f).into(imagenEmpresa);
            }

            nomEmpresa.setText(empresa.getNomEmpresa());
            dirEmpresa.setText(empresa.getDirEmpresa());
            descEmpresa.setText(empresa.getDescEmpresa());

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    listener.onItemClick(empresa);
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

    public List<Empresa> getNoticiasList()
    {
        return listadoEmpresas;
    }

}
