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

            while (true) {
                ObjectInputStream ois = new ObjectInputStream(serverSocket.getInputStream());
                com = (Command) ois.readObject();

                // Si la commande est un déplacement de l'autre joueur
                if (com.getTypeAction().startsWith("setcoo")) {
                    MainGamePanel.persoTwo.setX(com.getcoordX());
                    MainGamePanel.persoTwo.setY(com.getcoordY());
                }

                //Si la commande est un envoie de map
                if (com.getTypeAction().equals("sendmap")) {
                    MainGamePanel.map = com.getMap();
                }

                if (com.getTypeAction().equals("launch")) {
                    JoinActivity.launch = true;
                }
            }

        } catch (IOException e) {
            System.err.println("Deconnexion du serveur");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
