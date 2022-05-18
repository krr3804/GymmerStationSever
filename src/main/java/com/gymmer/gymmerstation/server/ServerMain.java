package com.gymmer.gymmerstation.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerMain {
    public static Map<String,Socket> usersList = new HashMap();

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            //server binding
            serverSocket = new ServerSocket();
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println(hostAddress);
            serverSocket.bind(new InetSocketAddress(hostAddress, 8080));
            System.out.println("Waiting...");
            // wait until client connects
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client IP : " + socket.getInetAddress());
                new ServerThread(socket).start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
