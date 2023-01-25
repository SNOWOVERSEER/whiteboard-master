package whiteboard.view;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import whiteboard.controller.ChatListener;
import whiteboard.controller.DrawListener;
import whiteboard.controller.KickUserListener;
import whiteboard.controller.MenuBtnListener;
import whiteboard.model.UserManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ManagerWindow {
    private final JSONArray jsonArray;
    private final UserManager userManager;
    private final DrawListener drawListener;
    private final ChatListener chatListener;
    private final DefaultListModel<String> userListModel;
    private final JList<String> userList;
    private JTextPane textPaneDisplay;
    private Board board;

    public ManagerWindow(JSONArray jsonArray, UserManager userManager, String manager) {
        this.jsonArray = jsonArray;
        this.userManager = userManager;

        drawListener = new DrawListener(userManager, true);
        chatListener = new ChatListener(userManager, manager);

        userListModel = new DefaultListModel<>();
        userListModel.addElement("*" + userManager.getManager() + "*");
        userList = new JList<>(userListModel);

        init();
    }

    private void init() {
        JFrame window = new JFrame();
        window.setTitle("Distributed Shared Whiteboard [Manager]");
        window.setSize(800, 500);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        var windowContentPane = window.getContentPane();
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        /*
            Menu bar
         */
        JMenuBar menuBar = new JMenuBar();
        MenuBtnListener menuBtnListener = new MenuBtnListener();

        JButton btnNew = new JButton();
        btnNew.setText("New");
        btnNew.addActionListener(menuBtnListener);

        JButton btnOpen = new JButton();
        btnOpen.setText("Open");
        btnOpen.addActionListener(menuBtnListener);

        JButton btnSave = new JButton();
        btnSave.setText("Save");
        btnSave.addActionListener(menuBtnListener);

        JButton btnSaveAs = new JButton();
        btnSaveAs.setText("SaveAs");
        btnSaveAs.addActionListener(menuBtnListener);

        JButton btnClose = new JButton();
        btnClose.setText("Close");
        btnClose.addActionListener(menuBtnListener);

        menuBar.add(btnNew);
        menuBar.add(btnOpen);
        menuBar.add(btnSave);
        menuBar.add(btnSaveAs);
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
        KickUserListener kickUserListener = new KickUserListener(userManager, userList, userListModel);

        JButton btnKick = new JButton();
        btnKick.setText("Kick");
        btnKick.addActionListener(kickUserListener);

        JPanel panelKick = new JPanel();
        panelKick.setPreferredSize(new Dimension(110, 40));
        panelKick.setLayout(new FlowLayout());
        panelKick.add(btnKick);

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

        menuBtnListener.setUserManager(userManager);
        menuBtnListener.setBoard(board);
        menuBtnListener.setGraphics2D(graphics2D);
        menuBtnListener.setJsonArray(jsonArray);

        drawListener.setBoard(board);
        drawListener.setGraphics2D(graphics2D);
        drawListener.setJsonArray(jsonArray);
    }

    public void updateBoard(JSONObject newJSONObject) {
        drawListener.getJsonArray().add(newJSONObject);
        drawListener.draw(newJSONObject);
    }

    public void removeUser(String username) {
        userListModel.removeElement(username);
        userList.setModel(userListModel);
    }

    public void addUser(String username) {
        userListModel.addElement(username);
        userList.setModel(userListModel);
    }

    public boolean raisePermit(String username) {
        int managerOption = JOptionPane.showConfirmDialog(
                board,
                "Do you want " + username + " to join the whiteboard?",
                "Request For Join",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return managerOption == JOptionPane.YES_OPTION;
    }

    public void addChatText(String sender, String text) {
        String chatText = sender + ": " + text;
        if (textPaneDisplay.getText().equals(""))
            textPaneDisplay.setText(chatText);
        else
            textPaneDisplay.setText(textPaneDisplay.getText() + "\n" + chatText);
    }
}
