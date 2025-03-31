package _05_server_with_routing.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class Template {

    public static String fill(String layoutName, String templateName, Map<String, String> data) throws IOException {
        var layoutPath = Paths.get("src/main/resources/templates", layoutName + ".html");
        var templatePath = Paths.get("src/main/resources/templates", templateName + ".html");

        if ( ! Files.exists(layoutPath) ) {
            throw new FileNotFoundException("Could not find layout file " + layoutPath);
        }

        if ( ! Files.exists(templatePath) ) {
            throw new FileNotFoundException("Could not find template file " + templatePath);
        }

        var layout = Files.readString(layoutPath, StandardCharsets.UTF_8);
        var template = Files.readString(templatePath, StandardCharsets.UTF_8);

        layout = layout.replace(placeholder("CONTENT"), template);

        for (var entry : data.entrySet()) {
            layout = layout.replaceAll(placeholder(entry.getKey()), entry.getValue());
        }

        return layout;
    }

    private static String placeholder(String placeholderName) {
        return "<!---" + placeholderName.toUpperCase() + "--->";
    }
}
