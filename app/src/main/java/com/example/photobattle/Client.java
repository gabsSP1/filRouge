package com.example.photobattle;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 29/02/2016.
 */
public class Client {


    private static List<ClientThread> clients =new ArrayList<>();
    static Socket serverSocket=null;

    /**
     * Se connecte au serveur et renvoie son Socket
     *
     * @param host
     *            l'ip du serveur ("localhost" si en local)

     * @return le Socket d'échange
     */
    public static Socket connect(String host,  Context context, Activity act) {

        boolean connect= false;
        serverSocket = null;
        try {
            serverSocket = new Socket();
            serverSocket.connect(new InetSocketAddress(host, Connect_activity.PORT),500);
            ClientThread ct = new ClientThread(serverSocket, context, act);
            ct.start();
            clients.add(ct);
        } catch (IOException e) {
            e.printStackTrace();
            serverSocket = null;
        }



        return serverSocket;
    }

    /**
     * Envoie au serveur les nouvelles coordonnées du joueur
     *
     * @param x
     * @param y
     * @param isHost
     *            true si le joueur qui envoie les coordonnées héberge le
     *            serveur (et est donc J1)
     * @param serverSocket
     *            le socket d'échange
     */
    public static void sendCoordinates(int x, int y, boolean isHost, Socket serverSocket) {
        Command com;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(serverSocket.getOutputStream());
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if(BazarStatic.host) {
            com = new Command("setcooj2", x, y);
        }
        else
        {
            com = new Command("setcooj1", x, y);
        }


        try {
            oos.writeObject(com);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie la map pour la partie
     *
     * @param map          la map à envoyer
     * @param serverSocket le socket d'échange
     */
    public static void sendMap(Map map, Socket serverSocket) {
        Command com;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(serverSocket.getOutputStream());
            com = new Command(map);
            oos.writeObject(com);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static  void launch(Socket serverSocket)
    {
        Command com = new Command("launch", 0 , 0);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(serverSocket.getOutputStream());
            oos.writeObject(com);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void sendQuit()
    {
        Command com = new Command("quit", 0 , 0);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(serverSocket.getOutputStream());
            oos.writeObject(com);
        } catch (IOException e) {
            System.err.println("Erreur dans l'envoi du quit");
        }
    }

    public static void sendReady(Socket serverSocket)
    {
        Command com = new Command("ready", 0 , 0);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(serverSocket.getOutputStream());
            oos.writeObject(com);
        } catch (IOException e) {
            System.err.println("Erreur dans l'envoi du ready");
        }
    }

//    public static void main(String[] args) {
//        Socket serverSocket = connect("localhost", 20200);
//        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            String message = stdIn.readLine();
//            if (message.split(" ")[2].equals("true")) {
//                sendCoordinates(Integer.valueOf(message.split(" ")[0]), Integer.valueOf(message.split(" ")[1]), true,
//                        serverSocket);
//            } else {
//                sendCoordinates(Integer.valueOf(message.split(" ")[0]), Integer.valueOf(message.split(" ")[1]), false,
//                        serverSocket);
//            }
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    public static void sendWin()
    {
        Command com = new Command("win", 0 , 0);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(serverSocket.getOutputStream());
            oos.writeObject(com);
        } catch (IOException e) {
            System.err.println("Erreur dans l'envoi du win");
        }
    }

    public static void sendWinRecieved()
    {
        Command com = new Command("winRecieved", 0 , 0);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(serverSocket.getOutputStream());
            oos.writeObject(com);
        } catch (IOException e) {
            System.err.println("Erreur dans l'envoi du win");
        }
    }

    public static void quit()
    {
        for (ClientThread ct : clients)
        {
            ct.quitClient();
        }
        try {
            if(serverSocket != null)
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
