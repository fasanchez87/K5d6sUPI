package com.ingeniapps.dicmax.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.ingeniapps.dicmax.R;
import com.ingeniapps.dicmax.Text.FontStylerView;
import com.ingeniapps.dicmax.beans.Compromiso;
import com.ingeniapps.dicmax.beans.Pago;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PagoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Pago> listadoPagos;

    public final int TYPE_PAGO=0;
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
        void onItemClick(Pago pagos);
    }

    private final PagoAdapter.OnItemClickListener listener;

    public PagoAdapter(Activity activity, ArrayList<Pago> listadoPagos, PagoAdapter.OnItemClickListener listener)
    {
        this.activity=activity;
        this.listadoPagos=listadoPagos;
        vars=new vars();
        sharedPreferences=new gestionSharedPreferences(this.activity);
        this.listener=listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType==TYPE_PAGO)
        {
            return new PagoHolder(inflater.inflate(R.layout.pagos_layout_row,parent,false));
        }
        else
        {
            return new PagoHolder(inflater.inflate(R.layout.pagos_layout_row,parent,false));
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

        if(getItemViewType(position)==TYPE_PAGO)
        {
            ((PagoHolder)holder).bindData(listadoPagos.get(position));
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

        if(listadoPagos.get(position).getType().equals("pago"))
        {
            return TYPE_PAGO;
        }
        else
        {
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount()
    {
        return listadoPagos.size();
    }

    public class PagoHolder extends RecyclerView.ViewHolder
    {
        public FontStylerView tipoTransa;
        public FontStylerView nomEmpresa;
        public FontStylerView fecCompra;
        public FontStylerView valorPago;

        public FontStylerView estPagoRealizado;
        public FontStylerView estPagoPendiente;
        private NumberFormat numberFormat=NumberFormat.getNumberInstance(Locale.GERMAN);


        public PagoHolder(View view)
        {
            super(view);
            tipoTransa=(FontStylerView) view.findViewById(R.id.tipoTransa);
            nomEmpresa=(FontStylerView) view.findViewById(R.id.nomEmpresa);
            fecCompra=(FontStylerView) view.findViewById(R.id.fecCompra);
            valorPago=(FontStylerView) view.findViewById(R.id.valorPago);

            estPagoRealizado=(FontStylerView) view.findViewById(R.id.estPagoRealizado);
            estPagoPendiente=(FontStylerView) view.findViewById(R.id.estPagoPendiente);
        }

        void bindData(final Pago pagos)
        {
            tipoTransa.setText(pagos.getTipTransaccion());
            nomEmpresa.setText(pagos.getNomEmpresa());


            if(TextUtils.equals(String.valueOf(pagos.getFecAprobacion().charAt(0)),"0"))//FECHA NULA POR ESTA PENDIENTE
            {
                fecCompra.setText("Pendiente de aprobación");
            }
            else
            {
                fecCompra.setText(pagos.getFecAprobacion());
            }

            valorPago.setText("$"+numberFormat.format(Double.parseDouble(pagos.getValTransaccion())));

            if(TextUtils.equals(String.valueOf(pagos.getNomEstado()),"Pendiente"))
            {
                estPagoRealizado.setVisibility(View.GONE);
                estPagoPendiente.setVisibility(View.VISIBLE);
                estPagoPendiente.setText(pagos.getNomEstado());
            }
            else
            {
                estPagoRealizado.setText(pagos.getNomEstado());
            }

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    listener.onItemClick(pagos);
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

    public List<Pago> getPagosList()
    {
        return listadoPagos;
    }

}
