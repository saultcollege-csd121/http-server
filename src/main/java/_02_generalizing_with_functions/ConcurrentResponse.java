package _02_generalizing_with_functions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static _02_generalizing_with_functions.HttpUtils.getHttpRequestString;
import static _02_generalizing_with_functions.HttpUtils.sendHttpMessage;

public class ConcurrentResponse {

    public static void main(String[] args) {
        int port = 8080;

        try ( ServerSocket serverSocket = new ServerSocket(port) ) {

            System.out.println("Server started on port " + port);

            while (true) {

                // Wait for a connection from a client
                Socket clientSocket = serverSocket.accept();

                // When we get one, print the IP address to the console
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                try {
                    var request = getHttpRequestString(clientSocket);

                    if (request.isBlank()) {
                        clientSocket.close();
                        return;
                    }

                    System.out.println("Incoming request:");
                    System.out.println(request);

                    var urlPath = request.split(" ")[1];

                    switch (urlPath) {
                        case "/pthread" -> Thread.ofPlatform().start(() -> {
                            try {
                                Thread.sleep(3000);
                                sendHttpMessage(clientSocket, "Done in 3sec!");
                                clientSocket.close();
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        case "/vthread" -> Thread.ofVirtual().start(() -> {
                            try {
                                Thread.sleep(3000);
                                sendHttpMessage(clientSocket, "Done in 3sec!");
                                clientSocket.close();
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        case "/nothread" -> {
                            try {
                                Thread.sleep(3000);
                                sendHttpMessage(clientSocket, "Done in 3sec!");
                                clientSocket.close();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        default -> {
                            sendHttpMessage(clientSocket, "Try the /vthread, /pthread or /nothread endpoints");
                            clientSocket.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
