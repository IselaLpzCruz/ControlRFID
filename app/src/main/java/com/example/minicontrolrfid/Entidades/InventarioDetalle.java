package com.example.minicontrolrfid.Entidades;

import java.util.Date;

public class InventarioDetalle
{
    private int IdInventarioDetalle;
    private int IdInventario;
    private Integer IdActivo;
    private String EPC;
    private String Barcode;
    private String TipoLectura;
    private Date FechaLectura;
    private int Cantidad;
    private boolean Sincronizado;
    private boolean Activo;
    private Date FechaAlta;
    private Date FechaMod;
    private String UsuarioAlta;
    private String UsuarioMod;

    public InventarioDetalle()
    {
    }

    public InventarioDetalle(
            int idInventarioDetalle,
            int idInventario,
            Integer idActivo,
            String EPC,
            String barcode,
            String tipoLectura,
            Date fechaLectura,
            int cantidad,
            boolean sincronizado,
            boolean activo,
            Date fechaAlta,
            Date fechaMod,
            String usuarioAlta,
            String usuarioMod)
    {
        IdInventarioDetalle = idInventarioDetalle;
        IdInventario = idInventario;
        IdActivo = idActivo;
        this.EPC = EPC;
        Barcode = barcode;
        TipoLectura = tipoLectura;
        FechaLectura = fechaLectura;
        Cantidad = cantidad;
        Sincronizado = sincronizado;
        Activo = activo;
        FechaAlta = fechaAlta;
        FechaMod = fechaMod;
        UsuarioAlta = usuarioAlta;
        UsuarioMod = usuarioMod;
    }

    public int getIdInventarioDetalle()
    {
        return IdInventarioDetalle;
    }

    public void setIdInventarioDetalle(int idInventarioDetalle)
    {
        IdInventarioDetalle = idInventarioDetalle;
    }

    public int getIdInventario()
    {
        return IdInventario;
    }

    public void setIdInventario(int idInventario)
    {
        IdInventario = idInventario;
    }

    public Integer getIdActivo()
    {
        return IdActivo;
    }

    public void setIdActivo(Integer idActivo)
    {
        IdActivo = idActivo;
    }

    public String getEPC()
    {
        return EPC;
    }

    public void setEPC(String EPC)
    {
        this.EPC = EPC;
    }

    public String getBarcode()
    {
        return Barcode;
    }

    public void setBarcode(String barcode)
    {
        Barcode = barcode;
    }

    public String getTipoLectura()
    {
        return TipoLectura;
    }

    public void setTipoLectura(String tipoLectura)
    {
        TipoLectura = tipoLectura;
    }

    public Date getFechaLectura()
    {
        return FechaLectura;
    }

    public void setFechaLectura(Date fechaLectura)
    {
        FechaLectura = fechaLectura;
    }

    public int getCantidad()
    {
        return Cantidad;
    }

    public void setCantidad(int cantidad)
    {
        Cantidad = cantidad;
    }

    public boolean isSincronizado()
    {
        return Sincronizado;
    }

    public void setSincronizado(boolean sincronizado)
    {
        Sincronizado = sincronizado;
    }

    public boolean isActivo()
    {
        return Activo;
    }

    public void setActivo(boolean activo)
    {
        Activo = activo;
    }

    public Date getFechaAlta()
    {
        return FechaAlta;
    }

    public void setFechaAlta(Date fechaAlta)
    {
        FechaAlta = fechaAlta;
    }

    public Date getFechaMod()
    {
        return FechaMod;
    }

    public void setFechaMod(Date fechaMod)
    {
        FechaMod = fechaMod;
    }

    public String getUsuarioAlta()
    {
        return UsuarioAlta;
    }

    public void setUsuarioAlta(String usuarioAlta)
    {
        UsuarioAlta = usuarioAlta;
    }

    public String getUsuarioMod()
    {
        return UsuarioMod;
    }

    public void setUsuarioMod(String usuarioMod)
    {
        UsuarioMod = usuarioMod;
    }
}
