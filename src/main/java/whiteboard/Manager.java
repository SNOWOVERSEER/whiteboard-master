package whiteboard;

import com.alibaba.fastjson.JSONArray;
import whiteboard.controller.UserRequestsHandler;
import whiteboard.model.CommunicationUnit;
import whiteboard.model.UserManager;
import whiteboard.view.ManagerWindow;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class is the entry point for starting the server side.
 *
 * @author Quanchi Chen
 */
public class Manager {
    private static int serverPort;
    private static String manager;

    public static void main(String[] args) {
        parseArgs(args);

        JSONArray jsonArray = new JSONArray();
        UserManager userManager = new UserManager(manager);

        ManagerWindow managerWindow = new ManagerWindow(jsonArray, userManager, manager);

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new UserRequestsHandler(new CommunicationUnit(socket), userManager, jsonArray, managerWindow).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse the command line arguments to get the server port and username.
     *
     * @param args the command line arguments
     */
    private static void parseArgs(String[] args) {
        if (args.length != 2) {
            System.out.println("Please provide two command line arguments, i.e., the server port and the manager name.");
            System.exit(1);
        }

        try {
            serverPort = Integer.parseInt(args[0]);
            if (serverPort < 1024 || serverPort > 49151) {
                System.out.println("Please enter a port number in the range of 1024 to 49151.");
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        manager = args[1];
    }
}
