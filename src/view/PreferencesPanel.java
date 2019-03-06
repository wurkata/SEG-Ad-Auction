/**
 * Created by William Kewell on 04/03/2019
 */
package view;

import controller.AuctionController;

import javax.swing.*;
import java.awt.*;

public class PreferencesPanel extends JPanel {

    private AuctionController controller;
    private JFrame frame;

    public PreferencesPanel(AuctionController controller, JFrame frame) {
        this.controller=controller;
        this.frame=frame;
        init();
    }

    private JLabel fontSizeLabel = new JLabel("Font Size:");
    private ButtonGroup bg = new ButtonGroup();


    public void init() {
        this.setLayout(new GridLayout(2,2));
        this.setPreferredSize(new Dimension(300,100));

        JRadioButton lightModeButton = new JRadioButton("Light Mode");
        //lightModeButton.addActionListener(e->{

        JRadioButton darkModeButton = new JRadioButton("Dark Mode");
        //darkModeButton.addActionListener(e->{

        JSpinner fontSizeSpinner = new JSpinner();
        //fontSizeSpinner.addActionListener(e->{

        bg.add(lightModeButton);
        bg.add(darkModeButton);

        //this.add(lightModeLabel);
        this.add(lightModeButton);
        //this.add(darkModeLabel);
        this.add(darkModeButton);
        this.add(fontSizeLabel);
        this.add(fontSizeSpinner);
    }
}
