package _05_server_with_routing.app;

import _05_server_with_routing.http.HttpServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        var server = new HttpServer();

        // The HttpServer works with RequestHandler objects
        // We can add as many handlers as we want of any type, so long as they implement the RequestHandler interface
        server.addHandler(new StaticFileHandler("src/main/resources/websiteFiles"));
        server.addHandler(new DiceHandler());
        server.addHandler(new NotFoundHandler());
        // OR, since RequestHandler is a functional interface, we can use a lambda expression!
        // server.addHandler(request -> HttpResponse.ofHtml(HttpStatus.NOT_FOUND, Template.fill("mainLayout", "404", Map.of("title", "404 Not Found"))));

        try {
            server.start(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
