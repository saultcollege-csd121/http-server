package _05_server_with_routing.http;

public interface RequestHandler {
    HttpResponse handle(HttpRequest request) throws Exception;
}
