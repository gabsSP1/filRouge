package com.example.photobattle;

import android.content.Context;
import android.content.Intent;

import java.io.*;
import java.net.Socket;

/**
 * Thread pour la lecture des informations sur le Socket du serveur
 *
 * @author Tom
 */
public class ClientThread extends Thread {
    Context context;
    private Socket serverSocket;
    ClientThread(Socket s, Context context) {
        serverSocket = s;
        this.context=context;
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
                    /*MainGamePanel.persoTwo.setX(Map.pixelToDp((int)(com.getcoordX()*BazarStatic.ratio+BazarStatic.deltaWidth)));
                    MainGamePanel.persoTwo.setY(Map.pixelToDp((int)(com.getcoordY()*BazarStatic.ratio+BazarStatic.deltaHeight)));*/
                }

                //Si la commande est un envoie de map
                if (com.getTypeAction().equals("sendmap")) {
                    BazarStatic.map = com.getMap();
                    BazarStatic.map.convert();
                }

                if (com.getTypeAction().equals("launch")) {
                    Intent intentMyAccount = new Intent(context, Game.class);
                    intentMyAccount.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentMyAccount);
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
