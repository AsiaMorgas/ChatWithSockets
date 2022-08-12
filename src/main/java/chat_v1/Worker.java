package chat_v1;

import chat_v1.commons.TextReader;
import chat_v1.commons.TextWriter;

import java.net.Socket;
import java.util.function.Consumer;


class Worker implements Runnable {

    private final Socket socket;
    private final EventsBus eventsBus;
    private final TextWriter writer;

    Worker(Socket socket, EventsBus eventsBus) {
        this.socket = socket;
        this.eventsBus = eventsBus;
        writer = new TextWriter(socket);
    }

    @Override
    public void run() {
        new TextReader(socket, this::onText, this::onInputClose).read();
    }

    private void onText(String text) {
        eventsBus.publish(ServerEvent.builder()
                .type(ServerEventType.MESSAGE_RECEIVED)
                .payload(text)
                .source(this)
                .build());
    }

    private void onPrintUsersCommand(String text, Consumer<ServerEvent> consumer) {

        eventsBus.publishTo(ServerEvent.builder()
                .type(ServerEventType.COMMAND_RECEIVED)
                .payload(text)
                .source(this)
                .build(), consumer);
    }

    private void onInputClose() {
        eventsBus.publish(ServerEvent.builder()
                .type(ServerEventType.CONNECTION_CLOSED)
                .source(this)
                .build());
    }

    void send(String text) {
        writer.write(text);
    }

}
