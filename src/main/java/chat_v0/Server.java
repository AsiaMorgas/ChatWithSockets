package chat_v0;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Server implements Runnable {

    //    private Set<ConnectionHandler> connections;
    private ConnectionHandlers connections;
    private ExecutorService pool;
    private ServerSocket server;
    private Channels channels;
    private int port = 9999;
    private boolean done;

    public Server() {
        // connections = new HashSet<>();
        connections = new ConnectionHandlers();
        channels = new Channels();
        done = false;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
            log.info("Server is running on port " + port);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                var socket = server.accept();
                var connectionHandler = new ConnectionHandler(this, socket);
                connections.addConnection(connectionHandler);
                pool.execute(connectionHandler);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void logActivity(String message) {
        if (message != null)
            connections.broadcastToAll(message);
    }

    public void broadcast(String from, String message) {
        connections
                .broadcastFromToAll(from, message);
    }

    public void broadcastToChannel(String from, Channel to, String message) {
        connections.broadcastFromTo(from, to, message);
    }

    public void shutdown() {
        try {
            done = true;
            if (!server.isClosed()) {
                server.close();
            }
            pool.shutdown();
            connections.getConnections()
                    .forEach(connection -> connection.shutdown());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
