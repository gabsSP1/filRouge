package com.example.photobattle;

import java.io.*;
import java.net.*;

/**
 * Thread lisant les commandes envoyées par le client
 */
public class ServerThread extends Thread {

    private Socket clientSocket;
    private Server serverFrom;
    boolean quit;

    ServerThread(Socket s, Server se) {
        this.clientSocket = s;
        this.serverFrom = se;
        quit = false;
    }

    /**
     * Le run du thread Lit depuis le socket de l'utilisateur et envoie la
     * commande au Serveur pour qu'il la distribue correctement
     */
    public void run() {
        Command com;

        try {

            while (!quit) {
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                com = (Command) ois.readObject();
               serverFrom.sendCommand(com,clientSocket, this);
                if(com.getTypeAction().equals("quit"))
                    break;
            }

        } catch (IOException e) {
            System.err.println("Erreur ServerThread : Deconnexion d'un des clients");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("ServerThread correctement terminé");
    }

    public void quitServerThread()
    {
        quit = true;
    }

}