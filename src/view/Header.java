package view;

import controller.AuctionController;

import java.awt.*;
import javax.swing.*;

public class Header extends Component {
    private AuctionController controller;

    public JPanel displayHeader(AuctionController controller) {
        this.controller=controller;
        JPanel header = new JPanel(new GridLayout(1, 5));
        JButton settings = new JButton("Preferences");
        //settings.setEnabled(false);
        settings.addActionListener(e->{
            JFrame frame = new JFrame("Preferences");
            frame.setContentPane(new PreferencesPanel(controller, frame));
            frame.pack();
            frame.setVisible(true);
        });
        JButton imp = new JButton("Import");
        JButton sav = new JButton("Save");
        sav.setEnabled(false);
        JButton histogram = new JButton("View Histogram");
        histogram.setEnabled(false);
        JButton print = new JButton("Print");
        print.setEnabled(false);

        header.add(settings);
        header.add(imp);
        imp.addActionListener(e->{
            JFrame frame = new JFrame("Select files to import");
            frame.setContentPane(new FileSelectPanel(controller, frame));
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });


        header.add(sav);
        header.add(histogram);
        header.add(print);

        return header;
    }
}
