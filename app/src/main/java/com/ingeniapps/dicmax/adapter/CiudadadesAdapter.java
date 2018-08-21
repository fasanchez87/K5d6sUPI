package com.ingeniapps.dicmax.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.ingeniapps.dicmax.beans.Empresa;
import com.ingeniapps.dicmax.sharedPreferences.gestionSharedPreferences;
import com.ingeniapps.dicmax.vars.vars;
import com.ingeniapps.dicmax.volley.ControllerSingleton;

import java.util.ArrayList;
import java.util.List;

public class CiudadadesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Ciudad> listadoCiudades;
    private List<Ciudad> temporarylist;


    public final int TYPE_CIUDAD=0;
    public final int TYPE_LOAD=1;
    private gestionSharedPreferences sharedPreferences;
    private Context context;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading=false, isMoreDataAvailable=true;
    vars vars;
    int previousPosition=0;
    TextView editTextNumCiudades;


    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();

    public interface OnItemClickListener
    {
        void onItemClick(Ciudad ciudad);
    }

    private final CiudadadesAdapter.OnItemClickListener listener;

    public CiudadadesAdapter(Activity activity, ArrayList<Ciudad> listadoCiudades, CiudadadesAdapter.OnItemClickListener listener)
    {
        this.activity=activity;
        this.listadoCiudades=listadoCiudades;
        vars=new vars();
        sharedPreferences=new gestionSharedPreferences(this.activity);
        this.listener=listener;
        temporarylist=listadoCiudades;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType==TYPE_CIUDAD)
        {
            return new CiudadesHolder(inflater.inflate(R.layout.ciudades_layout_row,parent,false));
        }
        else
        {
            return new LoadHolder(inflater.inflate(R.layout.ciudades_layout_row,parent,false));
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                String charString = charSequence.toString();
                if (charString.isEmpty())
                {
                    temporarylist = listadoCiudades;
                }
                else
                {
                    List<Ciudad> filteredList = new ArrayList<>();
                    for (Ciudad row : listadoCiudades)
                    {
                        if (row.getNomCiudad().toLowerCase().contains(charString.toLowerCase()))
                        {
                            filteredList.add(row);
                        }
                    }

                    temporarylist = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = temporarylist;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                temporarylist = (ArrayList<Ciudad>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(position >= getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null)
        {
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if(getItemViewType(position)==TYPE_CIUDAD)
        {
            ((CiudadesHolder)holder).bindData(temporarylist.get(position));
        }
    }

    @Override
    public int getItemViewType(int position)
    {

       if(temporarylist.get(position).getType().equals("ciudades"))
        {
            return TYPE_CIUDAD;
        }
        else
        {
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount()
    {
        return temporarylist.size();
    }

    public class CiudadesHolder extends RecyclerView.ViewHolder
    {
        public FontStylerView nombreCiudadRow;

        public CiudadesHolder(View view)
        {
            super(view);
            nombreCiudadRow=(FontStylerView) view.findViewById(R.id.nombreCiudadRow);
            editTextNumCiudades=(TextView)view.findViewById(R.id.editTextNumCiudades);

        }

        void bindData(final Ciudad ciudad)
        {
            nombreCiudadRow.setText(ciudad.getNomCiudad());

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    listener.onItemClick(ciudad);
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

    public List<Ciudad> getCiudadList()
    {
        return temporarylist;
    }

}
