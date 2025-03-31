package _04_file_server_by_data_types.http;

import java.util.HashMap;
import java.util.Map;

public sealed abstract class HttpMessage permits HttpRequest, HttpResponse {

    private final String version;
    private final Map<String, String> headers;
    private final byte[] body;

    protected HttpMessage(String version, Map<String, String> headers, byte[] body) {
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    protected HttpMessage(String body, ContentType contentType) {
        this.version = "HTTP/1.1";
        this.headers = new HashMap<>();
        this.body = body.getBytes();
        this.headers.put("Content-Type", contentType.getValue());
    }

    protected HttpMessage(byte[] body, ContentType contentType) {
        this.version = "HTTP/1.1";
        this.headers = new HashMap<>();
        this.body = body;
        this.headers.put("Content-Type", contentType.getValue());
    }

    public String getVersion() {
        return version;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public byte[] getBody() {
        return body;
    }

    public abstract String firstLine();

    public byte[] toByteArray() {
        var headString = new StringBuilder();
        headString.append(firstLine()).append("\r\n");
        for (var header : this.headers.entrySet()) {

            // Skip the Content-Length header if it's already set (it will be set automatically below)
            if ( header.getKey().equals("Content-Length") ) {
                continue;
            }

            headString.append("%s: %s\r\n".formatted(header.getKey(), header.getValue()));
        }

        if ( body != null ) {
            headString.append("Content-Length: %d\r\n".formatted(body.length));

            if ( headers.get("Content-Type") == null ) {
                headString.append("Content-Type: application/octet-stream\r\n");
            }

            headString.append("\r\n");

            var head = headString.toString().getBytes();
            var messageBytes = new byte[head.length + body.length];
            System.arraycopy(head, 0, messageBytes, 0, head.length);
            System.arraycopy(body, 0, messageBytes, head.length, body.length);
            return messageBytes;
        } else {
            headString.append("\r\n");
            return headString.toString().getBytes();
        }
    }
}
