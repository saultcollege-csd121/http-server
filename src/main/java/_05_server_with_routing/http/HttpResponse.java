package _05_server_with_routing.http;

public final class HttpResponse extends HttpMessage {

    private final HttpStatus status;

    public HttpResponse(HttpStatus status, String body, ContentType contentType) {
        super(body, contentType);
        this.status = status;
    }

    public HttpResponse(String body, ContentType contentType) {
        super(body, contentType);
        this.status = HttpStatus.OK;
    }

    public HttpResponse(byte[] body, ContentType contentType) {
        super(body, contentType);
        this.status = HttpStatus.OK;
    }

    public static HttpResponse ofHtml(HttpStatus status, String body) {
        return new HttpResponse(status, body, ContentType.HTML);
    }

    public static HttpResponse ofHtml(String body) {
        return new HttpResponse(body, ContentType.HTML);
    }

    @Override
    public String firstLine() {
        return getVersion() + " " + status;
    }
}
