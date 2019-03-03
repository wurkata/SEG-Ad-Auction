package view;

import java.awt.*;
import javax.swing.*;

public class KeyMetrics extends Component {

    /**
     * @Author Zeno
     * @Date 02/03/2019
     */
    private static final long serialVersionUID = 6340049980224403816L;

    private JRadioButton numOfImpression = new JRadioButton("Number of Impressions");
    private JRadioButton numOfClick = new JRadioButton("Number of Clicks");
    private JRadioButton numOfBounce = new JRadioButton("Number of Bounces");
    private JRadioButton numOfUnique = new JRadioButton("Number of Uniques");
    private JRadioButton numOfConversion = new JRadioButton("Number of Conversions");
    private JRadioButton totalCost = new JRadioButton("Total Cost");
    private JRadioButton ctr = new JRadioButton("Click-through-rate");
    private JRadioButton cpc = new JRadioButton("Cost-per-click");
    private JRadioButton cpm = new JRadioButton("Cost-per-thousand Ipressions");
    private JRadioButton cpa = new JRadioButton("Cost-per-acquisition");
    private JRadioButton bounceRate = new JRadioButton("Bounce Rate");

    public JPanel displayKeyMetrics() {
        JPanel km = new JPanel(new GridLayout(11, 1, 0, 0));

        ButtonGroup bg = new ButtonGroup();
        bg.add(numOfImpression);
        bg.add(numOfClick);
        bg.add(numOfBounce);
        bg.add(numOfUnique);
        bg.add(numOfConversion);
        bg.add(totalCost);
        bg.add(ctr);
        bg.add(cpc);
        bg.add(cpm);
        bg.add(cpa);
        bg.add(bounceRate);
        km.add(numOfImpression);
        km.add(numOfClick);
        km.add(numOfBounce);
        km.add(numOfUnique);
        km.add(numOfConversion);
        km.add(totalCost);
        km.add(ctr);
        km.add(cpc);
        km.add(cpm);
        km.add(cpa);
        km.add(bounceRate);
        numOfImpression.setSelected(true);

        return km;
    }

}
