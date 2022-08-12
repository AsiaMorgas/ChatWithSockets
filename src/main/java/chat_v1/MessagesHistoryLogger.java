package chat_v1;

import lombok.extern.java.Log;

import java.util.function.Consumer;

;

@Log
class MessagesHistoryLogger implements Consumer<ServerEvent> {

    @Override
    public void accept(ServerEvent event) {
        switch (event.getType()) {
            case MESSAGE_RECEIVED -> log.info("New message from " + event.getPayload());
        }
    }
}
