package com.photogame.imarena;

import android.app.Activity;

import java.io.*;
import java.net.*;

/**
 * Thread acceptant les connexions des deux clients
 *
 * @author Tom
 */
public class Server extends Thread {


    static Socket socJ1;
    static Socket socJ2;
    private boolean  ready;
    private ServerThread ct1;
    private ServerThread ct2;
    Command map;
    ServerSocket listenSocket;
    static int port;
    Activity act;
    public Server(int port,Activity pact) {
        Server.port = port;
        act = pact;
        ready=false;
    }

    /**
     * Run du thread. Accepte la connexion des deux clients
     */
    public void run() {


        try {
            listenSocket = new ServerSocket(Connect_activity.PORT);
            System.out.println("Server ready...");
            ready=true;
            // Admission du premier Client (l'host)
            socJ1 = listenSocket.accept();

            System.out.println("Connexion from:" + socJ1.getInetAddress());
            ct1 = new ServerThread(socJ1, this);
            ct1.start();
            sendLog("Server Ready");
            // Admission du deuxième Client
            socJ2 = listenSocket.accept();
            System.out.println("Connexion from:" + socJ2.getInetAddress());
            sendLog("Connecté à " + socJ2.getInetAddress());
            act.runOnUiThread(
                    new Thread() {
                        @Override
                        public void run() {

                            Connect_activity.connexionSucessful();

                        }
                    });
            ct2 = new ServerThread(socJ2, this);
            ct2.start();
            try {
                ObjectOutputStream socOut = new ObjectOutputStream(socJ2.getOutputStream());
                sendLog("Envoi de la map");
                socOut.writeObject(new Command(BazarStatic.map));
                sendLog("Map envoyée");
            } catch (IOException e) {
                System.err.println("Erreur lors de l'envoi de la map");
                sendLog("Erreur lors de l'envoi de la map");
            }
        } catch (Exception e) {
            System.err.println("Error in Server:" + e);
            sendLog("Erreur lors du lancement du serveur");
        }
    }

    /**
     * Envoie une commande aux joueurs concernés
     *
     * @param com
     *            la commande à envoyer
     */
    public void sendCommand(Command com, Socket socketfrom, ServerThread ct) {
        // Si on veut bouger un joueur sur l'écran du J1
        switch (com.getTypeAction()) {
            case "setcooj1":
                try {
                    ObjectOutputStream socOut = new ObjectOutputStream(socJ1.getOutputStream());
                    socOut.writeObject(com);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            // Si on veut bouger un joueur sur l'écran du J2
            case "setcooj2":
                try {
                    ObjectOutputStream socOut = new ObjectOutputStream(socJ2.getOutputStream());
                    socOut.writeObject(com);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case "launch":
                try {
                    ObjectOutputStream socOut = new ObjectOutputStream(socJ2.getOutputStream());
                    socOut.writeObject(com);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case "okmap":
                try {
                    ObjectOutputStream socOut = new ObjectOutputStream(socJ1.getOutputStream());
                    socOut.writeObject(com);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            //Si on envoie la map
            case "sendmap":
                map = com;
                break;
            //Message pour lancer le jeu
            case "ready":
                try {
                    ObjectOutputStream socOut1 = new ObjectOutputStream(socJ1.getOutputStream());
                    ObjectOutputStream socOut2 = new ObjectOutputStream(socJ2.getOutputStream());
                    socOut1.writeObject(com);
                    socOut2.writeObject(com);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            case "quit":
                try {
                    if(socketfrom.equals(socJ1))
                    {
                        ObjectOutputStream socOut= new ObjectOutputStream(socJ2.getOutputStream());
                        socOut.writeObject(com);
                    }
                    else
                    {
                        ObjectOutputStream socOut = new ObjectOutputStream(socJ1.getOutputStream());
                        socOut.writeObject(com);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            case "win": {
                try {
                    if(ct == ct1) {

                        ObjectOutputStream socOut2 = new ObjectOutputStream(socJ2.getOutputStream());

                        socOut2.writeObject(com);
                    }
                    else
                    {
                        ObjectOutputStream socOut1 = new ObjectOutputStream(socJ1.getOutputStream());
                        socOut1.writeObject(com);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
                break;
                case "winRecieved":
                {
                    try {
                        if(ct == ct1) {

                            ObjectOutputStream socOut2 = new ObjectOutputStream(socJ2.getOutputStream());

                            socOut2.writeObject(com);
                        }
                        else
                        {
                            ObjectOutputStream socOut1 = new ObjectOutputStream(socJ1.getOutputStream());
                            socOut1.writeObject(com);
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
            }
        }
    }

    public void sendLog(final String s)
    {
        act.runOnUiThread(
                new Thread() {
                    @Override
                    public void run() {

                        Connect_activity.addToLog(s);

                    }
                });
    }

    public void quitServer()
    {
        if(ct1 != null)
        ct1.quitServerThread();
        if(ct2!=null)
        ct1.quitServerThread();
//        try {
//            socJ1.close();
//            socJ2.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            if(listenSocket!=null) {
                listenSocket.close();
                System.out.println("server killed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
