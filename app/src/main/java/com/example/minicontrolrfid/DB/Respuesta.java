package com.example.minicontrolrfid.DB;

import java.util.List;

public class Respuesta {

    private boolean exito;
    private String resultado;
    private String error;
    private List<ParametrosOut> parametrosOut;

    public Respuesta() {
    }

    public Respuesta(boolean exito, String resultado, String error, List<ParametrosOut> parametrosOut) {
        this.exito = exito;
        this.resultado = resultado;
        this.error = error;
        this.parametrosOut = parametrosOut;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<ParametrosOut> getParametrosOut() {
        return parametrosOut;
    }

    public void setParametrosOut(List<ParametrosOut> parametrosOut) {
        this.parametrosOut = parametrosOut;
    }
}
