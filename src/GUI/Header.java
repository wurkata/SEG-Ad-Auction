import java.awt.*;
import javax.swing.*;

public class Header extends Component {

	JButton settings = new JButton("Preferences");
	JButton imp = new JButton("Import");
  JButton sav = new JButton("Save");
  JButton histogram = new JButton("View Histogram");
  JButton print = new JButton("Print");

  public JPanel displayHeader(){
    JPanel header = new JPanel(new GridLayout(1, 5));
    header.add(settings);
    header.add(imp);
    header.add(sav);
    header.add(histogram);
    header.add(print);

    return header;
  }
}
