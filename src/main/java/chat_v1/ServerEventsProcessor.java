package chat_v1;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class ServerEventsProcessor implements Consumer<ServerEvent> {

    private final ServerWorkers serverWorkers;

    @Override
    public void accept(ServerEvent event) {
        switch (event.getType()) {
            case MESSAGE_RECEIVED -> serverWorkers.broadcast(event.getPayload());
            case CONNECTION_CLOSED -> serverWorkers.remove(event.getSource());
            case COMMAND_RECEIVED -> serverWorkers.execute(event.getPayload());
          //  case USER_JOINED_CHANNEL ->
        }
    }

}
