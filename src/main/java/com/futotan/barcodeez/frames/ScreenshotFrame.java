package com.futotan.barcodeez.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ScreenshotFrame extends JFrame {
    final private MainFrame parent;

    public ScreenshotFrame(MainFrame parent) {
        this.parent = parent;
    }

    private List<Integer> startPos = List.of(0,0);
    private List<Integer> endPos = List.of(0,0);

    private BufferedImage screenshot;

    public void build() throws AWTException {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setBackground(Color.BLACK);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setAlwaysOnTop(true);
        this.setResizable(false);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Robot robot = new Robot();
        screenshot = robot.createScreenCapture(new Rectangle(screenSize.width, screenSize.height));

        ScreenshotPanel panel = new ScreenshotPanel(this);
        panel.setBounds(this.getBounds());
        this.add(panel);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
    }

    private void processScreenshot(Rectangle rectangle) {
        parent.processBarcode(screenshot.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height), new Runnable() {
            @Override
            public void run() {
                dispose();
            }
        });
        dispose();
    }

    class ScreenshotPanel extends JPanel {
        private boolean isPressed = false;

        ScreenshotPanel(ScreenshotFrame parent) {
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    startPos = List.of(e.getX(),e.getY());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    int x = Math.min(startPos.get(0),endPos.get(0));
                    int y = Math.min(startPos.get(1),endPos.get(1));
                    int width = Math.abs(endPos.get(0) - startPos.get(0));
                    int height = Math.abs(endPos.get(1) - startPos.get(1));

                    Rectangle rectangle = new Rectangle(x,y,width,height);

                    parent.processScreenshot(rectangle);
                }
            });

            this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isPressed) {
                        endPos = List.of(e.getX(),e.getY());
                        repaint();
                    }
                }
            });

            parent.pack();
            parent.setVisible(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(screenshot,0,0,this);

            if (isPressed) {
                g.setColor(new Color(0,0,0,64));

                int x = Math.min(startPos.get(0),endPos.get(0));
                int y = Math.min(startPos.get(1),endPos.get(1));
                int width = Math.abs(endPos.get(0) - startPos.get(0));
                int height = Math.abs(endPos.get(1) - startPos.get(1));

                // 多边形，四边形挖掉中间，分两次画L形状
                g.fillPolygon(new int[]{0,x,x,x+width,x+width,0}, new int[]{0,0,y+height,y+height,getHeight(),getHeight()}, 6);
                g.fillPolygon(new int[]{x,getWidth(),getWidth(),x+width,x+width,x}, new int[]{0,0,getHeight(),getHeight(),y,y}, 6);
                
                g.setColor(Color.CYAN);
                g.drawRect(x,y,width,height);
            } else {
                g.setColor(new Color(0,0,0,64));
                g.fillRect(0,0,getWidth(),getHeight());
            }
        }
    }
}
