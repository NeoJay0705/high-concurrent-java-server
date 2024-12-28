package net.n.example.high_concurrent_java_server.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import lombok.SneakyThrows;

public final class HttpUtil {
    @SneakyThrows
    public static Map<String, String> getQueryParam(String query) {
        Map<String, String> params = new HashMap<>();

        if (query == null)
            return params;

        Scanner scanner = new Scanner(query);
        scanner.useDelimiter("&");

        while (scanner.hasNext()) {
            String[] nameValue = scanner.next().split("=");
            if (nameValue.length == 2) {
                params.put(nameValue[0], nameValue[1]);
            }
        }
        scanner.close();
        return params;

    }

}
