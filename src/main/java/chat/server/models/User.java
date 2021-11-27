package chat.server.models;

public class User {
    public String id = "";
    public String nick = "";
    public User(String id, String name) {
        this.id = id;
        this.nick = name;
    }
}
