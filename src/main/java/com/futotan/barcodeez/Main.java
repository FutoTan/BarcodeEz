package com.futotan.barcodeez;

import com.futotan.barcodeez.frames.MainFrame;

public class Main {
    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        javax.swing.SwingUtilities.invokeLater(mainFrame::build);
    }
}