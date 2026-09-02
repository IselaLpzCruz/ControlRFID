package com.example.minicontrolrfid.DB;

import java.util.ArrayList;
import java.util.List;

public class Peticion {

    private String nombreProcedimiento;
    private List<Parametro> parametros;
    private List<ParametrosOut> parametrosOut;

    public Peticion() {
        this.parametros = new ArrayList<Parametro>();
        this.parametrosOut = new ArrayList<ParametrosOut>();
    }

    public Peticion(String nombreProcedimiento, List<Parametro> parametros, List<ParametrosOut> parametrosOut) {
        this.nombreProcedimiento = nombreProcedimiento;
        this.parametros = parametros;
        this.parametrosOut = parametrosOut;
    }

    public Peticion(String nombreProcedimiento) {
        this.nombreProcedimiento = nombreProcedimiento;
        this.parametros = new ArrayList<Parametro>();
        this.parametrosOut = new ArrayList<ParametrosOut>();
    }

    public void AgregarParametro(String nombre, String valor, int tipo) {
        if (parametros == null) {
            parametros = new ArrayList<Parametro>();
        }
        parametros.add(new Parametro(nombre, valor, tipo));
    }

    public void AgregarParametroOut(String nombre, String valor, int tipo) {
        if (parametrosOut == null) {
            this.parametrosOut = new ArrayList<ParametrosOut>();
        }
        parametrosOut.add(new ParametrosOut(nombre, valor, tipo));
    }

    public String getNombreProcedimiento() {
        return nombreProcedimiento;
    }

    public void setNombreProcedimiento(String nombreProcedimiento) {
        this.nombreProcedimiento = nombreProcedimiento;
    }

    public List<Parametro> getParametros() {
        return parametros;
    }

    public void setParametros(List<Parametro> parametros) {
        this.parametros = parametros;
    }

    public List<ParametrosOut> getParametrosOut() {
        return parametrosOut;
    }

    public void setParametrosOut(List<ParametrosOut> parametrosOut) {
        this.parametrosOut = parametrosOut;
    }
}
