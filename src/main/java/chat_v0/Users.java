package chat_v0;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class Users {
    private static Set<String> users = new HashSet<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();


    public Set<String> getUsers() {
        return users;
    }

    public void addUser(String name) {
        lock.writeLock().lock();
        if (users.contains(name)) {
            log.info("User %s already exists!", name);
        }else {
            users.add(name);
        }
        lock.writeLock().unlock();
    }

    public void removeUSer(String name) {
        if(users.contains(name))
            users.remove(name);
        else log.info(name + " absent in the users list!");
    }
}
