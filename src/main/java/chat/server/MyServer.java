package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import chat.constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Логика сервера.
 */
public class MyServer {
    public static final Logger logger = LogManager.getLogger(MyServer.class);
    /**
     * Сервис аутентификации.
     */
    private AuthService authService;

    /**
     * Активные клиента.
     */
    private List<ClientHandler> clients;


    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        try (ServerSocket server = new ServerSocket(Constants.SERVER_PORT)) {

            authService = new DbAuthService();
            authService.start();
            clients = new ArrayList<>();

            logger.trace("Сервер готов");
            while (true) {
                Socket socket = server.accept();
                new ClientHandler(this, socket);
            }

        } catch (IOException ex) {
            logger.error("Ошибка в работе сервера.");
            ex.printStackTrace();
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    public synchronized void broadcastMessage(String message) {
        clients.forEach(client -> client.sendMessage(message));
    }

    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
    }

    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    public synchronized String getActiveClients() {
        StringBuilder sb = new StringBuilder(Constants.CLIENTS_LIST_COMMAND).append(" ");
        sb.append(clients.stream()
                .map(c -> c.getName())
                .collect(Collectors.joining(" "))
        );
        return sb.toString();
    }
}
