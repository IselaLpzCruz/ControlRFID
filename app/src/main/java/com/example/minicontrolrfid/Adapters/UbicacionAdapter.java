package com.example.minicontrolrfid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.minicontrolrfid.Entidades.Ubicacion;
import com.example.minicontrolrfid.R;

import java.util.List;

public class UbicacionAdapter extends BaseAdapter {

    private Context context;
    private List<Ubicacion> lista;

    public UbicacionAdapter(Context context, List<Ubicacion> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView clave;
        TextView descripcion;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.epclist_ubicacion, parent, false);

            holder = new ViewHolder();
            holder.clave = convertView.findViewById(R.id.txtClaveUbicacion);
            holder.descripcion = convertView.findViewById(R.id.txtDescripcionUbicacion);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Ubicacion ubicacion = lista.get(position);

        holder.clave.setText(ubicacion.getClave());
        holder.descripcion.setText(ubicacion.getDescripcion());

        return convertView;
    }

    public void setLista(List<Ubicacion> nueva) {
        this.lista = nueva;
        notifyDataSetChanged();
    }
}
