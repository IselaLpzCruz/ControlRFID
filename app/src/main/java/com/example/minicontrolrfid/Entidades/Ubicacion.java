package com.example.minicontrolrfid.Entidades;

import java.util.Date;

public class Ubicacion
{
    private int IdUbicacion;
    private String Clave;
    private String Descripcion;
    private boolean Activo;
    private Date FechaAlta;
    private Date FechaMod;
    private String UsuarioAlta;
    private String UsuarioMod;

    public Ubicacion()
    {
    }

    public Ubicacion(
            int idUbicacion,
            String clave,
            String descripcion,
            boolean activo,
            Date fechaAlta,
            Date fechaMod,
            String usuarioAlta,
            String usuarioMod)
    {
        IdUbicacion = idUbicacion;
        Clave = clave;
        Descripcion = descripcion;
        Activo = activo;
        FechaAlta = fechaAlta;
        FechaMod = fechaMod;
        UsuarioAlta = usuarioAlta;
        UsuarioMod = usuarioMod;
    }

    public int getIdUbicacion()
    {
        return IdUbicacion;
    }

    public void setIdUbicacion(int idUbicacion)
    {
        IdUbicacion = idUbicacion;
    }

    public String getClave()
    {
        return Clave;
    }

    public void setClave(String clave)
    {
        Clave = clave;
    }

    public String getDescripcion()
    {
        return Descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        Descripcion = descripcion;
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
