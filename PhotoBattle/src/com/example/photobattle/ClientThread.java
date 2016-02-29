package com.example.photobattle;

import java.io.*;
import java.net.Socket;

/**
 * Created by Tom on 29/02/2016.
 */
public class ClientThread extends Thread {

    Socket serverSocket;

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

                if (com.getTypeAction().equals("setcoo")) {

                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
