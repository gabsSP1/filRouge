package com.example.photobattle;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Objet echangé sur les Sockets entre serveur et client
 *
 * @author Tom
 */
public class Command implements Serializable {

    private static final long serialVersionUID = -7543408412075826284L;

    // Informations envoyées
    private int coordX;
    private int coordY;

    private Map map;

    private String typeAction;

    public Command(String typeAct, int coX, int coY) {
        typeAction = typeAct;

        coordX = coX;
        coordY = coY;
    }

    public Command(Map map) {
        typeAction = "sendmap";

        this.map = map;
    }

    public int getcoordX() {
        return coordX;
    }

    public int getcoordY() {
        return coordY;
    }

    public String getTypeAction() {
        return typeAction;
    }

    public Map getMap() {
        return map;
    }

    @Override
    public String toString() {
        return "Command [coordX=" + coordX + ", coordY=" + coordY + ", typeAction=" + typeAction + "]";
    }

}