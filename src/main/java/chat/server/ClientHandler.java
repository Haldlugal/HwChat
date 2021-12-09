package chat.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import chat.constants.Constants;
import chat.server.models.User;

/**
 * Обработчик для конкретного клиента.
 */
public class ClientHandler {

    private final MyServer server;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private User user;
    BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/chat/logs/logs.txt", true));
    BufferedReader reader = new BufferedReader(new FileReader("src/main/java/chat/logs/logs.txt"));

    public ClientHandler(MyServer server, Socket socket) throws IOException {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            server.getExecutorService().execute(() -> {
                try {
                    authentification();
                    readMessage();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    closeConnection();
                }
            });
//            new Thread(() -> {
//                try {
//                    authentification();
//                    readMessage();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                } finally {
//                    closeConnection();
//                }
//            }).start();
        } catch (IOException ex) {
            throw new RuntimeException("Проблемы при создании обработчика");
        }
    }

    private void authentification() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith(Constants.AUTH_COMMAND)) {
                String[] tokens = str.split("\\s+");
                Optional<User> user = server.getAuthService().getNickByLoginAndPass(tokens[1], tokens[2]);

                if (user.isPresent()) {
                    for (int i = 0; i < 1000; i++) {
                        writer.write("test" + i + "\n");
                    }
                    this.user = user.get();
                    sendMessage(Constants.AUTH_OK_COMMAND + " " + this.user.nick);
                    server.broadcastMessage(this.user.nick + " вошел в чат");
                    server.broadcastMessage(server.getActiveClients());
                    server.subscribe(this);
                    List<String> logs = this.getLogs();
                    int i =0;
                    for (String log : logs) {
                        i++;
                        sendMessage(log);
                    }
                    System.out.println("count: " + i);
                    return;
                } else {
                    sendMessage("Неверные логин/пароль");
                }
            }
        }
    }

    List<String> getLogs() throws IOException{
        List<String> strings = new ArrayList<>();
        String str;
        int i = 0;
        while((str = reader.readLine()) != null) {
            strings.add(str);
            i++;
            if (i > 100) {
                strings.remove(0);
            }
        }
        return strings;
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessage() throws IOException {
        while (true) {
            String messageFromClient = in.readUTF();
            if (messageFromClient.startsWith(Constants.CLIENTS_LIST_COMMAND)) {
                sendMessage(server.getActiveClients());
            } else if (messageFromClient.startsWith(Constants.CHANGE_NICK)) {
                String newNick = messageFromClient.split("\\s+")[1];
                boolean result = server.getAuthService().changeNickname(user.id, newNick);
                if (result) {
                    server.broadcastMessage(user.nick + " сменил ник на " + newNick);
                    user.nick = newNick;
                } else {
                    sendMessage("Не удалось сменить ник");
                }
            } else {
                if (messageFromClient.equals(Constants.END_COMMAND)) {
                    break;
                }
                String finalMessage = user.nick + ": " + messageFromClient;
                writer.write(finalMessage + "\n");
                server.broadcastMessage(finalMessage);
            }
        }
    }

    public String getName() {
        return user.nick;
    }

    private void closeConnection() {
        server.unsubscribe(this);
        server.broadcastMessage(user.nick + " вышел из чата");

        try {
            in.close();
        } catch (IOException ex) {
            //ignore
        }
        try {
            out.close();
        } catch (IOException ex) {
            //ignore
        }
        try {
            socket.close();
        } catch (IOException ex) {
            //ignore
        }
        try {
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
