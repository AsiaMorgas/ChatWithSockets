package chat_v0;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
class ConnectionHandler implements Runnable {

    private final Server server;
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    @Getter
    private String nickname;
    private boolean done;

    ConnectionHandler(Server server, Socket client) {
        this.server = server;
        this.client = client;
        this.done = false;
        this.nickname = "";
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            Users users = new Users();
            while (nicknameIncorrect(nickname)) {
                out.println("Please provide your nickname: ");
                nickname = in.readLine().trim();
            }
            users.addUser(nickname);
            log.info(nickname + " connected!");
            server.logActivity(nickname + " joined the chat!");
            String text;
            while ((text = in.readLine()) != null) {
//                if (text.startsWith("/nick ")) {
//                    String[] splitText = text.split(" ", 2);
//                    if (splitText.length == 2) {
//                        server.broadcast(nickname + " renamed themselves to " + splitText[1]);
//                        log.info(nickname + " renamed themselves to " + splitText[1]);
//                        users.getUsers().remove(nickname);
//                        nickname = splitText[1];
//                        out.println("Successfully changed nickname to " + nickname);
//                        users.addUser(nickname);
//                    } else {
//                        out.println("No nickname provided.");
//                    }
//                } else
                if (text.startsWith("/printAll")) {
                    String usersList = users.getUsers().toString();
                    this.sendMessage("All users: " + usersList);
                } else if (text.startsWith("/printChannels")) {
                    String channelList = Channels.getChannelNames().toString();
                    this.sendMessage("Existing channels: " + channelList);
                } else if (text.startsWith("/join ")) {
                    String channelName = text.split(" ", 2)[1];
                    Channel newChannel = Channels.addChannel(channelName);
                    newChannel.addUser(nickname);
                    this.sendMessage(nickname + " joined " + channelName);
                } else if (text.startsWith("/toChannel ")) {
                    String[] splitText = text.split(" ", 3);
                    String message = nickname + ": " + splitText[2];
                    Channel channel = Channels.getChannelByName(splitText[1]);
                    server.broadcastToChannel(nickname, channel, message);
                } else if (text.startsWith("/leave ")) {
                    String channelToLeave = text.split(" ", 2)[1];
                    Channels.getChannelByName(channelToLeave).removeUser(nickname);
                } else if (text.startsWith("/quit")) {
                    users.getUsers().remove(nickname);
                    Channels.getChannels().forEach(c -> c.getNames().remove(nickname));
                    server.logActivity(nickname + " left the chat!");
                    shutdown();
                } else {
                    server.broadcast(nickname, nickname + ": " + text);
                }
            }
        } catch (Exception e) {
            shutdown();
        }

    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                //server.connections.remove(this);
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean nicknameIncorrect(String nickname) {
        var nicknameDuplicate = isNicknameDuplicate(nickname);
        return (nickname.isEmpty() || nickname == null || nicknameDuplicate);
    }

    private boolean isNicknameDuplicate(String nickname) {
        Users users = new Users();
        return users.getUsers().contains(nickname);
    }
}
