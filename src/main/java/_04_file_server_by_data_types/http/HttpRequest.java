package _04_file_server_by_data_types.http;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class HttpRequest extends HttpMessage {

    private final HttpMethod method;
    private final URI uri;

    public HttpRequest(HttpMethod method, URI uri, String version, Map<String, String> headers, byte[] body) {
        super(version, headers, body);
        this.method = method;
        this.uri = uri;
    }


    public HttpMethod getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public String firstLine() {
        return method + " " + uri + " " + getVersion();
    }

    public static HttpRequest parse(String head, byte[] body) {

        var headLines = head.split("\n");

        // Assuming the first line of the request is of the form "HTTPMETHOD /the/url/path?key=val&key=val HTTP/1.1"
        var firstLineParts = headLines[0].split(" ");      // space sparates HTTP method, path, and version
        var httpMethod = firstLineParts[0];
        var fullUrl = firstLineParts[1];
        var version = firstLineParts[2];

        var firstLine = true;
        var headers = new HashMap<String, String>();
        for (var line : headLines) {
            // Skip the first line, which we've already processed
            if ( firstLine ) {
                firstLine = false;
                continue;
            }

            var colonIndex = line.indexOf(':');
            if ( colonIndex == -1 ) {
                throw new IllegalArgumentException("Invalid header: " + line);
            }

            headers.put(line.substring(0, colonIndex).trim(), line.substring(colonIndex).trim());
        }

        return new HttpRequest(
                HttpMethod.valueOf(httpMethod),
                URI.create(fullUrl),
                version,
                headers,
                body
        );
    }

}
