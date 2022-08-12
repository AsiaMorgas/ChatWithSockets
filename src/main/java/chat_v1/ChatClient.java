package chat_v1;

import chat_v1.commons.Sockets;
import chat_v1.commons.TextReader;
import chat_v1.commons.TextWriter;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.Socket;

@Log
@Getter
public class ChatClient {

    private static final int DEFAULT_PORT = 8888;

    private final Runnable readFromSocket;
    private final Runnable readFromConsole;

    private String name;
    public ChatClient(String host, int port, String name) throws IOException {
        var socket = new Socket(host, port);
        this.name = name;
        readFromSocket = () -> new TextReader(socket, log::info, () -> Sockets.close(socket)).read();
        readFromConsole = () -> new TextReader(System.in, text -> new TextWriter(socket).write(name + ": " + text)).read();
    }

    private void start() {
        new Thread(readFromSocket).start();
        var consoleReader = new Thread(readFromConsole);
        consoleReader.setDaemon(true);
        consoleReader.start();
    }

    public static void main(String[] args) throws IOException {
        var port = Sockets.parsePort(args[1], DEFAULT_PORT);
        new ChatClient(args[0], port, args[2]).start();
    }

}
