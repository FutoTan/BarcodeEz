package com.futotan.barcodeez.frames;

import com.futotan.barcodeez.utils.Barcode;
import com.futotan.barcodeez.widgets.BarcodePreview;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class MainFrame extends JFrame implements ActionListener {
    private static final String imageDescription = "Image file(png|jpg|jpeg|gif)";
    private static final List<String> imageExtensions = List.of("png","jpg","jpeg","gif");

    private JMenuItem fileItem,screenshotItem,closeItem;
    private JPanel panel;
    private BarcodePreview barcodePreview;
    private JTextArea outputInfoArea;
    private JTextArea detailsInfoArea;

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(closeItem)) {
            this.dispose();
        } else if (source.equals(fileItem)) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter(imageDescription, imageExtensions.toArray(String[]::new)));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                processBarcode(String.valueOf(fileChooser.getSelectedFile()));
            }
        } else if (source.equals(screenshotItem)) {
            ScreenshotFrame frame = new ScreenshotFrame(this);

            try {
                frame.build();
            } catch (Exception exception) {
                System.out.println("[Error] Failed to get screenshot");
                System.out.println(exception.getMessage());
            }
        }
    }

    private void setFileDrop() {
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    Transferable transferable = event.getTransferable();
                    if(transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                        List files = (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);

                        for (Object object : files) {
                            File file = (File) object;
                            if (!file.isFile()) continue;

                            String path = file.getAbsolutePath();
                            String extension = path.substring(path.lastIndexOf(".") + 1);

                            if (imageExtensions.contains(extension)) {
                                processBarcode(path);
                                break;
                            }
                        }
                        event.dropComplete(true);
                    } else {
                        event.rejectDrop();
                    }
                } catch(Exception e) {
                    System.out.println("[Error] Failed to drop file");
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    public void processBarcode(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            processBarcode(image);
        } catch (Exception e) {
            System.out.println("[Error] Failed to load image");
            System.out.println(e.getMessage());
        }
    }

    public void processBarcode(BufferedImage image) {
        processBarcode(image,null);
    }

    public void processBarcode(BufferedImage image, Runnable onError) {
        try {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Result result = Barcode.decode(bitmap);
            outputInfoArea.setText(result.getText());
            detailsInfoArea.setText(Barcode.getDetailedInfo(result));
            barcodePreview.loadImage(image);
        } catch (Exception e) {
            if (onError != null) {
                onError.run();
            }

            JOptionPane.showMessageDialog(this, "Barcode was not found!");
        }
    }

    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Open
        JMenu openMenu = new JMenu("Open");

        fileItem = new JMenuItem("From File");
        fileItem.addActionListener(this);
        openMenu.add(fileItem);

        screenshotItem = new JMenuItem("From Screenshot");
        screenshotItem.addActionListener(this);
        openMenu.add(screenshotItem);

        openMenu.addSeparator();

        closeItem = new JMenuItem("Close");
        closeItem.addActionListener(this);
        openMenu.add(closeItem);

        menuBar.add(openMenu);

        this.setJMenuBar(menuBar);
    }

    public void buildPanel() {
        panel.setLayout(new GridLayout(2,1,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        barcodePreview = new BarcodePreview();

        panel.add(barcodePreview);

        JTabbedPane tabbedPane = new JTabbedPane();

        outputInfoArea = buildInfoArea();

        JScrollPane scrollPane = new JScrollPane(outputInfoArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        tabbedPane.addTab("Output", scrollPane);

        detailsInfoArea = buildInfoArea();

        JScrollPane scrollPane2 = new JScrollPane(detailsInfoArea);
        scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        tabbedPane.addTab("Details", scrollPane2);

        panel.add(tabbedPane);
    }

    public JTextArea buildInfoArea() {
        JTextArea textArea = new JTextArea();

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem copyItem = new JMenuItem("Copy");
                    copyItem.addActionListener(e -> textArea.copy());
                    popupMenu.add(copyItem);
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });

        return textArea;
    }

    public void build() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("[Error] Failed to set look and feel");
            System.out.println(e.getMessage());
        }

        this.setTitle("BarcodeEz");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        this.setMinimumSize(new Dimension(350,350));
        this.setLocation(
                (screenSize.width - this.getWidth()) / 2,
                (screenSize.height - this.getHeight()) / 2
        );
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setFileDrop();

        buildMenuBar();

        panel = new JPanel();
        buildPanel();
        this.add(panel);

        this.pack();
        this.setVisible(true);
    }
}
