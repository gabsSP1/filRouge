package com.example.photobattle;

/**
 * Created by Tom on 21/02/2016.
 */

import java.io.*;
import java.net.*;

public class ServerThread extends Thread {

    private Socket clientSocket;
    private String pseudo = "";
    private Server serverFrom;


    ServerThread(Socket s, Server se) {
        this.clientSocket = s;
        this.serverFrom = se;
    }

    /**
     * Le run du thread
     * Lit depuis le socket de l'utilisateur et envoie les informations correspondantes à la classe Server en fonction de la commande reçue
     */
    public void run() {
        Command com;

        try {
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            while (true) {
                com = (Command) ois.readObject();

                //Si on veut bouger le joueur 1
                if (com.getTypeAction().equals("setcooj1")) {

                }

                //Si on veut bouger le joueur 2
                else if (com.getTypeAction().equals("setcooj2")) {

                }


            }
        } catch (Exception e) {
            System.err.println("Déconnexion d'un client");
        }
    }

}