package com.example.photobattle;

/**
 * Created by Tom on 21/02/2016.
 */

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Server {

    static Server s = new Server();


    public static void launchServer(int port) {
        ServerSocket listenSocket;

        try {
            listenSocket = new ServerSocket(port); //port
            System.out.println("Server ready...");

            Socket clientSocket = listenSocket.accept();
            System.out.println("Connexion from:" + clientSocket.getInetAddress());
            ServerThread ct = new ServerThread(clientSocket, s);
            ct.start();
            ObjectOutputStream socOut = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (Exception e) {
            System.err.println("Error in Server:" + e);
        }
    }
}
