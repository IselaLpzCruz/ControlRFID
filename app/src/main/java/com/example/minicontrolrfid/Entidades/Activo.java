package com.example.minicontrolrfid.Entidades;

import java.util.Date;

public class Activo
{
    private int IdActivo;
    private String CodigoActivo;
    private String Descripcion;
    private String EPC;
    private String Barcode;
    private Integer IdUbicacion;
    private String Marca;
    private String Modelo;
    private String NumeroSerie;
    private boolean Activo;
    private Date FechaAlta;
    private Date FechaMod;
    private String UsuarioAlta;
    private String UsuarioMod;

    public Activo()
    {
    }

    public Activo(
            int idActivo,
            String codigoActivo,
            String descripcion,
            String EPC,
            String barcode,
            Integer idUbicacion,
            String marca,
            String modelo,
            String numeroSerie,
            boolean activo,
            Date fechaAlta,
            Date fechaMod,
            String usuarioAlta,
            String usuarioMod)
    {
        IdActivo = idActivo;
        CodigoActivo = codigoActivo;
        Descripcion = descripcion;
        this.EPC = EPC;
        Barcode = barcode;
        IdUbicacion = idUbicacion;
        Marca = marca;
        Modelo = modelo;
        NumeroSerie = numeroSerie;
        Activo = activo;
        FechaAlta = fechaAlta;
        FechaMod = fechaMod;
        UsuarioAlta = usuarioAlta;
        UsuarioMod = usuarioMod;
    }

    public int getIdActivo()
    {
        return IdActivo;
    }

    public void setIdActivo(int idActivo)
    {
        IdActivo = idActivo;
    }

    public String getCodigoActivo()
    {
        return CodigoActivo;
    }

    public void setCodigoActivo(String codigoActivo)
    {
        CodigoActivo = codigoActivo;
    }

    public String getDescripcion()
    {
        return Descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        Descripcion = descripcion;
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

    public Integer getIdUbicacion()
    {
        return IdUbicacion;
    }

    public void setIdUbicacion(Integer idUbicacion)
    {
        IdUbicacion = idUbicacion;
    }

    public String getMarca()
    {
        return Marca;
    }

    public void setMarca(String marca)
    {
        Marca = marca;
    }

    public String getModelo()
    {
        return Modelo;
    }

    public void setModelo(String modelo)
    {
        Modelo = modelo;
    }

    public String getNumeroSerie()
    {
        return NumeroSerie;
    }

    public void setNumeroSerie(String numeroSerie)
    {
        NumeroSerie = numeroSerie;
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
