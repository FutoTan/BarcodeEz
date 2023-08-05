package com.futotan.barcodeez.widgets;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

// 二维码预览
public class BarcodePreview extends JPanel {
    public BarcodePreview() {
        this.setLayout(new GridLayout(1,1,0,0));

        JLabel label = new JLabel("No image");
        label.setHorizontalAlignment(JLabel.CENTER);
        this.add(label);
    }

    public void loadImage(BufferedImage image) {
        ImageIcon icon = new ImageIcon(image) {
            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                double ratio = Math.min(
                        (double) c.getWidth() / getIconWidth(),
                        (double) c.getHeight() / getIconHeight());
                int width = (int) (getIconWidth() * ratio);
                int height = (int) (getIconHeight() * ratio);

                g.drawImage(getImage(), (c.getWidth() - width) / 2, (c.getHeight() - height) / 2, width, height, null);
            }
        };

        JLabel label = new JLabel();
        label.setIcon(icon);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBounds(this.getBounds());

        this.removeAll();
        this.add(label);
        repaint();
    }
}
