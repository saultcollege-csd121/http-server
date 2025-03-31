package _05_server_with_routing.app;

import _05_server_with_routing.http.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StaticFileHandler implements RequestHandler {

    private String rootPath;

    public StaticFileHandler(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {

        if ( request.getMethod() != HttpMethod.GET ) {
            return null;
        }

        var path = Paths.get(rootPath, request.getUri().getPath());
        if ( ! Files.exists(path) ) {
            return null;
        }

        var contentType = ContentType.fromPath(path);
        return new HttpResponse(Files.readAllBytes(path), contentType);
    }
}
