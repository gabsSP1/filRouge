package com.example.photobattle;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.*;
import java.net.Socket;

/**
 * Created by Tom on 29/02/2016.
 */
public class Client {

    /**
     * Se connecte au serveur et renvoie son Socket
     *
     * @param host
     *            l'ip du serveur ("localhost" si en local)

     * @return le Socket d'échange
     */
    public static Socket connect(String host,  Context context, Activity act) {
        Socket serverSocket = null;
        try {
            serverSocket = new Socket(host, Connect_activity.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientThread ct = new ClientThread(serverSocket, context, act);
        ct.start();

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
        if (isHost) {
            com = new Command("setcooj2", (int)((Map.dpToPixel(x)/BazarStatic.ratio-BazarStatic.deltaWidth)), (int)((Map.dpToPixel(y))/BazarStatic.ratio-BazarStatic.deltaHeight));
        } else {
            com = new Command("setcooj1",  (int)((Map.dpToPixel(x)-BazarStatic.deltaWidth)/BazarStatic.ratio), (int)((Map.dpToPixel(y)-BazarStatic.deltaHeight)/BazarStatic.ratio));
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

    public static  void lauch (Socket serverSocket)
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

}
