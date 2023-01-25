package whiteboard.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import whiteboard.model.CommunicationUnit;
import whiteboard.model.UserManager;
import whiteboard.view.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import static whiteboard.controller.MessageType.SEND_SHAPE_TO_USER;
import static whiteboard.controller.MessageType.UPDATE_SERVER_CANVAS;

public class DrawListener implements ActionListener, MouseListener, MouseMotionListener {
    private final UserManager userManager;
    private final boolean flag;
    private final CommunicationUnit unit;
    private Board board;
    private Graphics2D graphics2D;
    private JSONArray jsonArray;
    private String shape;
    private Color color;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public DrawListener(UserManager userManager, boolean flag) {
        this.userManager = userManager;
        this.flag = flag;
        this.unit = null;
        shape = "Line";
        color = Color.BLACK;
    }

    public DrawListener(boolean flag, CommunicationUnit unit) {
        this.userManager = null;
        this.flag = flag;
        this.unit = unit;
        shape = "Line";
        color = Color.BLACK;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setGraphics2D(Graphics2D graphics2D) {
        this.graphics2D = graphics2D;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        for (Object object : jsonArray)
            draw((JSONObject) object);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actCmd = e.getActionCommand();
        if (actCmd.equals("Color")) {
            System.out.println("btnColor Fired");
            color = JColorChooser.showDialog(new JFrame(), "Select a Color", Color.black);
        } else {
            System.out.println("btn" + actCmd + " Fired");
            shape = actCmd;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        graphics2D.setStroke(new BasicStroke(1));
        graphics2D.setColor(color);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        endX = e.getX();
        endY = e.getY();
        JSONObject jsonObject = null;
        switch (shape) {
            case "Line":
            case "Triangle":
                jsonObject = JSONObject.parseObject(String.format(
                        "{\"Shape\":\"%s\",\"Parameters\":\"%d,%d,%d,%d\",\"Color\":\"%d,%d,%d\"}",
                        shape, startX, startY, endX, endY, color.getRed(), color.getBlue(), color.getGreen()
                ));
                jsonArray.add(jsonObject);
                draw(jsonObject);
                break;
            case "Rectangle":
                jsonObject = JSONObject.parseObject(String.format(
                        "{\"Shape\":\"%s\",\"Parameters\":\"%d,%d,%d,%d\",\"Color\":\"%d,%d,%d\"}",
                        shape, startX, startY, Math.abs(endX - startX), Math.abs(endY - startY), color.getRed(), color.getBlue(), color.getGreen()
                ));
                jsonArray.add(jsonObject);
                draw(jsonObject);
                break;
            case "Circle":
                jsonObject = JSONObject.parseObject(String.format(
                        "{\"Shape\":\"%s\",\"Parameters\":\"%d,%d,%d,%d\",\"Color\":\"%d,%d,%d\"}",
                        shape, startX, startY, Math.abs(endX - startX), Math.abs(endX - startX), color.getRed(), color.getBlue(), color.getGreen()
                ));
                jsonArray.add(jsonObject);
                draw(jsonObject);
                break;
            case "Text":
                String text = JOptionPane.showInputDialog(board, "Please input the text");
                jsonObject = JSONObject.parseObject(String.format(
                        "{\"Shape\":\"%s\",\"Parameters\":\"%d,%d,0,0\",\"Text\":\"%s\",\"Color\":\"%d,%d,%d\"}",
                        shape, endX, endY, text, color.getRed(), color.getBlue(), color.getGreen()
                ));
                jsonArray.add(jsonObject);
                draw(jsonObject);
                break;
            case "FreeHand":
                break;
            default:
                System.out.println("Unknown Shape");
        }

        if (jsonObject != null) {
            if (flag)
                sendNewShapeToAllUsers(jsonObject);
            else
                sendNewShapeToServer(jsonObject);
        }
    }

    private void sendNewShapeToAllUsers(JSONObject jsonObject) {
        String shape = jsonObject.getString("Shape");
        String params = jsonObject.getString("Parameters");
        String color = jsonObject.getString("Color");
        String sendShapeToUserMsg;
        if (shape.equals("Text")) {
            String text = jsonObject.getString("Text");
            sendShapeToUserMsg = String.format(
                    "{\"Type\": \"%s\", \"Shape\": \"%s\", \"Parameters\": \"%s\", \"Color\": \"%s\", \"Text\": \"%s\"}",
                    SEND_SHAPE_TO_USER, shape, params, color, text
            );
        } else {
            sendShapeToUserMsg = String.format(
                    "{\"Type\": \"%s\", \"Shape\": \"%s\", \"Parameters\": \"%s\", \"Color\": \"%s\"}",
                    SEND_SHAPE_TO_USER, shape, params, color
            );
        }
        assert userManager != null;
        userManager.broadcastMsg(sendShapeToUserMsg);
    }

    private void sendNewShapeToServer(JSONObject jsonObject) {
        String shape = jsonObject.getString("Shape");
        String params = jsonObject.getString("Parameters");
        String color = jsonObject.getString("Color");
        String updateServerCanvasMsg;
        if (shape.equals("Text")) {
            String text = jsonObject.getString("Text");
            updateServerCanvasMsg = String.format(
                    "{\"Type\": \"%s\", \"Shape\": \"%s\", \"Parameters\": \"%s\", \"Color\": \"%s\", \"Text\": \"%s\"}",
                    UPDATE_SERVER_CANVAS, shape, params, color, text
            );
        } else {
            updateServerCanvasMsg = String.format(
                    "{\"Type\": \"%s\", \"Shape\": \"%s\", \"Parameters\": \"%s\", \"Color\": \"%s\"}",
                    UPDATE_SERVER_CANVAS, shape, params, color
            );
        }
        try {
            assert unit != null;
            unit.send(updateServerCanvasMsg);
        } catch (IOException e) {
            System.out.println("Message" + updateServerCanvasMsg + " Send Failed");
            e.printStackTrace();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        endX = e.getX();
        endY = e.getY();
        if (shape.equals("FreeHand")) {
            graphics2D.drawLine(startX, startY, endX, endY);
            JSONObject jsonObject = JSONObject.parseObject(String.format(
                    "{\"Shape\":\"Line\",\"Parameters\":\"%d,%d,%d,%d\",\"Color\":\"%d,%d,%d\"}",
                    startX, startY, endX, endY, color.getRed(), color.getBlue(), color.getGreen()
            ));
            jsonArray.add(jsonObject);
            startX = endX;
            startY = endY;
            board.repaint();
            if (flag)
                sendNewShapeToAllUsers(jsonObject);
            else sendNewShapeToServer(jsonObject);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void draw(JSONObject jsonObject) {
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
        graphics2D.setColor(new Color(r, b, g));

        switch (shape) {
            case "Line":
                graphics2D.drawLine(param1, param2, param3, param4);
                break;
            case "Circle":
                graphics2D.drawOval(param1, param2, param3, param4);
                break;
            case "Triangle":
                graphics2D.drawLine(param1, param2, param3, param4);
                graphics2D.drawLine(param1, param4, param3, param4);
                graphics2D.drawLine(param1, param2, param1, param4);
                break;
            case "Rectangle":
                graphics2D.drawRect(param1, param2, param3, param4);
                break;
            case "Text":
                String str = (String) jsonObject.get("Text");
                graphics2D.drawString(str, param1, param2);
                break;
            case "FillRect":
                graphics2D.fillRect(param1, param2, param3, param4);
                break;
            default:
                System.out.println("Unknown Shape");
                return;
        }

        board.repaint();
    }
}
