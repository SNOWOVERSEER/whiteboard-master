package whiteboard.view;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import whiteboard.controller.ChatListener;
import whiteboard.controller.DrawListener;
import whiteboard.controller.MenuBtnListener;
import whiteboard.model.CommunicationUnit;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class UserWindow {
    private final JSONArray jsonArray;
    private final String username;
    private final DrawListener drawListener;
    private final ChatListener chatListener;
    private final DefaultListModel<String> userListModel;
    private final JList<String> userList;
    private final CommunicationUnit unit;
    private JTextPane textPaneDisplay;
    private Board board;

    public UserWindow(JSONArray jsonArray, String username, CommunicationUnit unit) {
        this.jsonArray = jsonArray;
        this.username = username;
        this.unit = unit;

        drawListener = new DrawListener(false, unit);
        chatListener = new ChatListener(unit, username);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);

        init();
    }

    private void init() {
        JFrame window = new JFrame();
        window.setTitle("Distributed Shared Whiteboard [" + username + "]");
        window.setSize(800, 500);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        var windowContentPane = window.getContentPane();
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        /*
            Menu bar
         */
        JButton btnClose = new JButton();
        btnClose.setText("Close");
        btnClose.addActionListener(new MenuBtnListener(unit, username));
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(btnClose);

        /*
            Draw button panel
         */
        JPanel panelDrawBtn = new JPanel();

        JButton btnLine = new JButton();
        btnLine.setText("Line");
        btnLine.addActionListener(drawListener);

        JButton btnCircle = new JButton();
        btnCircle.setText("Circle");
        btnCircle.addActionListener(drawListener);

        JButton btnTriangle = new JButton();
        btnTriangle.setText("Triangle");
        btnTriangle.addActionListener(drawListener);

        JButton btnRectangle = new JButton();
        btnRectangle.setText("Rectangle");
        btnRectangle.addActionListener(drawListener);

        JButton btnFreeHand = new JButton();
        btnFreeHand.setText("Free-Hand");
        btnFreeHand.setActionCommand("FreeHand");
        btnFreeHand.addActionListener(drawListener);

        JButton btnText = new JButton();
        btnText.setText("Text");
        btnText.addActionListener(drawListener);

        JButton btnColor = new JButton();
        btnColor.setText("Color");
        btnColor.addActionListener(drawListener);

        panelDrawBtn.setLayout(new GridLayout(7, 1));
        panelDrawBtn.add(btnLine);
        panelDrawBtn.add(btnCircle);
        panelDrawBtn.add(btnTriangle);
        panelDrawBtn.add(btnRectangle);
        panelDrawBtn.add(btnFreeHand);
        panelDrawBtn.add(btnText);
        panelDrawBtn.add(btnColor);

        /*
            User list component
         */

        JScrollPane panelUserList = new JScrollPane();
        panelUserList.setViewportView(userList);

        /*
            Kick panel
         */
        JPanel panelKick = new JPanel();
        panelKick.setPreferredSize(new Dimension(110, 40));
        panelKick.setLayout(new FlowLayout());

        /*
            Chat component
         */
        textPaneDisplay = new JTextPane();
        textPaneDisplay.setFocusable(false);
        chatListener.setTextPaneDisplay(textPaneDisplay);

        JScrollPane scrollPaneDisplay = new JScrollPane();
        scrollPaneDisplay.setViewportView(textPaneDisplay);

        JEditorPane editorPaneInput = new JEditorPane();
        editorPaneInput.setText("");
        chatListener.setEditorPaneInput(editorPaneInput);

        JScrollPane scrollPaneInput = new JScrollPane();
        scrollPaneInput.setViewportView(editorPaneInput);

        JButton btnSend = new JButton();
        btnSend.setText("Send");
        btnSend.addActionListener(chatListener);

        /*
            Board
         */
        BufferedImage img = new BufferedImage(680, 487, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = img.createGraphics();
        board = new Board(img);

        board.setBackground(Color.white);
        board.setBorder(new BevelBorder(BevelBorder.LOWERED));
        board.setLayout(new FlowLayout());

        board.addMouseListener(drawListener);
        board.addMouseMotionListener(drawListener);

        GroupLayout windowContentPaneLayout = new GroupLayout(windowContentPane);
        windowContentPane.setLayout(windowContentPaneLayout);
        windowContentPaneLayout.setHorizontalGroup(
                windowContentPaneLayout.createParallelGroup()
                        .addGroup(windowContentPaneLayout.createSequentialGroup()
                                .addGroup(windowContentPaneLayout.createParallelGroup()
                                        .addComponent(panelKick, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                                        .addGroup(windowContentPaneLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(windowContentPaneLayout.createParallelGroup()
                                                        .addComponent(panelUserList, GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                                                        .addComponent(panelDrawBtn, GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(board, GroupLayout.PREFERRED_SIZE, 675, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(windowContentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(scrollPaneDisplay)
                                        .addComponent(scrollPaneInput)
                                        .addComponent(btnSend, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        windowContentPaneLayout.setVerticalGroup(
                windowContentPaneLayout.createParallelGroup()
                        .addGroup(windowContentPaneLayout.createSequentialGroup()
                                .addGroup(windowContentPaneLayout.createParallelGroup()
                                        .addGroup(GroupLayout.Alignment.TRAILING, windowContentPaneLayout.createSequentialGroup()
                                                .addComponent(scrollPaneDisplay, GroupLayout.PREFERRED_SIZE, 315, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(scrollPaneInput)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(GroupLayout.Alignment.TRAILING, windowContentPaneLayout.createSequentialGroup()
                                                .addGap(0, 6, Short.MAX_VALUE)
                                                .addGroup(windowContentPaneLayout.createParallelGroup()
                                                        .addGroup(GroupLayout.Alignment.TRAILING, windowContentPaneLayout.createSequentialGroup()
                                                                .addComponent(panelDrawBtn, GroupLayout.PREFERRED_SIZE, 297, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(panelUserList, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(panelKick, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(board, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 487, GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap())
        );

        window.setJMenuBar(menuBar);
        window.pack();
        window.setLocationRelativeTo(window.getOwner());
        window.setVisible(true);

        drawListener.setBoard(board);
        drawListener.setGraphics2D(graphics2D);
        drawListener.setJsonArray(jsonArray);
    }

    public void clearBoard() {
        drawListener.draw(JSONObject.parseObject(String.format(
                "{\"Shape\": \"%s\", \"Parameters\": \"%s\", \"Color\": \"%s\"}",
                "FillRect", "0,0,680,487", "255,255,255"
        )));
    }

    public void raiseInfo(String info) {
        JOptionPane.showMessageDialog(board, info);
    }

    public void updateBoard(JSONArray newJSONArray) {
        clearBoard();
        for (Object object : newJSONArray) {
            drawListener.draw((JSONObject) object);
        }
    }

    public void updateBoard(JSONObject newJSONObject) {
        drawListener.draw(newJSONObject);
    }

    public void removeUser(String usernameToRemove) {
        userListModel.removeElement(usernameToRemove);
        userList.setModel(userListModel);
    }

    public void addUser(String usernameToAdd) {
        userListModel.addElement(usernameToAdd);
        userList.setModel(userListModel);
    }

    public void addChatText(String sender, String text) {
        String chatText = sender + ": " + text;
        if (textPaneDisplay.getText().equals(""))
            textPaneDisplay.setText(chatText);
        else
            textPaneDisplay.setText(textPaneDisplay.getText() + "\n" + chatText);
    }
}
