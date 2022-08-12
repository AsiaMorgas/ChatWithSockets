package chat_v0;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ConnectionHandlers {
    @Getter
    private Set<ConnectionHandler> connections;

    public ConnectionHandlers() {
        connections = new HashSet<>();
    }


    public void broadcastFromToAll(String from, String message) {
        if (message != null) {
            var filteredConnections = connections.stream()
                            .filter(connectionHandler -> !connectionHandler.getNickname().equals(from))
                                    .collect(Collectors.toList());
            filteredConnections.forEach(handler -> handler.sendMessage(message));
        }
    }

    public void broadcastToAll(String message) {
        if (message != null) {
            connections.forEach(handler -> handler.sendMessage(message));
        }
    }

    public void broadcastFromTo(String from, Channel channel, String message) {
        Set<String> addressees = channel.getNames();
        if (message != null && userInChannel(from, channel)) {
            connections
                    .stream()
                    .filter(connection -> addressees.contains(connection.getNickname()))
                    .forEach(connectionHandler -> connectionHandler.sendMessage(message));
        } else {
            getConnections().stream()
                    .filter(connectionHandler -> connectionHandler.getNickname().equals(from))
                    .findFirst()
                    .get()
                    .sendMessage("The message cannot be sent!");
        }
    }

    public void addConnection(ConnectionHandler connection) {
        if (connection != null)
            connections.add(connection);
    }

    private boolean userInChannel(String user, Channel channel) {
        return channel.getNames().contains(user);
    }
}
