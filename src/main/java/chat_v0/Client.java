package chat_v0;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class Client implements Runnable {

    private BufferedReader in;
    private PrintWriter out;
    private Socket client;
    private boolean done;
    private int port = 9999;
    private String host = "127.0.0.1";


    @Override
    public void run() {
        try {
            client = new Socket(host, port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread thread = new Thread(inputHandler);
            thread.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                log.info(inMessage);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if (!client.isClosed())
                client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class InputHandler implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String consoleText = consoleReader.readLine();
                    if (consoleText.equals("/quit")) {
                        out.println(consoleText);
                        consoleReader.close();
                        shutdown();
                    } else {
                        out.println(consoleText);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
