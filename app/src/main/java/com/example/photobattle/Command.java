package com.example.photobattle;

/**
 * Created by Tom on 29/02/2016.
 */
public class Command {

    //Informations  envoyer
    private int coordJ1X;
    private int coordJ1Y;
    private int coordJ2X;
    private int coordJ2Y;

    private String typeAction;

    Command(String typeAct, int coJ1X, int coJ1Y, int coJ2X, int coJ2Y) {
        typeAction = typeAct;

        coordJ1X = coJ1X;
        coordJ1Y = coJ1Y;
        coordJ2X = coJ2X;
        coordJ2Y = coJ2Y;
    }

    public int getCoordJ1X() {
        return coordJ1X;
    }

    public int getCoordJ1Y() {
        return coordJ1Y;
    }

    public int getCoordJ2X() {
        return coordJ2X;
    }

    public int getCoordJ2Y() {
        return coordJ2Y;
    }

    public String getTypeAction() {
        return typeAction;
    }
}
