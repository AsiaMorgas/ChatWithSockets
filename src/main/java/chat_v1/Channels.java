package chat_v1;

import lombok.extern.java.Log;

import java.util.HashSet;
import java.util.Set;


@Log
public class Channels {

    private Set<Channel> channels = new HashSet<>();

    public void printChannels() {
        if (hasChannels())
            channels.stream()
                    .forEach(channel -> log.info(channel.getChannelName()));
        log.info("There are no available channels.");

    }

    private boolean hasChannels() {
        return channels.size() > 0;
    }
}
