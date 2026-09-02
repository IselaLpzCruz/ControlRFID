package com.example.minicontrolrfid.Entidades;

import java.util.Date;

public class Inventario
{
    private int IdInventario;
    private Date FechaInventario;
    private int IdUsuario;
    private Integer IdUbicacion;
    private String Estatus;
    private boolean Sincronizado;
    private boolean Activo;
    private Date FechaAlta;
    private Date FechaMod;
    private String UsuarioAlta;
    private String UsuarioMod;

    public Inventario()
    {
    }

    public Inventario(
            int idInventario,
            Date fechaInventario,
            int idUsuario,
            Integer idUbicacion,
            String estatus,
            boolean sincronizado,
            boolean activo,
            Date fechaAlta,
            Date fechaMod,
            String usuarioAlta,
            String usuarioMod)
    {
        IdInventario = idInventario;
        FechaInventario = fechaInventario;
        IdUsuario = idUsuario;
        IdUbicacion = idUbicacion;
        Estatus = estatus;
        Sincronizado = sincronizado;
        Activo = activo;
        FechaAlta = fechaAlta;
        FechaMod = fechaMod;
        UsuarioAlta = usuarioAlta;
        UsuarioMod = usuarioMod;
    }

    public int getIdInventario()
    {
        return IdInventario;
    }

    public void setIdInventario(int idInventario)
    {
        IdInventario = idInventario;
    }

    public Date getFechaInventario()
    {
        return FechaInventario;
    }

    public void setFechaInventario(Date fechaInventario)
    {
        FechaInventario = fechaInventario;
    }

    public int getIdUsuario()
    {
        return IdUsuario;
    }

    public void setIdUsuario(int idUsuario)
    {
        IdUsuario = idUsuario;
    }

    public Integer getIdUbicacion()
    {
        return IdUbicacion;
    }

    public void setIdUbicacion(Integer idUbicacion)
    {
        IdUbicacion = idUbicacion;
    }

    public String getEstatus()
    {
        return Estatus;
    }

    public void setEstatus(String estatus)
    {
        Estatus = estatus;
    }

    public boolean getSincronizado()
    {
        return Sincronizado;
    }

    public void setSincronizado(boolean sincronizado)
    {
        Sincronizado = sincronizado;
    }

    public boolean getActivo()
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
