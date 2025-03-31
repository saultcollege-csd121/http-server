package _02_generalizing_with_functions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static _02_generalizing_with_functions.HttpUtils.*;

public class DynamicResponse {

    public static void main(String[] args) {

        int port = 8080;

        try ( ServerSocket serverSocket = new ServerSocket(port) ) {

            System.out.println("Server started on port " + port);

            int numRequests = 0;

            while (true) {

                // Wait for a connection from a client
                try ( Socket clientSocket = serverSocket.accept() ) {

                    numRequests += 1;

                    // When we get one, print the IP address to the console
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                    var request = getHttpRequestString(clientSocket);

                    if ( request.isBlank() ) {
                        continue;
                    }

                    // Assuming the first line of the request is of the form "GET /the/url/path HTTP/1.1"
                    var urlPath = request.split(" ")[1];

                    System.out.println("Request for: " + urlPath);

                    var body = "";
                    if (urlPath.equals("/")) {
                        body = "Hello, World!";
                    } else if (urlPath.equals("/goodbye")) {
                        body = "Goodbye, World!";
                    } else if ( urlPath.equals("/numRequests") ) {
                        body = "Number of requests this server has handled since starting: " + numRequests;
                    } else if (urlPath.matches("/roll/d\\d{1,3}")) {
                        var dice = urlPath.split("/")[2];
                        var sides = Integer.parseInt(dice.substring(1));
                        body = "You rolled a %s and got %d".formatted(dice, (int) (Math.random() * sides + 1));
                    } else if ( urlPath.equals("/longTask") ) {
                        Thread.sleep(3000);
                        body = "That took 3sec... or did it?";
                    } else {
                        body = "I don't know what you want";
                    }
                    sendHttpMessage(clientSocket, body);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
