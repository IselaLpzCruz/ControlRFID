package com.example.minicontrolrfid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.minicontrolrfid.Entidades.Activo;
import com.example.minicontrolrfid.Entidades.InventarioDetalle;
import com.example.minicontrolrfid.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class InventarioDetalleAdapter extends BaseAdapter {

    private Context context;
    private List<InventarioDetalle> lista;
    private Map<Integer, Activo> activos; // IdActivo -> Activo, ya resuelto por la Activity (para la Descripcion)
    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public InventarioDetalleAdapter(Context context, List<InventarioDetalle> lista, Map<Integer, Activo> activos) {
        this.context = context;
        this.lista = lista;
        this.activos = activos;
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
        TextView descripcion;
        TextView tipoLectura;
        TextView fechaLectura;
        TextView cantidad;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.epclist_inventario_detalle, parent, false);

            holder = new ViewHolder();
            holder.descripcion = convertView.findViewById(R.id.txtDescripcionDetalle);
            holder.tipoLectura = convertView.findViewById(R.id.txtTipoLecturaDetalle);
            holder.fechaLectura = convertView.findViewById(R.id.txtFechaLecturaDetalle);
            holder.cantidad = convertView.findViewById(R.id.txtCantidadDetalle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        InventarioDetalle detalle = lista.get(position);

        Activo activo = (activos != null) ? activos.get(detalle.getIdActivo()) : null;
        holder.descripcion.setText(activo != null ? activo.getDescripcion() : "");

        holder.tipoLectura.setText(detalle.getTipoLectura());

        holder.fechaLectura.setText(detalle.getFechaLectura() != null
                ? FORMATO_FECHA.format(detalle.getFechaLectura()) : "");

        holder.cantidad.setText(String.valueOf(detalle.getCantidad()));

        return convertView;
    }

    public void setLista(List<InventarioDetalle> nueva) {
        this.lista = nueva;
        notifyDataSetChanged();
    }
}
