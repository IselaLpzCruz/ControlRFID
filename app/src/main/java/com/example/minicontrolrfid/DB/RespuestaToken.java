package com.example.minicontrolrfid.DB;

public class RespuestaToken {

    private String usuario;
    private String expira;
    private String tokenString;

    public RespuestaToken() {
    }

    public RespuestaToken(String usuario, String expira, String tokenString) {
        this.usuario = usuario;
        this.expira = expira;
        this.tokenString = tokenString;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getExpira() {
        return expira;
    }

    public void setExpira(String expira) {
        this.expira = expira;
    }

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }
}
