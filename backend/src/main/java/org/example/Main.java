package org.example;

import com.fastcgi.FCGIInterface;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final List<Point> results = new ArrayList<>();

    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();

        while (fcgi.FCGIaccept() >= 0) {
            try {
                processRequest();

            } catch (Exception e) {
                sendResponse(500, "{\"error\":\"Internal server error\"}");
                e.printStackTrace(System.err);
            }
        }
    }

    private static void processRequest() throws IOException {
        long startTime = System.nanoTime();
        long timestamp = System.currentTimeMillis();

        String method = System.getProperty("REQUEST_METHOD");

        if (!"POST".equalsIgnoreCase(method)) {
            sendResponse(405, "{\"error\":\"Only POST requests are allowed\"}");
            return;
        }

        int contentLength;

        try {
            contentLength = Integer.parseInt(
                    System.getProperty("CONTENT_LENGTH", "-1")
            );
        } catch (NumberFormatException e) {
            sendResponse(400, "{\"error\":\"Invalid Content-Length\"}");
            return;
        }

        if (contentLength < 0 || contentLength > 10000) {
            sendResponse(400, "{\"error\":\"Invalid request size\"}");
            return;
        }

        byte[] bodyBytes = System.in.readNBytes(contentLength);

        if (bodyBytes.length != contentLength) {
            sendResponse(400, "{\"error\":\"Incomplete request body\"}");
            return;
        }

        String body = new String(bodyBytes, StandardCharsets.UTF_8);
        Map<String, String> params = parseFormData(body);

        try {
            double x = Double.parseDouble(params.get("x"));
            double y = Double.parseDouble(params.get("y"));
            double r = Double.parseDouble(params.get("r"));

            if (!Double.isFinite(x) || !Double.isFinite(y) ||
                    !Double.isFinite(r)) {
                sendResponse(400, "{\"error\":\"Values must be finite numbers\"}");
                return;
            }

            if (!isValidX(x) || y <= -5 || y >= 3 || !isValidR(r)) {
                sendResponse(400, "{\"error\":\"Invalid coordinates\"}");
                return;
            }

            double executionTime = (System.nanoTime() - startTime) / 1_000_000.0;
            Point point = new Point(x, y, r, timestamp, executionTime);
            results.add(point);

            sendResponse(200, buildResultsJson());

        } catch (NumberFormatException | NullPointerException e) {
            sendResponse(400, "{\"error\":\"Missing or invalid parameters\"}");
        }
    }

    private static Map<String, String> parseFormData(String body) {
        Map<String, String> params = new HashMap<>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            if (parts.length == 2) {
                String key = URLDecoder.decode(
                        parts[0], StandardCharsets.UTF_8
                );

                String value = URLDecoder.decode(
                        parts[1], StandardCharsets.UTF_8
                );

                params.put(key, value);
            }
        }

        return params;
    }

    private static boolean isValidX(double x) {
        return x >= -2 && x <= 2 && x * 2 == Math.rint(x * 2);
    }

    private static boolean isValidR(double r) {
        return r >= 1 && r <= 3 && r * 2 == Math.rint(r * 2);
    }

    private static void sendResponse(int status, String body) {
        String statusText = switch (status) {
            case 200 -> "200 OK";
            case 400 -> "400 Bad Request";
            case 405 -> "405 Method Not Allowed";
            default -> "500 Internal Server Error";
        };

        System.out.print("Status: " + statusText + "\r\n");
        System.out.print("Content-Type: application/json; charset=UTF-8\r\n");
        System.out.print("\r\n");
        System.out.print(body);

        System.out.flush();
        System.out.close();
    }

    private static String buildResultsJson() {
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < results.size(); i++) {
            Point point = results.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"x\":").append(point.getX()).append(",")
                    .append("\"y\":").append(point.getY()).append(",")
                    .append("\"r\":").append(point.getR()).append(",")
                    .append("\"hit\":").append(point.isHit()).append(",")
                    .append("\"timestamp\":").append(point.getTimestamp()).append(",")
                    .append("\"executionTime\":").append(point.getExecutionTime())
                    .append("}");
        }

        json.append("]");

        return json.toString();
    }
}