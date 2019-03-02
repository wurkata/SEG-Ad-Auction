import java.awt.*;
import javax.swing.*;

public class KeyMetrics extends Component {

	/**
	 * 
	 * @Author Zeno 
	 * @Date 02/03/2019
	 * 
	 */
	private static final long serialVersionUID = 6340049980224403816L;

	JRadioButton numOfImpression = new JRadioButton("Number of Impressions");
	JRadioButton numOfClick = new JRadioButton("Number of Clicks");
	JRadioButton numOfBounce = new JRadioButton("Number of Bounces");
	JRadioButton numOfUnique = new JRadioButton("Number of Uniques");
	JRadioButton numOfConversion = new JRadioButton("Number of Conversions");
	JRadioButton totalCost = new JRadioButton("Total Cost");
	JRadioButton ctr = new JRadioButton("Click-through-rate");
	JRadioButton cpc = new JRadioButton("Cost-per-click");
	JRadioButton cpm = new JRadioButton("Cost-per-thousand Ipressions");
	JRadioButton cpa = new JRadioButton("Cost-per-acquisition");
	JRadioButton bounceRate = new JRadioButton("Bounce Rate");

	public JPanel displayKeyMetrics() {
		JPanel km = new JPanel(new GridLayout(11,1,0,0));
		
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
