package com.example.photobattle;

import java.io.*;
import java.net.Socket;

/**
 * Created by Tom on 29/02/2016.
 */
public class Client {

    Socket serverSocket;
    ObjectOutputStream oos;
    boolean isHost;


    Client(String host, int port, boolean iH) {
        try {
            serverSocket = new Socket(host, port);
            oos = new ObjectOutputStream(serverSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        isHost = iH;
    }

    void runListeningThread() {
        ClientThread ct = new ClientThread(serverSocket);
        ct.start();
    }

    void sendCoordinates(int x, int y) {
        Command com;
        if (isHost) {
            com = new Command("setcooj1", x, y, 0, 0);
        } else {
            com = new Command("setcooj2", 0, 0, x, y);
        }

        try {
            oos.writeObject(com);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
