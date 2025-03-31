package _05_server_with_routing.app;

import _05_server_with_routing.http.HttpRequest;
import _05_server_with_routing.http.HttpResponse;
import _05_server_with_routing.http.HttpStatus;
import _05_server_with_routing.http.RequestHandler;

import java.util.Map;

public class NotFoundHandler implements RequestHandler {

    @Override
    public HttpResponse handle(HttpRequest request) throws Exception {

        return HttpResponse.ofHtml(HttpStatus.NOT_FOUND, Template.fill("mainLayout", "404", Map.of("title", "404 Not Found")));
    }
}
