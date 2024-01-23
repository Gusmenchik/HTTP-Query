package org.example;
import java.io.IOException;
public class Main {
    public static void main(String[] args) {
        final var server = new Server();

        server.addHandler("GET", "/messages", (request, responseStream) -> {
            // Handle GET /messages
            try {
                String responseBody = "Handling GET /messages";
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: text/plain\r\n" +
                                "Content-Length: " + responseBody.length() + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n" +
                                responseBody
                ).getBytes());
                responseStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("POST", "/messages", (request, responseStream) -> {
            // Handle POST /messages
            try {
                String responseBody = "Handling POST /messages";
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: text/plain\r\n" +
                                "Content-Length: " + responseBody.length() + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n" +
                                responseBody
                ).getBytes());
                responseStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.start(7656);
    }
}
