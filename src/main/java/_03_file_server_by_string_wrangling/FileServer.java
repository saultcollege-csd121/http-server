package _03_file_server_by_string_wrangling;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static _03_file_server_by_string_wrangling.HttpUtils.*;

/**
 * WARNING: For the sake of simplicity, this is NOT a fully valid HTTP server.
 * It does not correctly implement all aspects of the HTTP specification.
 */

public class FileServer {

    public static void main(String[] args) {

        if ( args.length != 2 ) {
            System.err.println("Usage: SimpleFileServer <rootPath> <port>");
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

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server started on port " + port);

                start(serverSocket, rootPath);
            }

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

    private static void start(ServerSocket serverSocket, Path rootPath) throws IOException {
        while (true) {

            // Wait for a connection from a client
            try ( Socket clientSocket = serverSocket.accept() ) {

                try {

                    var request = getHttpRequestString(clientSocket);

                    // Assuming the first line of the request is of the form "HTTPMETHOD /the/url/path?key=val&key=val HTTP/1.1"
                    var requestParts = request.split(" ");      // space sparates HTTP method, path, and version
                    var httpMethod = requestParts[0];
                    var fullUrl = requestParts[1];
                    var urlParts = fullUrl.split("\\?");        // ? separates path from query string
                    var urlPath = urlParts[0];
                    Map<String, String> urlParams = Map.of();
                    if ( urlParts.length > 1 ) {
                        urlParams = getQueryStringParams(urlParts[1]);
                    }

                    System.out.printf("%s %s%n", httpMethod, urlPath);

                    urlPath = cleanPath(urlPath);

                    var contentType = "text/html";

                    if ( httpMethod.equals("GET") ) {

                        // This is just a silly URL that allows the user to manually 'crash' the server
                        // Allows us to test the error handling
                        if (urlPath.equals("crash")) {
                            throw new RuntimeException("User requested crash");
                        } else if ( urlPath.equals("showTheQueryString") ) {
                            var data = """
                                <!DOCTYPE html>
                                <html>
                                    <head>
                                        <title>Query String Data</title>
                                    </head>
                                    <body>
                                        <h1>Query String Data</h1>
                                        <ul>
                            """;
                            for (var entry : urlParams.entrySet()) {
                                data += "<li>" + entry.getKey() + " = " + entry.getValue() + "</li>";
                            }
                            data += """
                                        </ul>
                                    </body>
                                </html>
                            """;
                            sendHttpMessage(clientSocket, data.getBytes(StandardCharsets.UTF_8), "text/html");
                        } else {
                            var path = rootPath.resolve(Paths.get(urlPath));
                            if (Files.exists(path)) {
                                contentType = getContentTypeOf(path);
                                sendHttpMessage(clientSocket, Files.readAllBytes(path), contentType);
                            } else {
                                sendHttpMessage(clientSocket, 404, "Sorry, that resource could not be found".getBytes(StandardCharsets.UTF_8), "text/plain");
                            }
                        }
                    } else if ( httpMethod.equals("POST") ) {
                        if ( urlPath.equals("showThePost") ) {

                            var messageParts = request.split("\n\n"); // \n\n separates head from body
                            Map<String, String> data = Map.of();
                            if ( messageParts.length > 1 ) {
                                data = getQueryStringParams(messageParts[1]);
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
                            """;
                            for (var entry : data.entrySet()) {
                                responseBody += "<li>" + entry.getKey() + " = " + entry.getValue() + "</li>";
                            }
                            responseBody += """
                                        </p>
                                    </body>
                                </html>
                            """;
                            sendHttpMessage(clientSocket, responseBody.getBytes(StandardCharsets.UTF_8), "text/html");
                        } else {
                            sendHttpMessage(clientSocket, 404, "Sorry, that resource could not be found".getBytes(StandardCharsets.UTF_8), "text/plain");
                        }
                    }


                } catch (Exception e) {
                    // Gracefully handle any exceptions that occur so the server can continue handling requests
                    System.err.println("ERROR: " + e.getMessage());
                    e.printStackTrace();

                    sendHttpMessage(clientSocket, 500, "An error occurred on the server".getBytes(StandardCharsets.UTF_8), "text/plain");
                }


            }
        }
    }

    private static HashMap<String, String> getQueryStringParams(String queryString) {
        var urlParams = new HashMap<String, String>();    // query string is a set of key=val pairs
        if ( ! queryString.isBlank() ) {
            var params = queryString.split("&");    // & separates individual query string parameters
            // WARNING: For the sake of simplicity, this implementation does not correctly handle
            // multiple values for the same key
            for (var param : params) {
                var parts = param.split("=");

                // Browsers will encode special characters in URLs, so we need to decode them
                // to get the actual key and value strings
                var key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                var val = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                urlParams.put(key, val);
            }
        }

        return urlParams;
    }
}

