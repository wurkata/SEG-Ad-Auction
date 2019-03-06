package view;

import controller.AuctionController;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Created by furqan on 04/03/2019.
 */
public class FileSelectPanel extends JPanel {
    private AuctionController controller;
    private JFrame frame;

    public FileSelectPanel(AuctionController controller, JFrame frame) {
        this.controller = controller;
        this.frame = frame;
        init();
    }


    public void init() {
        this.setLayout(new GridLayout(3, 3));
        this.setPreferredSize(new Dimension(500, 100));

        JLabel impLabel = new JLabel("No file selected");
        JLabel clickLabel = new JLabel("No file selected");
        JLabel serverLabel = new JLabel("No file selected");
        JButton impButton = new JButton("Select impression log");

        impButton.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.addChoosableFileFilter(new FileNameExtensionFilter("CSV file", "csv"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File impLog = fc.getSelectedFile();
                impLabel.setText(impLog.getAbsolutePath());
            }
        });

        JButton clickButton = new JButton("Select click log");
        clickButton.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.addChoosableFileFilter(new FileNameExtensionFilter("CSV file", "csv"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File clickLog = fc.getSelectedFile();
                clickLabel.setText(clickLog.getAbsolutePath());
            }
        });

        JButton serverButton = new JButton("Select server log");
        serverButton.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.addChoosableFileFilter(new FileNameExtensionFilter("CSV file", "csv"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File serverLog = fc.getSelectedFile();
                serverLabel.setText(serverLog.getAbsolutePath());
            }
        });


        JButton confirm = new JButton("Confirm");
        confirm.addActionListener(e -> {
            if (!impLabel.getText().equals("No file selected") && !clickLabel.equals("No file selected") && !serverLabel.equals("No file selected")) {
                try {
                    controller.setModel(new File(impLabel.getText()), new File(clickLabel.getText()), new File(serverLabel.getText()));
                    frame.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        this.add(impLabel);
        this.add(clickLabel);
        this.add(serverLabel);
        this.add(impButton);
        this.add(clickButton);
        this.add(serverButton);
        this.add(new JLabel());
        this.add(confirm);

    }

}
