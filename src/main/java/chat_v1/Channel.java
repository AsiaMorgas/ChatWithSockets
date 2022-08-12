package chat_v1;

import lombok.Getter;
import lombok.extern.java.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Log
public class Channel {

    @Getter
    private String channelName;
    @Getter
    private Set<ChatClient> users ;

    private ReadWriteLock lock = new ReentrantReadWriteLock();


    public Channel(String name) {
        this.users = new HashSet<>();
        this.channelName = name;
    }

    public void addUser(ChatClient user) {
        lock.writeLock().lock();
        if(!users.contains(user)) {
            users.add(user);
            log.info(user.getName() + " joined channel");
        } else {
            log.info("User " + user.getName() + " already present in channel " + this.channelName);
        }
        lock.writeLock().unlock();
    }

    public void printUsers() {
        lock.readLock().lock();
        if(hasUsers()) {
            users.stream()
                    .forEach(user -> log.info("Users in channel: " + user.getName()));
        }
        lock.readLock().unlock();
    }


    private boolean hasUsers() {
        return users.size() > 0;
    }
}
