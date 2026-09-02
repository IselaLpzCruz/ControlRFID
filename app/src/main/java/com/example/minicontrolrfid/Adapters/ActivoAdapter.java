package com.example.minicontrolrfid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.minicontrolrfid.Entidades.Activo;
import com.example.minicontrolrfid.R;

import java.util.List;
import java.util.Map;

public class ActivoAdapter extends BaseAdapter {

    private Context context;
    private List<Activo> lista;
    private Map<Integer, String> ubicaciones; // IdUbicacion -> Clave/Descripcion, ya resuelto por la Activity

    public ActivoAdapter(Context context, List<Activo> lista, Map<Integer, String> ubicaciones) {
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
        TextView codigo;
        TextView descripcion;
        TextView epc;
        TextView barcode;
        TextView ubicacion;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.epclist_activo, parent, false);

            holder = new ViewHolder();
            holder.codigo = convertView.findViewById(R.id.txtCodigoActivo);
            holder.descripcion = convertView.findViewById(R.id.txtDescripcionActivo);
            holder.epc = convertView.findViewById(R.id.txtEPCActivo);
            holder.barcode = convertView.findViewById(R.id.txtBarcodeActivo);
            holder.ubicacion = convertView.findViewById(R.id.txtUbicacionActivo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Activo activo = lista.get(position);

        holder.codigo.setText(activo.getCodigoActivo());
        holder.descripcion.setText(activo.getDescripcion());
        holder.epc.setText(activo.getEPC());
        holder.barcode.setText(activo.getBarcode());

        String nombreUbicacion = (ubicaciones != null) ? ubicaciones.get(activo.getIdUbicacion()) : null;
        holder.ubicacion.setText(nombreUbicacion != null ? nombreUbicacion : "");

        return convertView;
    }

    public void setLista(List<Activo> nueva) {
        this.lista = nueva;
        notifyDataSetChanged();
    }
}
