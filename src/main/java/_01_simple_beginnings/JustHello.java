package _01_simple_beginnings;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class JustHello {

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

                    // Build the HTTP message we will send back to the client
                    var response = "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: text/plain\r\n"
                            + "Content-Length: 14\r\n\r\n"
                            + "Hello, World!\n";



                    // The client socket gives us an 'output stream' that we can write to,
                    // much like writing to a file
                    var out = clientSocket.getOutputStream();
                    // The write function expects a byte[], so we use the getBytes function to
                    // convert the string into a byte[]
                    out.write(response.getBytes(StandardCharsets.UTF_8));
                    out.flush(); // Flush the output stream to ensure the message is sent
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
