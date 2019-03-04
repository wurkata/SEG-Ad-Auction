package view;

import controller.AuctionController;

import java.awt.*;
import javax.swing.*;

public class Header extends Component {

    private JButton settings = new JButton("Preferences");
    private JButton imp = new JButton("Import");
    private JButton sav = new JButton("Save");
    private JButton histogram = new JButton("View Histogram");
    private JButton print = new JButton("Print");

    private AuctionController controller;

    public JPanel displayHeader(AuctionController controller) {
        this.controller=controller;
        JPanel header = new JPanel(new GridLayout(1, 5));
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
