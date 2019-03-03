import java.awt.*;

import javax.swing.*;

public class ChartControl extends Component{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4281748239449190331L;
	
	String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
	String[] hours = {"0:00-0:59","1:00-1:59","2:00-2:59","3:00-3:59","4:00-4:59","5:00-5:59","6:00-6:59","7:00-7:59","8:00-8:59","9:00-9:59","10:00-10:59","11:00-11:59","12:00-12:59","13:00-13:59","14:00-14:59","15:00-15:59","16:00-16:59","17:00-17:59","18:00-18:59","19:00-19:59","20:00-20:59","21:00-21:59","22:00-22:59","23:00-23:59"};
	String[] income = {"Low", "Mdeium", "High"};
	String[] gender = {"Male", "Female"};
	String[] context = {"News", "Shopping", "Social", "Media", "Blog", "Hobbies", "Travel"};
	String[] age = {"<25", "25-34", "35-44", "45-54", ">54"}; 
	JSlider tgs = new JSlider(JSlider.HORIZONTAL, 0, 50, 5); 
	JComboBox<String> dow = new JComboBox<String>(days);
	JComboBox<String> tod = new JComboBox<String>(hours);
	JComboBox<String> inc = new JComboBox<String>(income);
	JComboBox<String> gen = new JComboBox<String>(gender);
	JComboBox<String> con = new JComboBox<String>(context);
	JComboBox<String> ag = new JComboBox<String>(age);
	
	public JPanel displayChartControls() {
		JPanel cc = new JPanel(new FlowLayout());
		cc.add(tgs);
		tgs.setMajorTickSpacing(10);
		tgs.setMinorTickSpacing(2);
		tgs.setSnapToTicks(true);
		tgs.setName("Time Granuality Slider");
		tgs.setPaintLabels(true);
		cc.add(dow);
		cc.add(tod);
		cc.add(inc);
		cc.add(gen);
		cc.add(con);
		cc.add(ag);
		
		return cc;
		
	}

}
