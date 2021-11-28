package chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import chat.constants.Constants;
import chat.server.models.User;

/**
 * Обработчик для конкретного клиента.
 */
public class ClientHandler {

    private MyServer server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private User user;

    public ClientHandler(MyServer server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    authentification();
                    readMessage();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException ex) {
            throw new RuntimeException("Проблемы при создании обработчика");
        }
    }

    // /auth login pass

    private void authentification() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith(Constants.AUTH_COMMAND)) {
                String[] tokens = str.split("\\s+");
                Optional<User> user = server.getAuthService().getNickByLoginAndPass(tokens[1], tokens[2]);

                if (user.isPresent()) {
                    //Дописать проверку что такого ника нет в чате(*)
                    //Авторизовались
                    this.user = user.get();
                    sendMessage(Constants.AUTH_OK_COMMAND + " " + this.user.nick);
                    server.broadcastMessage(this.user.nick + " вошел в чат");
                    server.broadcastMessage(server.getActiveClients());
                    server.subscribe(this);
                    return;
                } else {
                    sendMessage("Неверные логин/пароль");
                }
            }
        }
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

            //TODO chat history
            String messageFromClient = in.readUTF();
            //hint: можем получать команду
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
                System.out.println("Сообщение от " + user.nick + ": " + messageFromClient);
                if (messageFromClient.equals(Constants.END_COMMAND)) {
                    break;
                }
                server.broadcastMessage(user.nick + ": " + messageFromClient);
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
    }
}
