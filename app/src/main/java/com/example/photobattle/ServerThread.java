package com.example.photobattle;

import java.io.*;
import java.net.*;

/**
 * Thread lisant les commandes envoyées par le client
 */
public class ServerThread extends Thread {

    private Socket clientSocket;
    private Server serverFrom;

    ServerThread(Socket s, Server se) {
        this.clientSocket = s;
        this.serverFrom = se;
    }

    /**
     * Le run du thread Lit depuis le socket de l'utilisateur et envoie la
     * commande au Serveur pour qu'il la distribue correctement
     */
    public void run() {
        Command com;

        try {
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            while (true) {
                com = (Command) ois.readObject();
                System.out.println(com);
                serverFrom.sendCommand(com);
            }

        } catch (IOException e) {
            System.err.println("Deconnexion d'un des clients");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}