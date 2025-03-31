package _05_server_with_routing.app;

import _05_server_with_routing.http.HttpMethod;
import _05_server_with_routing.http.HttpRequest;
import _05_server_with_routing.http.HttpResponse;
import _05_server_with_routing.http.RequestHandler;

import java.util.Map;

public class DiceHandler implements RequestHandler {
    @Override
    public HttpResponse handle(HttpRequest request) throws Exception {

        if ( request.getMethod() != HttpMethod.GET ) {
            return null;
        }

        // Check if the path matches the regular expression ^/dice/d\d+$
        if ( !request.getUri().getPath().matches("^/d(4|6|8|10|12|20)$") ) {
            return null;
        }

        // Extract the number of sides from the path
        int sides = Integer.parseInt(request.getUri().getPath().substring(2));

        // Generate a random number between 1 and the number of sides
        int roll = (int) (Math.random() * sides) + 1;

        return HttpResponse.ofHtml(Template.fill("mainLayout", "dice", Map.of("title", "Dice!", "sides", ""+sides, "roll", ""+roll)));

    }
}
