package whiteboard.controller;

import com.alibaba.fastjson.JSONObject;
import whiteboard.model.CommunicationUnit;
import whiteboard.model.UserManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static whiteboard.controller.MessageType.KICK_OUT_USER;
import static whiteboard.controller.MessageType.REMOVE_USER;

public class KickUserListener implements ActionListener {
    private final UserManager userManager;
    private final JList<String> userList;
    private final DefaultListModel<String> userListModel;

    public KickUserListener(UserManager userManager, JList<String> userList, DefaultListModel<String> userListModel) {
        this.userManager = userManager;
        this.userList = userList;
        this.userListModel = userListModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String userToKick = userList.getSelectedValue();
        if (userToKick != null) {
            CommunicationUnit unit = userManager.getUserCommunicationRegistry().get(userToKick);

            try {
                unit.send(String.format("{\"Type\":\"%s\"}", KICK_OUT_USER));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            userManager.removeUser(userToKick);
            userListModel.removeElement(userToKick);
            userList.setModel(userListModel);

            JSONObject removeUserBroadcastMsg = JSONObject.parseObject(String.format(
                    "{\"Type\": \"%s\", \"Username\": \"%s\"}",
                    REMOVE_USER, userToKick
            ));
            userManager.broadcastMsg(removeUserBroadcastMsg.toString(), userToKick);
        }
    }
}
