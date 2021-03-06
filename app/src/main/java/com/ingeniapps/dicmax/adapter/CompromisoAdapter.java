package com.ingeniapps.dicmax.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.Text.FontStylerView;
import com.ingeniapps.dicmax.beans.Compromiso;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CompromisoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Compromiso> listadoCompromisos;

    public final int TYPE_COMPROMISO=0;
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
        void onItemClick(Compromiso compromiso);
    }

    private final CompromisoAdapter.OnItemClickListener listener;

    public CompromisoAdapter(Activity activity, ArrayList<Compromiso> listadoCompromisos, CompromisoAdapter.OnItemClickListener listener)
    {
        this.activity=activity;
        this.listadoCompromisos=listadoCompromisos;
        vars=new vars();
        sharedPreferences=new gestionSharedPreferences(this.activity);
        this.listener=listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType==TYPE_COMPROMISO)
        {
            return new CompromisoHolder(inflater.inflate(R.layout.compromisos_layout_row,parent,false));
        }
        else
        {
            return new LoadHolder(inflater.inflate(R.layout.compromisos_layout_row,parent,false));
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

        if(getItemViewType(position)==TYPE_COMPROMISO)
        {
            ((CompromisoHolder)holder).bindData(listadoCompromisos.get(position));
        }

      /*  if(position>previousPosition)
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

        if(listadoCompromisos.get(position).getType().equals("compromiso"))
        {
            return TYPE_COMPROMISO;
        }
        else
        {
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount()
    {
        return listadoCompromisos.size();
    }

    public class CompromisoHolder extends RecyclerView.ViewHolder
    {
        public FontStylerView fecCompro;
        public FontStylerView valPendiente;
        public FontStylerView numDiasVerde;
        public FontStylerView numDiasRojo;
        private NumberFormat numberFormat=NumberFormat.getNumberInstance(Locale.GERMAN);


        public CompromisoHolder(View view)
        {
            super(view);
            fecCompro=(FontStylerView) view.findViewById(R.id.fecCompro);
            valPendiente=(FontStylerView) view.findViewById(R.id.valPendiente);

            numDiasVerde=(FontStylerView) view.findViewById(R.id.numDiasVerde);
            numDiasRojo=(FontStylerView) view.findViewById(R.id.numDiasRojo);

        }

        void bindData(final Compromiso compromiso)
        {
            fecCompro.setText(compromiso.getFecCompro());
            valPendiente.setText("$"+numberFormat.format(Double.parseDouble(compromiso.getValPendiente())));
            if(TextUtils.equals(String.valueOf(compromiso.getNumDias().charAt(0)),"-"))//SI ES NEGATIVO EL COMPROMISO
            {
                numDiasVerde.setVisibility(View.GONE);
                numDiasRojo.setVisibility(View.VISIBLE);
                numDiasRojo.setText(compromiso.getNumDias());
            }
            else//SI EL COMPROMISO ES POSITIVO
            {
                numDiasVerde.setText(compromiso.getNumDias());
            }

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    listener.onItemClick(compromiso);
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

    public List<Compromiso> getCompromisosList()
    {
        return listadoCompromisos;
    }

}
