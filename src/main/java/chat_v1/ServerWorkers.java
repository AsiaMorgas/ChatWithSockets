package chat_v1;

interface ServerWorkers {

    void add(Worker worker);

    void remove(Worker worker);

    void broadcast(String text);

    void execute(String command);

}
