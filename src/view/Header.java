package view;

import java.awt.*;
import javax.swing.*;

public class Header extends Component {

    private JButton settings = new JButton("Preferences");
    private JButton imp = new JButton("Import");
    private JButton sav = new JButton("Save");
    private JButton histogram = new JButton("View Histogram");
    private JButton print = new JButton("Print");

    public JPanel displayHeader() {
        JPanel header = new JPanel(new GridLayout(1, 5));
        header.add(settings);
        header.add(imp);
        header.add(sav);
        header.add(histogram);
        header.add(print);

        return header;
    }
}
