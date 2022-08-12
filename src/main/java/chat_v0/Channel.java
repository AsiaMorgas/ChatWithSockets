package chat_v0;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class Channel {
    private Set<String> users = new HashSet<>();
    @Getter
    private String name;

    public Channel(String name) {
        this.name = name;
    }

    public void addUser(String user) {
        if (users.contains(user))
            log.info(user + " already in channel " + name);
        users.add(user);
    }

    public void removeUser(String user) {
        if (user.contains(user)) {
            users.remove(user);
            log.info(user + " left channel " + name);
        } else {
            log.info(user + " not found in channel " +  name);
        }
    }

    public Set<String> getNames() {
        return users;
    }
}
