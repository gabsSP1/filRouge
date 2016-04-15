package com.example.photobattle;

import java.io.*;
import java.net.Socket;

/**
 * Thread pour la lecture des informations sur le Socket du serveur
 *
 * @author Tom
 */
public class ClientThread extends Thread {

    private Socket serverSocket;

    ClientThread(Socket s) {
        serverSocket = s;
    }

    /**
     * Run du Thread qui lit les commandes provenant du socket
     */
    public void run() {
        Command com;

        try {
            ObjectInputStream ois = new ObjectInputStream(serverSocket.getInputStream());
            while (true) {
                com = (Command) ois.readObject();

                // Si la commande est un déplacement de l'autre joueur
                if (com.getTypeAction().startsWith("setcoo")) {

                }

                //Si la commande est un envoie de map
                if (com.getTypeAction().equals("sendmap")) {

                }
            }

        } catch (IOException e) {
            System.err.println("Deconnexion du serveur");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
