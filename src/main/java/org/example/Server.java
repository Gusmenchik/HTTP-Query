package org.example;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int THREAD_POOL_SIZE = 64;

    private final ExecutorService threadPool;
    private final Map<String, Map<String, Handler>> handlers;

    public Server() {
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.handlers = new HashMap<>();
    }

    public void start(int port) {
        try (final var serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress("localhost", port));
            System.out.println("Server started on port " + port);

            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    threadPool.submit(() -> handleConnection(socket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers
                .computeIfAbsent(method, k -> new HashMap<>())
                .put(path, handler);
    }

    private void handleConnection(Socket socket) {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                return;
            }

            final var method = parts[0];
            final var fullPath = parts[1];

            // Extract path and query from the full path
            String[] pathAndQuery = fullPath.split("\\?");
            final var path = pathAndQuery[0];

            final var handler = handlers.getOrDefault(method, Map.of()).get(path);
            if (handler != null) {
                final var request = parseRequest(in);
                handler.handle(request, out);
            } else {
                sendNotFoundResponse(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Request parseRequest(BufferedReader in) throws IOException {
        // Parse request line
        String requestLine = in.readLine();
        String[] requestParts = requestLine.split(" ");
        String method = requestParts[0];
        String path = requestParts[1];
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ");
            headers.put(headerParts[0], headerParts[1]);
        }

        InputStream body = null;

        return new Request(method, path, headers, body);
    }

    private void sendNotFoundResponse(OutputStream out) {
        try {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





