package whiteboard;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import whiteboard.model.CommunicationUnit;
import whiteboard.view.UserWindow;

import java.io.IOException;

import static whiteboard.controller.MessageType.*;

/**
 * This class is the entry point for starting the client side.
 *
 * @author Group 3
 */
public class User {
    private static String serverIP;
    private static int serverPort;
    private static String username;

    public static void main(String[] args) {
        parseArgs(args);

        CommunicationUnit unit = new CommunicationUnit(serverIP, serverPort);

        String joinWhiteboardRequest = String.format("{\"Type\": \"%s\", \"Username\": \"%s\"}", JOIN_WHITEBOARD_REQUEST, username);
        try {
            unit.send(joinWhiteboardRequest);
        } catch (IOException e) {
            System.out.println("Message" + joinWhiteboardRequest + " Send Failed");
            return;
        }

        try {
            JSONObject joinWhiteboardResponse = JSONObject.parseObject(unit.receive());

            if (!joinWhiteboardResponse.get("Type").equals(JOIN_WHITEBOARD_RESPONSE)) {
                System.out.println("Message " + JOIN_WHITEBOARD_RESPONSE + " Expected");
                return;
            }

            boolean duplicated = joinWhiteboardResponse.getBoolean("Duplicated");
            boolean approved = joinWhiteboardResponse.getBoolean("Approved");

            if (duplicated) {
                System.out.println("The username already exists. Please input another one.");
                System.exit(1);
            } else {
                if (!approved) {
                    System.out.println("The manager refused you to join the whiteboard.");
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            JSONObject updateUserCanvasMsg = JSONObject.parseObject(unit.receive());
            JSONArray jsonArray = updateUserCanvasMsg.getJSONArray("Shapes");

            UserWindow userWindow = new UserWindow(jsonArray, username, unit);

            while (true) {
                JSONObject msgFromServer = JSONObject.parseObject(unit.receive());
                if (!msgFromServer.toString().equals("")) {
                    String type = msgFromServer.getString("Type");
                    switch (type) {
                        case UPDATE_USER_CANVAS:
                            JSONArray newJSONArray = msgFromServer.getJSONArray("Shapes");
                            userWindow.updateBoard(newJSONArray);
                            break;
                        case CLEAR_USER_CANVAS:
                            userWindow.clearBoard();
                            break;
                        case SEND_SHAPE_TO_USER:
                            String shape = msgFromServer.getString("Shape");
                            String params = msgFromServer.getString("Parameters");
                            String color = msgFromServer.getString("Color");
                            JSONObject newJSONObject;
                            if (shape.equals("Text")) {
                                String text = msgFromServer.getString("Text");
                                newJSONObject = JSONObject.parseObject(String.format(
                                        "{\"Shape\": \"%s\", \"Parameters\": \"%s\", \"Color\": \"%s\", \"Text\": \"%s\"}",
                                        shape, params, color, text
                                ));
                            } else {
                                newJSONObject = JSONObject.parseObject(String.format(
                                        "{\"Shape\": \"%s\", \"Parameters\": \"%s\", \"Color\": \"%s\"}",
                                        shape, params, color
                                ));
                            }
                            userWindow.updateBoard(newJSONObject);
                            break;
                        case ADD_USER:
                            String usernameToAdd = msgFromServer.getString("Username");
                            userWindow.addUser(usernameToAdd);
                            break;
                        case REMOVE_USER:
                            String usernameToRemove = msgFromServer.getString("Username");
                            userWindow.removeUser(usernameToRemove);
                            break;
                        case SEND_MESSAGE_TO_USER:
                            String sender = msgFromServer.getString("Username");
                            String text = msgFromServer.getString("Text");
                            userWindow.addChatText(sender, text);
                            break;
                        case GOODBYE:
                            System.out.println("The manager has closed the whiteboard.");
                            userWindow.raiseInfo("The manager has closed the whiteboard.");
                            unit.close();
                            System.exit(1);
                            break;
                        case KICK_OUT_USER:
                            System.out.println("The manager has kicked you out of the whiteboard.");
                            userWindow.raiseInfo("The manager has kicked you out of the whiteboard.");
                            unit.close();
                            System.exit(1);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse the command line arguments to get the server IP address, server port, and username.
     *
     * @param args the command line arguments
     */
    private static void parseArgs(String[] args) {
        if (args.length != 3) {
            System.out.println("Please provide three command line arguments, i.e., the server IP address, server port, and username.");
            System.exit(1);
        }

        serverIP = args[0];
        username = args[2];

        try {
            serverPort = Integer.parseInt(args[1]);
            if (serverPort < 1024 || serverPort > 49151) {
                System.out.println("Please enter a port number in the range of 1024 to 49151.");
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
