package com.example.minicontrolrfid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.minicontrolrfid.Entidades.Inventario;
import com.example.minicontrolrfid.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class InventarioAdapter extends BaseAdapter {

    private Context context;
    private List<Inventario> lista;
    private Map<Integer, String> ubicaciones; // IdUbicacion -> Clave/Descripcion, ya resuelto por la Activity
    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    public InventarioAdapter(Context context, List<Inventario> lista, Map<Integer, String> ubicaciones) {
        this.context = context;
        this.lista = lista;
        this.ubicaciones = ubicaciones;
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
        TextView fecha;
        TextView ubicacion;
        TextView estatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.epclist_inventario, parent, false);

            holder = new ViewHolder();
            holder.fecha = convertView.findViewById(R.id.txtFechaInventario);
            holder.ubicacion = convertView.findViewById(R.id.txtUbicacionInventario);
            holder.estatus = convertView.findViewById(R.id.txtEstatusInventario);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Inventario inventario = lista.get(position);

        holder.fecha.setText(inventario.getFechaInventario() != null
                ? FORMATO_FECHA.format(inventario.getFechaInventario()) : "");

        String nombreUbicacion = (ubicaciones != null) ? ubicaciones.get(inventario.getIdUbicacion()) : null;
        holder.ubicacion.setText(nombreUbicacion != null ? nombreUbicacion : "");

        holder.estatus.setText(inventario.getEstatus());

        return convertView;
    }

    public void setLista(List<Inventario> nueva) {
        this.lista = nueva;
        notifyDataSetChanged();
    }
}
