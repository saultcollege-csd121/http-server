package _04_file_server_by_data_types.http;

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

    @Override
    public String firstLine() {
        return getVersion() + " " + status;
    }
}
