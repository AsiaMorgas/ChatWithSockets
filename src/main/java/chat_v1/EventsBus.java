package chat_v1;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Collections.synchronizedSet;

class EventsBus {
//klasa pozwalajaca na
//TODO: tu dodac scheduler, który weźmie od clientów odpowiedzialność powiadamiania o pojawiającym sie evencie
    private final Set<Consumer<ServerEvent>> consumers = synchronizedSet(new HashSet<>());

    void addConsumer(Consumer<ServerEvent> consumer) {
        consumers.add(consumer);
    }

    void publish(ServerEvent event) {
        consumers.forEach(consumer -> consumer.accept(event));
    }

    void publishTo(ServerEvent event, Consumer<ServerEvent> consumer) {
        consumer.accept(event);
    }

}
