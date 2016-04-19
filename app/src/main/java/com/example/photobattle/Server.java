package com.example.photobattle;

import android.widget.TextView;

import java.io.*;
import java.net.*;

/**
 * Thread acceptant les connexions des deux clients
 *
 * @author Tom
 */
public class Server extends Thread {

    static int port;
    static Socket socJ1;
    static Socket socJ2;
    Command map;
    public Server(int port) {
        Server.port = port;
    }

    /**
     * Run du thread. Accepte la connexion des deux clients
     */
    public void run() {
        ServerSocket listenSocket;

        try {
            listenSocket = new ServerSocket(port);
            System.out.println("Server ready...");

            // Admission du premier Client (l'host)
            socJ1 = listenSocket.accept();
            System.out.println("Connexion from:" + socJ1.getInetAddress());
            ServerThread ct = new ServerThread(socJ1, this);
            ct.start();

            // Admission du deuxième Client
            socJ2 = listenSocket.accept();
            System.out.println("Connexion from:" + socJ2.getInetAddress());
            ServerThread ct2 = new ServerThread(socJ2, this);
            ct2.start();
            try {
                ObjectOutputStream socOut = new ObjectOutputStream(socJ2.getOutputStream());
                socOut.writeObject(map);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Error in Server:" + e);
        }
    }

    /**
     * Envoie une commande aux joueurs concernés
     *
     * @param com
     *            la commande à envoyer
     */
    public void sendCommand(Command com) {
        // Si on veut bouger un joueur sur l'écran du J1
        if (com.getTypeAction().equals("setcooj1")) {
            try {
                ObjectOutputStream socOut = new ObjectOutputStream(socJ1.getOutputStream());
                socOut.writeObject(com);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Si on veut bouger un joueur sur l'écran du J2
        else if (com.getTypeAction().equals("setcooj2")) {
            try {
                ObjectOutputStream socOut = new ObjectOutputStream(socJ2.getOutputStream());
                socOut.writeObject(com);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if(com.getTypeAction().equals("launch"))
        {
            try {
                ObjectOutputStream socOut = new ObjectOutputStream(socJ2.getOutputStream());
                socOut.writeObject(com);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //Si on envoie la map
        else if (com.getTypeAction().equals("sendmap")) {
            map=com;
        }
    }

}
