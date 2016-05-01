package com.example.photobattle;

import android.app.Activity;
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
    Activity connectActivity;
    static Activity gameActivity = null;
    ClientThread(Socket s, Context context, Activity act) {
        serverSocket = s;
        this.context=context;
        this.connectActivity = act;
    }

    /**
     * Run du Thread qui lit les commandes provenant du socket
     */
    public void run() {
        Command com;

        try {
            while (true) {

                ObjectInputStream ois = new ObjectInputStream(serverSocket.getInputStream());
                com =  (Command) ois.readObject();
                    if (com.getTypeAction().startsWith("setcoo")) {
                        if(GameScene.persoTwo!=null) {
                            GameScene.persoTwo.setpX(com.getcoordX());
                            GameScene.persoTwo.setpY(com.getcoordY());
                        }
                    }

                    //Si la commande est un envoie de map
                    if (com.getTypeAction().equals("sendmap")) {
                        BazarStatic.map = com.getMap();
                        BazarStatic.map.convert();
                        Command conf = new Command("okmap", 0, 0);
                        ObjectOutputStream oos = null;
                        try {
                            oos = new ObjectOutputStream(serverSocket.getOutputStream());
                            oos.writeObject(conf);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    if (com.getTypeAction().equals("launch")) {
                        Intent intentMyAccount = new Intent(context, Game.class);
                        intentMyAccount.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentMyAccount);
                    }

                    if (com.getTypeAction().equals("okmap")) connectActivity.runOnUiThread(
                            new Thread() {
                                @Override
                                public void run() {

                                    Connect_activity.addToLog("Map reçue, jeu prêt");
                                    Connect_activity.permitLaunch();

                                }
                            });
                    else if(com.getTypeAction().equals("quit"))
                    {
                        gameActivity.finish();
                        break;
                    }

                    else if(com.getTypeAction().equals("ready"))
                    {
                        ObjectInputStream ois2 = new ObjectInputStream(serverSocket.getInputStream());
                        com = (Command) ois2.readObject();
                        if(com.getTypeAction().equals("ready"))
                        {
                            System.out.println("Tous les joueurs sont prêts");
                        }
                    }
                }
        } catch (IOException e) {
            System.err.println("Deconnexion du serveur");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setGameActivity(Activity gact)
    {
        gameActivity = gact;
    }

}
