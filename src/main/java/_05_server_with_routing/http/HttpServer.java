package _05_server_with_routing.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {

    private List<RequestHandler> requestHandlers = new ArrayList<>();

    public void addHandler(RequestHandler handler) {
        requestHandlers.add(handler);
    }

    public void start(int port) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {

                // Wait for a connection from a client
                try (Socket clientSocket = serverSocket.accept()) {

                    try {

                        var request = getRequest(clientSocket);

                        // This may happen when the client disconnects from the socket
                        if (request == null) {
                            continue;
                        }

                        HttpResponse response = null;
                        for (RequestHandler requestHandler : requestHandlers) {
                            response = requestHandler.handle(request);
                            if (response != null) {
                                break;
                            }
                        }

                        if ( response != null ) {
                            sendResponse(clientSocket, response);
                        } else {
                            System.out.println("No handler could handle the request");
                            sendResponse(clientSocket, new HttpResponse(HttpStatus.NOT_FOUND, "Not found", ContentType.PLAIN));
                        }

                    } catch (Exception e) {
                        // Gracefully handle any exceptions that occur so the server can continue handling requests
                        System.err.println("ERROR: " + e.getMessage());
                        e.printStackTrace();

                        sendResponse(clientSocket, new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred on the server", ContentType.PLAIN));
                    }
                }
            }
        }
    }

    /**
     * Get the HTTP request message from a client socket
     * @param clientSocket The socket to read the request from
     * @return The HTTP request
     * @throws IOException If there is an error reading the request
     */
    private  HttpRequest getRequest(Socket clientSocket) throws IOException {
        var inputStream = clientSocket.getInputStream();
        var requestReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        int contentLength = 0;

        // Read the headers
        while ( true ) {
            var line = requestReader.readLine();

            // Get the Content-Length header value
            if ( line != null && line.startsWith("Content-Length: ") ) {
                contentLength = Integer.parseInt(line.substring("Content-Length: ".length()));
            }

            // In an HTTP message, an empty line indicates the end of the headers
            if ( line == null || line.isEmpty() ) {
                break;
            }
            sb.append(line);
            sb.append("\n");
        }

        // If there is a body, read it
        byte[] body;
        if ( contentLength > 0 ) {
            sb.append("\n"); // Separate the headers from the body with a blank line

            // We used a BufferedReader to read the headers, which works well for the text content of headers
            // But bodies are binary data, and BufferedReader is not well-suited for reading binary data
            // So we need to read the body as raw bytes.
            // BUT the BufferedReader may have buffered in some of the body, so we need to capture that here
            var stuffRemainingInBufferedReader = new ByteArrayOutputStream();
            while ( requestReader.ready() ) {
                stuffRemainingInBufferedReader.write(requestReader.read());
            }

            // And then concatenate that with the rest of the body from the original input stream
            // (SequenceInputStreams can be used to concatenate multiple InputStreams)
            var stuffRemainingInBufferedReaderPlusStuffRemainingInOriginalStream = new SequenceInputStream(new ByteArrayInputStream(stuffRemainingInBufferedReader.toByteArray()), inputStream);
            body = new byte[contentLength];
            stuffRemainingInBufferedReaderPlusStuffRemainingInOriginalStream.read(body, 0, contentLength);
        } else {
            body = new byte[0];
        }

        var headString = sb.toString();
        if ( ! headString.isBlank() ) {
            return HttpRequest.parse(headString, body);
        } else {
            return null;
        }

    }

    public static void sendResponse(Socket clientSocket, HttpResponse response) throws IOException {
        var out = clientSocket.getOutputStream();

        // Add some typical headers that are present in most HTTP responses
        response.setHeader("Date", ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
        response.setHeader("Server", "CSD121 Java HTTP Server");
        response.setHeader("Connection", "close");

        out.write(response.toByteArray());
        out.flush();
    }
}
