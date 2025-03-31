package _03_file_server_by_string_wrangling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class HttpUtils {

    /**
     * Send an HTTP message to the client containing a given String as the payload
     * @param socket The socket to send the message over
     * @param messageBody The desired body to send with the message.
     * @throws IOException If there is an error sending the message
     */
    public static void sendHttpMessage(Socket socket, byte[] messageBody, String contentType) throws IOException {
        var out = socket.getOutputStream();
        var message = createHttpResponseMessage(200, messageBody, contentType);
        out.write(message);
        out.flush();
    }

    /**
     * Send an HTTP message to the client containing a given String as the payload
     * @param socket The socket to send the message over
     * @param messageBody The desired body to send with the message.
     * @throws IOException If there is an error sending the message
     */
    public static void sendHttpMessage(Socket socket, int status, byte[] messageBody, String contentType) throws IOException {
        var out = socket.getOutputStream();
        var message = createHttpResponseMessage(status, messageBody, contentType);
        out.write(message);
        out.flush();
    }

    /**
     * Create an HTTP response message with body in the given Content-Type
     * @param status The HTTP status code to send
     * @param body The body of the response
     * @param contentType The Content-Type of the body (e.g. text/plain, text/html, img/jpeg, etc.)
     * @return The full HTTP response message
     */
    public static byte[] createHttpResponseMessage(int status, byte[] body, String contentType) {
        // Build the HTTP message we will send back to the client
        var headers = "HTTP/1.1 %d %s\r\n".formatted(status, getHttpStatusMessage(status))
                + "Content-Type: %s\r\n".formatted(contentType);

        headers += "Content-Length: " + body.length + "\r\n\r\n";

        var headerBytes = headers.getBytes(StandardCharsets.UTF_8);

        // Create a new byte[] that can hold both the headers and the body
        var response = new byte[headerBytes.length + body.length];
        // Copy in the headers
        System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
        // Copy the body in AFTER the headers (hence destPost = headerBytes.length)
        System.arraycopy(body, 0, response, headerBytes.length, body.length);

        return response;
    }

    /**
     * Get the HTTP request message from a client socket
     * @param socket The socket to read the request from
     * @return The HTTP request message
     * @throws IOException If there is an error reading the request
     */
    public static String getHttpRequestString(Socket socket) throws IOException {
        var requestReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
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

        if ( contentLength > 0 ) {
            sb.append("\n"); // Separate the headers from the body with a blank line
            // Read the body
            // WARNING: This is not a robust way to read the body.  We are assuming that each byte of the body is
            //         represented by a single character in the requestReader.  This is not always the case.
            //         However, for the simple requests we are handling in this example, it is sufficient.
            var body = new char[contentLength];
            requestReader.read(body);
            sb.append(body);
        }

        return sb.toString();
    }

    /**
     * Determines the HTTP Content-Type for a given file path, based on the file's extension
     * @param path The path of the file for which to determine the content type
     * @return The Content-Type string
     */
    public static String getContentTypeOf(Path path) {
        var filename = path.getFileName().toString();
        var dotIndex = filename.lastIndexOf(".");
        var extension = "";
        if ( dotIndex >= 0 ) {
            extension = filename.substring(dotIndex + 1).toLowerCase();
        }

        return switch (extension) {
            case "html" -> "text/html";
            case "txt" -> "text/plain";
            case "css"  -> "text/css";
            case "js"   -> "application/javascript";
            case "json" -> "application/json";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "svg" -> "image/svg+xml";
            case "ico" -> "image/x-icon";
            default -> "application/octet-stream";  // Default for unknown types
        };
    }

    public static String getHttpStatusMessage(int status) {
        return switch(status) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 202 -> "Accepted";
            case 204 -> "No Content";
            case 301 -> "Moved Permanently";
            case 302 -> "Found";
            case 304 -> "Not Modified";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 500 -> "Internal Server Error";
            case 501 -> "Not Implemented";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            case 504 -> "Gateway Timeout";
            default -> "Unknown Status";
        };
    }
}
