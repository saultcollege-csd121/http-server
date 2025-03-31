package _05_server_with_routing.http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class QueryStringParams {

    public static HashMap<String, String> parse(String queryString) {

        if ( queryString == null ) {
            return new HashMap<>();
        }

        // Strip the leading '?' if it exists
        if (queryString.startsWith("?")) {
            queryString = queryString.substring(1);
        }

        var urlParams = new HashMap<String, String>();    // query string is a set of key=val pairs
        if ( ! queryString.isBlank() ) {
            var params = queryString.split("&");    // & separates individual query string parameters
            // WARNING: For the sake of simplicity, this implementation does not correctly handle
            // multiple values for the same key
            for (var param : params) {
                var parts = param.split("=");

                // Browsers will encode special characters in URLs, so we need to decode them
                // to get the actual key and value strings
                var key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                var val = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                urlParams.put(key, val);
            }
        }

        return urlParams;
    }

}
