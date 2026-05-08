package dataaccess;

import model.UserData;

import java.util.HashMap;

public class UserDAO {

    private final HashMap<String, UserData> users = new HashMap<>();

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    public void clear() {
        users.clear();
    }
}