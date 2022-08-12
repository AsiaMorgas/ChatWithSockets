package chat_v1;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class ServerEvent {

    private final ServerEventType type;
    private String payload;
    private Worker source;

}
