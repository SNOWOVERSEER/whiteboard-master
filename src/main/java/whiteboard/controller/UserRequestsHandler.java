package whiteboard.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import whiteboard.model.CommunicationUnit;
import whiteboard.model.UserManager;
import whiteboard.view.ManagerWindow;

import java.io.IOException;

import static whiteboard.controller.MessageType.*;

/**
 * The thread handles the requests from connected users.
 *
 * @author Group 3
 */
public class UserRequestsHandler extends Thread {
    private final CommunicationUnit communicationUnit;
    private final UserManager userManager;
    private final JSONArray jsonArray;
    private final ManagerWindow managerWindow;

    public UserRequestsHandler(CommunicationUnit communicationUnit, UserManager userManager, JSONArray jsonArray, ManagerWindow managerWindow) {
        this.communicationUnit = communicationUnit;
        this.userManager = userManager;
        this.jsonArray = jsonArray;
        this.managerWindow = managerWindow;
    }

    @Override
    public void run() {
        try {
            while (true) {
                JSONObject userMsg = JSONObject.parseObject(communicationUnit.receive());
                String requestType = userMsg.getString("Type");

                switch (requestType) {
                    case JOIN_WHITEBOARD_REQUEST:
                        String usernameToAdd = userMsg.getString("Username");
                        if (userManager.isDuplicated(usernameToAdd)) {
                            JSONObject joinWhiteboardResponse = JSONObject.parseObject(String.format(
                                    "{\"Type\": \"%s\", \"Duplicated\": \"%s\", \"Approved\": \"%s\"}",
                                    JOIN_WHITEBOARD_RESPONSE, true, false)
                            );
                            communicationUnit.send(joinWhiteboardResponse.toString());
                        } else {
                            boolean result = managerWindow.raisePermit(usernameToAdd);
                            JSONObject joinWhiteboardResponse = JSONObject.parseObject(String.format(
                                    "{\"Type\": \"%s\", \"Duplicated\": \"%s\", \"Approved\": \"%s\"}",
                                    JOIN_WHITEBOARD_RESPONSE, false, result)
                            );
                            communicationUnit.send(joinWhiteboardResponse.toString());
                            if (result) {
                                userManager.addUser(usernameToAdd, communicationUnit);
                                managerWindow.addUser(usernameToAdd);

                                JSONObject updateUserCanvasMsg = JSONObject.parseObject(String.format(
                                        "{\"Type\": \"%s\", \"Shapes\": %s}",
                                        UPDATE_USER_CANVAS, jsonArray.toString()
                                ));
                                communicationUnit.send(updateUserCanvasMsg.toString());


                                JSONObject addUserMsg;
                                addUserMsg = JSONObject.parseObject(String.format(
                                        "{\"Type\": \"%s\", \"Username\": \"*%s*\"}",
                                        ADD_USER, userManager.getManager())
                                );
                                communicationUnit.send(addUserMsg.toString());
                                for (String registeredUsername : userManager.getUserRegistry()) {
                                    addUserMsg = JSONObject.parseObject(String.format(
                                            "{\"Type\": \"%s\", \"Username\": \"%s\"}",
                                            ADD_USER, registeredUsername)
                                    );
                                    communicationUnit.send(addUserMsg.toString());
                                }

                                JSONObject addUserBroadcastMsg = JSONObject.parseObject(String.format(
                                        "{\"Type\": \"%s\", \"Username\": \"%s\"}",
                                        ADD_USER, usernameToAdd)
                                );
                                userManager.broadcastMsg(addUserBroadcastMsg.toString(), usernameToAdd);
                            }
                        }
                        break;
                    case LEAVE_WHITEBOARD:
                        String usernameToRemove = userMsg.getString("Username");
                        userManager.removeUser(usernameToRemove);
                        managerWindow.removeUser(usernameToRemove);
                        JSONObject removeUserBroadcastMsg = JSONObject.parseObject(String.format(
                                "{\"Type\": \"%s\", \"Username\": \"%s\"}",
                                REMOVE_USER, usernameToRemove)
                        );
                        userManager.broadcastMsg(removeUserBroadcastMsg.toString());
                        break;
                    case UPDATE_SERVER_CANVAS:
                        String shape = userMsg.getString("Shape");
                        String parameters = userMsg.getString("Parameters");
                        String color = userMsg.getString("Color");

                        JSONObject jsonObject;
                        String sendShapeToUserMsg;

                        if (shape.equals("Text")) {
                            String text = userMsg.getString("Text");
                            jsonObject = JSONObject.parseObject(String.format(
                                    "{\"Shape\":\"%s\",\"Parameters\":\"%s\",\"Text\":\"%s\",\"Color\":\"%s\"}",
                                    shape, parameters, text, color
                            ));
                            sendShapeToUserMsg = String.format(
                                    "{\"Type\": \"%s\", \"Shape\": \"%s\", \"Parameters\": \"%s\", \"Color\": \"%s\", \"Text\": \"%s\"}",
                                    SEND_SHAPE_TO_USER, shape, parameters, color, text
                            );
                        } else {
                            jsonObject = JSONObject.parseObject(String.format(
                                    "{\"Shape\":\"%s\",\"Parameters\":\"%s\",\"Color\":\"%s\"}",
                                    shape, parameters, color
                            ));
                            sendShapeToUserMsg = String.format(
                                    "{\"Type\": \"%s\", \"Shape\": \"%s\", \"Parameters\": \"%s\", \"Color\": \"%s\"}",
                                    SEND_SHAPE_TO_USER, shape, parameters, color
                            );
                        }

                        managerWindow.updateBoard(jsonObject);
                        userManager.broadcastMsg(sendShapeToUserMsg, userMsg.getString("Username"));
                        break;
                    case SEND_MESSAGE_TO_SERVER:
                        String username = userMsg.getString("Username");
                        String text = userMsg.getString("Text");
                        managerWindow.addChatText(username, text);
                        String sendMsgToUser = String.format(
                                "{\"Type\": \"%s\", \"Username\": \"%s\", \"Text\": \"%s\"}",
                                SEND_MESSAGE_TO_USER, username, text
                        );
                        userManager.broadcastMsg(sendMsgToUser, username);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
