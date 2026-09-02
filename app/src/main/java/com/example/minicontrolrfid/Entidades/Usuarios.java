package com.example.minicontrolrfid.Entidades;

import java.io.Serializable;
import java.util.Date;

public class Usuarios implements Serializable
{
    private int IdUsuario;
    private String Nombre;
    private String Usuario;
    private String Password;
    private boolean Activo;
    private Date FechaAlta;
    private Date FechaMod;
    private String UsuarioAlta;
    private String UsuarioMod;

    public Usuarios()
    {
    }

    public Usuarios(
            int idUsuario,
            String nombre,
            String usuario,
            String password,
            boolean activo,
            Date fechaAlta,
            Date fechaMod,
            String usuarioAlta,
            String usuarioMod)
    {
        IdUsuario = idUsuario;
        Nombre = nombre;
        Usuario = usuario;
        Password = password;
        Activo = activo;
        FechaAlta = fechaAlta;
        FechaMod = fechaMod;
        UsuarioAlta = usuarioAlta;
        UsuarioMod = usuarioMod;
    }

    public int getIdUsuario()
    {
        return IdUsuario;
    }

    public void setIdUsuario(int idUsuario)
    {
        IdUsuario = idUsuario;
    }

    public String getNombre()
    {
        return Nombre;
    }

    public void setNombre(String nombre)
    {
        Nombre = nombre;
    }

    public String getUsuario()
    {
        return Usuario;
    }

    public void setUsuario(String usuario)
    {
        Usuario = usuario;
    }

    public String getPassword()
    {
        return Password;
    }

    public void setPassword(String password)
    {
        Password = password;
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
