package whiteboard.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages all connected users by maintaining all usernames and corresponding communication units.
 *
 * @author Group 3
 */
public class UserManager {
    private final String manager;
    private final List<String> userRegistry;
    private final Map<String, CommunicationUnit> userCommunicationRegistry;

    public UserManager(String manager) {
        this.manager = manager;
        userRegistry = new ArrayList<>();
        userCommunicationRegistry = new HashMap<>();
    }

    /**
     * Check whether the username already exists in the user registry.
     */
    public synchronized boolean isDuplicated(String username) {
        return userRegistry.contains(username);
    }

    /**
     * Add a user and the associated communication unit.
     */
    public synchronized void addUser(String username, CommunicationUnit communicationUnit) {
        userRegistry.add(username);
        userCommunicationRegistry.put(username, communicationUnit);
    }

    /**
     * Remove a user and the associated communication unit.
     */
    public synchronized void removeUser(String username) {
        userRegistry.remove(username);
        try {
            userCommunicationRegistry.get(username).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        userCommunicationRegistry.remove(username);
    }

    public String getManager() {
        return manager;
    }

    public List<String> getUserRegistry() {
        return userRegistry;
    }

    public Map<String, CommunicationUnit> getUserCommunicationRegistry() {
        return userCommunicationRegistry;
    }

    /**
     * Broadcast a message to all connected users.
     *
     * @param msg the message to broadcast
     */
    public synchronized void broadcastMsg(String msg) {
        for (Map.Entry<String, CommunicationUnit> entry : userCommunicationRegistry.entrySet()) {
            String username = entry.getKey();
            CommunicationUnit unit = entry.getValue();
            if (unit.isDisconnected()) {
                System.out.println(username + "Disconnected");
            } else {
                try {
                    unit.send(msg);
                    System.out.println("Receiver: " + username + "; Message: " + msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Broadcast a message to all connected users except for the specified user.
     *
     * @param msg               the message to broadcast
     * @param usernameToExclude the user to exclude
     */
    public synchronized void broadcastMsg(String msg, String usernameToExclude) {
        for (Map.Entry<String, CommunicationUnit> entry : userCommunicationRegistry.entrySet()) {
            String username = entry.getKey();
            if (username.equals(usernameToExclude))
                continue;
            CommunicationUnit unit = entry.getValue();
            if (unit.isDisconnected()) {
                System.out.println(username + "Disconnected");
            } else {
                try {
                    unit.send(msg);
                    System.out.println("Receiver: " + username + "; Message: " + msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
