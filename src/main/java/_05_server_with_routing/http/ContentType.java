package _05_server_with_routing.http;

import java.nio.file.Path;

public enum ContentType {
    BMP("image/bmp"),
    CSS("text/css"),
    FORM("application/x-www-form-urlencoded"),
    GIF("image/gif"),
    HTML("text/html"),
    ICO("image/x-icon"),
    JAVASCRIPT("application/javascript"),
    JPEG("image/jpeg"),
    JSON("application/json"),
    OCTET_STREAM("application/octet-stream"),
    PDF("application/pdf"),
    PLAIN("text/plain"),
    PNG("image/png"),
    SVG("image/svg+xml"),
    WEBP("image/webp"),
    XML("application/xml"),
    ZIP("application/zip");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Determines the HTTP Content-Type for a given file path, based on the file's extension
     * @param path The path of the file for which to determine the content type
     * @return The ContentType
     */
    public static ContentType fromPath(Path path) {
        var filename = path.getFileName().toString();
        var dotIndex = filename.lastIndexOf(".");
        var extension = "";
        if ( dotIndex >= 0 ) {
            extension = filename.substring(dotIndex + 1).toLowerCase();
        }

        return switch (extension) {
            case "html" -> HTML;
            case "txt" -> PLAIN;
            case "css"  -> CSS;
            case "js"   -> JAVASCRIPT;
            case "json" -> JSON;
            case "jpg", "jpeg" -> JPEG;
            case "png" -> PNG;
            case "webp" -> WEBP;
            case "gif" -> GIF;
            case "bmp" -> BMP;
            case "svg" -> SVG;
            case "ico" -> ICO;
            case "pdf" -> PDF;
            case "zip" -> ZIP;
            default -> OCTET_STREAM;  // Default for unknown types
        };
    }
}
