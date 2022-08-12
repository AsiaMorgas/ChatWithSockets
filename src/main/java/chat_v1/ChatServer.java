package chat_v1;

import chat_v1.commons.Sockets;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;
/**
 * Rolą serwera jest - obsługa klientów, serwowanie jednej funkcjonalności (nie powinno tu byc parsowania, lockowania)
 * */

@RequiredArgsConstructor
public class ChatServer {

    private static final int DEFAULT_PORT = 8888;
    private static final int THREADS_COUNT = 1024;

    private final ServerWorkers serverWorkers;
    private final EventsBus eventsBus;
    private final ExecutorService executorService;

    private void start(int port) throws IOException {
        eventsBus.addConsumer(new ServerEventsProcessor(serverWorkers));
        try (var serverSocket = new ServerSocket(port)) {
            eventsBus.publish(ServerEvent.builder().type(ServerEventType.SERVER_STARTED).build());
            while (true) {
                var socket = serverSocket.accept();
                eventsBus.publish(ServerEvent.builder().type(ServerEventType.CONNECTION_ACCEPTED).build());
                createWorker(socket);
            }
        }
    }

    private void createWorker(Socket socket) {
        var worker = new Worker(socket, eventsBus);
        serverWorkers.add(worker);
        executorService.execute(worker);
    }

    public static void main(String[] args) throws IOException {

        //tu nie ma zdefiniowanego parsowania -> wyrzucone do pomocniczej klasy Sockets,
        var port = Sockets.parsePort(args[0], DEFAULT_PORT);

        //zeby nie trzeba wszystkiego robic w klasie serwerowej,
        // to EventBus bedzie odpowiedzialny za nasłuchiwanie i logowanie
        var eventsBus = new EventsBus();
        eventsBus.addConsumer(new ServerEventsLogger());

        //logger moze zapisywac logi do bazy a main nic o tym nie wie
        eventsBus.addConsumer(new MessagesHistoryLogger());

        //tu warstwa synchonizujaca w postaci chat_v1.SynchronizedServiceWorkers()
        var serviceWorkers = new SynchronizedServiceWorkers(new HashSetServerWorkers());
        var server = new ChatServer(serviceWorkers, eventsBus, newFixedThreadPool(THREADS_COUNT));
        server.start(port);
    }

}
