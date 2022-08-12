package chat_v0;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class Channels {
    private static Set<Channel> channels = new HashSet<>();
    private static ReadWriteLock lock = new ReentrantReadWriteLock();

    public static Set<String> getChannelNames() {
        lock.readLock().lock();
        Set<String> allChannels = new HashSet<>();
        channels.forEach(channel -> allChannels.add(channel.getName()));
        lock.readLock().unlock();
        return allChannels;
    }

    public static synchronized Set<Channel> getChannels() {
        return Channels.channels;
    }

    public static synchronized Channel addChannel(String channelName) {
        if(isCreated(channelName)){
            log.info(channelName + " already exists!");
        }else{
            channels.add(new Channel(channelName));
        }
        return getChannelByName(channelName);
    }

    public static synchronized Channel getChannelByName(String channelName) {
        Optional<Channel> channel = channels
                .stream()
                .filter(ch -> ch.getName().equals(channelName))
                .findFirst();
        return channel.get();
    }

    private static synchronized boolean isCreated(String channelName) {
        return channels.stream()
                .anyMatch(ch -> ch.getName().equals(channelName));
    }


}
