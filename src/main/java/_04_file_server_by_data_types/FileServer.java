package _04_file_server_by_data_types;

import _04_file_server_by_data_types.http.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * WARNING: For the sake of simplicity, this is NOT a fully valid HTTP server.
 * It does not correctly implement all aspects of the HTTP specification.
 */

public class FileServer {

    public static void main(String[] args) {

        if ( args.length != 2 ) {
            System.err.println("Usage: FileServer <rootPath> <port>");
            System.exit(1);
        }

        Path rootPath;
        int port;
        try {
            rootPath = Paths.get(args[0]);
            if ( ! Files.exists(rootPath) || ! Files.isDirectory(rootPath) ) {
                System.err.println("The given path does not exist or is not a directory: " + args[0]);
                System.exit(1);
            }
            port = Integer.parseInt(args[1]);

            start(rootPath, port);

        } catch (NumberFormatException e) {
            System.err.println("The given port is not a number: " + args[1]);
            e.printStackTrace();
            System.exit(1);
        } catch (InvalidPathException e) {
            System.err.println("The given path is not valid: " + args[0]);
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Could not start server on port " + args[1]);
            e.printStackTrace();
            System.exit(1);
        }


    }

    private static String cleanPath(String path) {
        // Default to index.html if the path is just a directory
        if (path.endsWith("/")) {
            path += "index.html";
        }

        // Remove the leading / if necessary to make this a relative path
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    private static void start(Path rootPath, int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {

                // Wait for a connection from a client
                try (Socket clientSocket = serverSocket.accept()) {

                    try {

                        var request = Http.getRequest(clientSocket);

                        // This may happen when the client disconnects from the socket
                        if (request == null) {
                            continue;
                        }

                        handleRequest(rootPath, clientSocket, request);

                    } catch (Exception e) {
                        // Gracefully handle any exceptions that occur so the server can continue handling requests
                        System.err.println("ERROR: " + e.getMessage());
                        e.printStackTrace();

                        Http.sendResponse(clientSocket, new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred on the server", ContentType.PLAIN));
                    }


                }
            }
        }
    }

    private static void handleRequest(Path rootPath, Socket clientSocket, HttpRequest request) throws IOException {
        var path = cleanPath(request.getUri().getPath());
        System.out.printf("%s %s%n", request.getMethod(), path);

        var contentType = ContentType.HTML;

        var method = request.getMethod();
        if (method == HttpMethod.GET) {

            // This is just a silly URL that allows the user to manually 'crash' the server
            // Allows us to test the error handling
            if (path.equals("crash")) {
                throw new RuntimeException("User requested crash");
            } else if (path.equals("showTheQueryString")) {

                var queryData = "";
                for (var entry : QueryStringParams.parse(request.getUri().getQuery()).entrySet()) {
                    queryData += "<li>" + entry.getKey() + " = " + entry.getValue() + "</li>";
                }

                var responseBody = """
                    <!DOCTYPE html>
                    <html>
                        <head>
                            <title>Query String Data</title>
                        </head>
                        <body>
                            <h1>Query String Data</h1>
                            <ul>
                            <!--QUERY-DATA-->
                            </ul>
                        </body>
                    </html>
                    """;
                responseBody = responseBody.replace("<!--QUERY-DATA-->", queryData);

                Http.sendResponse(clientSocket, new HttpResponse(responseBody, ContentType.HTML));
            } else {
                var filePath = rootPath.resolve(Paths.get(path));
                if (Files.exists(filePath)) {
                    contentType = ContentType.fromPath(filePath);
                    Http.sendResponse(clientSocket, new HttpResponse(Files.readAllBytes(filePath), contentType));
                } else {
                    Http.sendResponse(clientSocket, new HttpResponse(HttpStatus.NOT_FOUND, "Sorry, that resource could not be found", ContentType.PLAIN));
                }
            }
        } else if (method == HttpMethod.POST) {
            if (path.equals("showThePost")) {

                var postData = "";
                var body = new String(request.getBody(), StandardCharsets.UTF_8);
                for (var entry : QueryStringParams.parse(body).entrySet()) {
                    postData += "<li>" + entry.getKey() + " = " + entry.getValue() + "</li>";
                }

                var responseBody = """
                    <!DOCTYPE html>
                    <html>
                        <head>
                            <title>POST Data</title>
                        </head>
                        <body>
                            <h1>POST Data</h1>
                            <p>
                            <!--POST-DATA-->
                            </p>
                        </body>
                    </html>
                    """;
                responseBody = responseBody.replace("<!--POST-DATA-->", postData);

                Http.sendResponse(clientSocket, new HttpResponse(responseBody, ContentType.HTML));
            } else {
                Http.sendResponse(clientSocket, new HttpResponse(HttpStatus.NOT_FOUND, "Sorry, that resource could not be found", ContentType.PLAIN));
            }
        }
    }
}
