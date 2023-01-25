package whiteboard.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import whiteboard.model.CommunicationUnit;
import whiteboard.model.UserManager;
import whiteboard.view.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;

import static whiteboard.controller.MessageType.*;

public class MenuBtnListener implements ActionListener {
    private final boolean flag;
    private final CommunicationUnit unit;
    private final String username;
    private UserManager userManager;
    private Board board;
    private Graphics2D graphics2D;
    private JSONArray jsonArray;

    public MenuBtnListener() {
        this.unit = null;
        this.flag = true;
        this.username = null;
    }

    public MenuBtnListener(CommunicationUnit unit, String username) {
        this.unit = unit;
        this.flag = false;
        this.username = username;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setGraphics2D(Graphics2D graphics2D) {
        this.graphics2D = graphics2D;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "New":
                System.out.println("btnNew Fired");
                if (confirmSave())
                    save("SaveAs");
                graphics2D.setColor(Color.white);
                graphics2D.fillRect(0, 0, 680, 487);
                jsonArray.clear();
                board.repaint();
                userManager.broadcastMsg(String.format("{\"Type\": \"%s\"}", CLEAR_USER_CANVAS));
                break;
            case "Open":
                System.out.println("btnOpen Fired");
                if (confirmSave())
                    save("SaveAs");
                open();
                String updateUserCanvasMsg = String.format(
                        "{\"Type\": \"%s\", \"Shapes\": %s}",
                        UPDATE_USER_CANVAS, jsonArray.toString()
                );
                userManager.broadcastMsg(updateUserCanvasMsg);
                break;
            case "Save":
                System.out.println("btnSave Fired");
                save("Save");
                break;
            case "SaveAs":
                System.out.println("btnSaveAs Fired");
                save("SaveAs");
                break;
            case "Close":
                System.out.println("btnClose Fired");
                if (flag) {
                    userManager.broadcastMsg(String.format("{\"Type\":\"%s\"}", GOODBYE));
                } else {
                    String leaveWhiteboardRequest = String.format(
                            "{\"Type\": \"%s\", \"Username\": \"%s\"}",
                            LEAVE_WHITEBOARD, username);
                    try {
                        assert unit != null;
                        unit.send(leaveWhiteboardRequest);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    try {
                        unit.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                System.exit(1);
                break;
            default:
                System.out.println("Unknown Menu Button");
        }
    }

    private void save(String cmd) {
        BufferedImage bufferedImage = new BufferedImage(board.getWidth(), board.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, board.getWidth(), board.getHeight());
        g2d.setColor(Color.BLACK);
        for (Object object : jsonArray)
            draw((JSONObject) object, g2d);
        g2d.dispose();

        try {
            System.out.println("writing");
            if (Objects.equals(cmd, "Save")) {
                BufferedWriter bw = new BufferedWriter(new FileWriter("./temp.txt"));   //save the JsonArray string
                bw.write(jsonArray.toJSONString());
                bw.close();

                ImageIO.write(bufferedImage, "jpg", new File("./temp.jpg"));  // save the image
            } else {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setCurrentDirectory(new java.io.File("."));
                jFileChooser.setDialogTitle("Please choose file to save");

                int intRetVal = jFileChooser.showSaveDialog(board);
                if (intRetVal == JFileChooser.APPROVE_OPTION) {
                    File fiveToSave = jFileChooser.getSelectedFile();
                    ImageIO.write(bufferedImage, "jpg", new File(fiveToSave.getParentFile(), fiveToSave.getName() + ".jpg"));
                    if (!fiveToSave.getName().toLowerCase().endsWith(".txt")) {
                        fiveToSave = new File(fiveToSave.getParentFile(), fiveToSave.getName() + ".txt");
                    }
                    BufferedWriter bw = new BufferedWriter(new FileWriter(fiveToSave));
                    bw.write(jsonArray.toJSONString());
                    bw.close();
                }
            }

        } catch (IOException e) {
            System.out.println("Error exist");
        }
    }

    private boolean confirmSave() {
        if (jsonArray.size() != 0) {
            int userOption = JOptionPane.showConfirmDialog(
                    board,
                    "Do you want to save the whiteboard?",
                    "Reminder",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            return userOption == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
    }

    private void open() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new java.io.File("."));
        jFileChooser.setDialogTitle("Please choose file to open");
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int intRetVal = jFileChooser.showOpenDialog(board);
        if (intRetVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                BufferedReader br = new BufferedReader(reader);
                String str = br.readLine();
                System.out.println(str);
                JSONArray jsonArrayRead = JSONArray.parseArray(str);
                graphics2D.setColor(Color.white);
                graphics2D.fillRect(0, 0, 680, 487);
                jsonArray.clear();
                jsonArray.addAll(jsonArrayRead);
                for (Object object : jsonArray)
                    draw((JSONObject) object, graphics2D);
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
    }

    private void draw(JSONObject jsonObject, Graphics2D g2d) {
        String shape = (String) jsonObject.get("Shape");
        String param = (String) jsonObject.get("Parameters");
        String color = (String) jsonObject.get("Color");

        int param1, param2, param3, param4, r, g, b;
        String[] params = param.split(",");
        String[] colors = color.split(",");

        param1 = Integer.parseInt(params[0]);
        param2 = Integer.parseInt(params[1]);
        param3 = Integer.parseInt(params[2]);
        param4 = Integer.parseInt(params[3]);

        r = Integer.parseInt(colors[0]);
        g = Integer.parseInt(colors[1]);
        b = Integer.parseInt(colors[2]);
        g2d.setColor(new Color(r, b, g));

        switch (shape) {
            case "Line":
                g2d.drawLine(param1, param2, param3, param4);

                break;
            case "Circle":
                g2d.drawOval(param1, param2, param3, param4);
                break;
            case "Triangle":
                g2d.drawLine(param1, param2, param3, param4);
                g2d.drawLine(param1, param4, param3, param4);
                g2d.drawLine(param1, param2, param1, param4);
                break;
            case "Rectangle":
                g2d.drawRect(param1, param2, param3, param4);
                break;
            case "Text":
                String str = (String) jsonObject.get("Text");
                g2d.drawString(str, param1, param2);
                break;
            default:
                System.out.println("Unknown Shape");
        }

        board.repaint();
    }
}
