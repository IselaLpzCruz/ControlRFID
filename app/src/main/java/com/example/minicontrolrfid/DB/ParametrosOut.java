package com.example.minicontrolrfid.DB;

public class ParametrosOut {

    private String nombre;
    private String valor;
    private int tipo;

    public ParametrosOut() {
    }

    public ParametrosOut(String nombre, String valor, int tipo) {
        this.nombre = nombre;
        this.valor = valor;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
