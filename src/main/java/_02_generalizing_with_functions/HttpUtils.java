package _02_generalizing_with_functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpUtils {

    /**
     * Send an HTTP message to the client
     * @param socket The socket to send the message over
     * @param messageBody The desired body of the HTTP message.
     * @throws IOException If there is an error sending the message
     */
    public static void sendHttpMessage(Socket socket, String messageBody) throws IOException {
        var out = socket.getOutputStream();
        var httpMessage = createHttpResponseString(messageBody);
        out.write(httpMessage.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    /**
     * Create an HTTP response message with a plain text body
     * @param body The body of the response
     * @return The full HTTP response message
     */
    private static String createHttpResponseString(String body) {
        // Build the HTTP message we will send back to the client
        var response = """
                HTTP/1.1 200 OK\r
                Content-Type: text/plain\r
                """;

        response += "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n";
        response += body;
        return response;
    }

    /**
     * Get the HTTP request message from a client socket
     * @param socket The socket to read the request from
     * @return The HTTP request message headers (this implementation does not read the body)
     * @throws IOException If there is an error reading the request
     */
    public static String getHttpRequestString(Socket socket) throws IOException {
        var requestReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        while ( true ) {
            var line = requestReader.readLine();

            // In an HTTP message, an empty line indicates the end of the headers
            if ( line == null || line.isEmpty() ) {
                break;
            }
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }
}
