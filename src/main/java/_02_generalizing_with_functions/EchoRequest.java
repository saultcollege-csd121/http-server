package _02_generalizing_with_functions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static _02_generalizing_with_functions.HttpUtils.*;

public class EchoRequest {

    // This is the entry point of a Java program.  A function named 'main'.
    public static void main(String[] args) {

        int port = 8080;

        try ( ServerSocket serverSocket = new ServerSocket(port) ) {

            System.out.println("Server started on port " + port);

            while (true) {

                // Wait for a connection from a client
                try ( Socket clientSocket = serverSocket.accept() ) {

                    // When we get one, print the IP address to the console
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                    var request = getHttpRequestString(clientSocket);

                    if (request.isBlank()) {
                        continue;
                    }

                    sendHttpMessage(clientSocket, "Here are the contents of the request you just made:\n" + request);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
