package whiteboard.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Board extends JPanel {
    private final BufferedImage img;

    public Board(BufferedImage img) {
        this.img = img;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        int w = img.getWidth();
        int h = img.getHeight();
        graphics2D.drawImage(img, 0, 0, w, h, null);
        graphics2D.dispose();
    }
}
