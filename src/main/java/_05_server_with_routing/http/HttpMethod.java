package _05_server_with_routing.http;

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS;

    public static HttpMethod parse(String method) throws IllegalArgumentException {
        return switch (method.toUpperCase()) {
            case "GET" -> GET;
            case "POST" -> POST;
            case "PUT" -> PUT;
            case "DELETE" -> DELETE;
            case "PATCH" -> PATCH;
            case "HEAD" -> HEAD;
            case "OPTIONS" -> OPTIONS;
            default -> throw new IllegalArgumentException("Unknown HTTP method: " + method);
        };
    }
}
