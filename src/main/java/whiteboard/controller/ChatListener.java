package whiteboard.controller;

import whiteboard.model.CommunicationUnit;
import whiteboard.model.UserManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static whiteboard.controller.MessageType.SEND_MESSAGE_TO_SERVER;
import static whiteboard.controller.MessageType.SEND_MESSAGE_TO_USER;

public class ChatListener implements ActionListener {
    private final boolean flag;
    private final CommunicationUnit unit;
    private final String username;
    private final UserManager userManager;
    private final String manager;
    private JTextPane textPaneDisplay;
    private JEditorPane editorPaneInput;

    public ChatListener(UserManager userManager, String manager) {
        flag = true;
        this.unit = null;
        this.username = null;
        this.userManager = userManager;
        this.manager = manager;
    }

    public ChatListener(CommunicationUnit unit, String username) {
        flag = false;
        this.unit = unit;
        this.username = username;
        this.userManager = null;
        this.manager = null;
    }

    public void setTextPaneDisplay(JTextPane textPaneDisplay) {
        this.textPaneDisplay = textPaneDisplay;
    }

    public void setEditorPaneInput(JEditorPane editorPaneInput) {
        this.editorPaneInput = editorPaneInput;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String chatMsg = editorPaneInput.getText();
        if (flag) {
            if (textPaneDisplay.getText().equals(""))
                textPaneDisplay.setText(manager + ": " + chatMsg);
            else
                textPaneDisplay.setText(textPaneDisplay.getText() + "\n" + manager + ": " + chatMsg);
            editorPaneInput.setText("");

            String sendMsgToUser = String.format(
                    "{\"Type\": \"%s\", \"Username\": \"%s\", \"Text\": \"%s\"}",
                    SEND_MESSAGE_TO_USER, manager, chatMsg
            );

            assert userManager != null;
            userManager.broadcastMsg(sendMsgToUser);
        } else {
            if (textPaneDisplay.getText().equals(""))
                textPaneDisplay.setText(username + ": " + chatMsg);
            else
                textPaneDisplay.setText(textPaneDisplay.getText() + "\n" + username + ": " + chatMsg);
            editorPaneInput.setText("");

            String sendMsgToServer = String.format(
                    "{\"Type\": \"%s\", \"Username\": \"%s\", \"Text\": \"%s\"}",
                    SEND_MESSAGE_TO_SERVER, username, chatMsg
            );

            try {
                assert unit != null;
                unit.send(sendMsgToServer);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
