package view;

import common.Granularity;
import controller.AuctionController;

import java.awt.*;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ChartControl extends Component {

    private static final long serialVersionUID = -4281748239449190331L;

//    private String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
//    private String[] hours = {"0:00-0:59", "1:00-1:59", "2:00-2:59", "3:00-3:59", "4:00-4:59", "5:00-5:59", "6:00-6:59", "7:00-7:59", "8:00-8:59", "9:00-9:59", "10:00-10:59", "11:00-11:59", "12:00-12:59", "13:00-13:59", "14:00-14:59", "15:00-15:59", "16:00-16:59", "17:00-17:59", "18:00-18:59", "19:00-19:59", "20:00-20:59", "21:00-21:59", "22:00-22:59", "23:00-23:59"};
//    private String[] income = {"Low", "Medium", "High"};
//    private String[] gender = {"Male", "Female"};
//    private String[] context = {"News", "Shopping", "Social", "Media", "Blog", "Hobbies", "Travel"};
//    private String[] age = {"<25", "25-34", "35-44", "45-54", ">54"};
    private JSlider tgs = new JSlider(JSlider.HORIZONTAL, 0, 3, 1);
//    private JComboBox<String> dow = new JComboBox<String>(days);
//    private JComboBox<String> tod = new JComboBox<String>(hours);
//    private JComboBox<String> inc = new JComboBox<String>(income);
//    private JComboBox<String> gen = new JComboBox<String>(gender);
//    private JComboBox<String> con = new JComboBox<String>(context);
//    private JComboBox<String> ag = new JComboBox<String>(age);

    private AuctionController controller;

    public JPanel displayChartControls(AuctionController controller) {
        this.controller=controller;
        JPanel cc = new JPanel(new FlowLayout());

        tgs.setSnapToTicks(true);
        tgs.setPaintLabels(true);
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(0, new JLabel("Hours"));
        labels.put(1, new JLabel("Days"));
        labels.put(2, new JLabel("Months"));
        labels.put(3, new JLabel("Years"));
        tgs.setLabelTable(labels);
        tgs.addChangeListener(e->{
            switch(((JSlider)e.getSource()).getValue()) {
                case 0:
                    controller.setGranularity(Granularity.HOUR);
                    break;
                case 1:
                    controller.setGranularity(Granularity.DAY);
                    break;
                case 2:
                    controller.setGranularity(Granularity.MONTH);
                    break;
                case 3:
                    controller.setGranularity(Granularity.YEAR);
                    break;
            }

        });
        JLabel tgsLabel = new JLabel("Chart Granularity");


        SpinnerNumberModel bouncePageModel = new SpinnerNumberModel(0,0,Integer.MAX_VALUE,1);
        JSpinner bouncePageSpinner = new JSpinner(bouncePageModel);
        bouncePageSpinner.addChangeListener(e->controller.setBouncePageReq((Integer) bouncePageSpinner.getValue()));
        JLabel pageSpinner = new JLabel("Page Bounce Threshold");


        SpinnerNumberModel bounceSecondModel = new SpinnerNumberModel(0,0,59,1);
        SpinnerNumberModel bounceMinuteModel = new SpinnerNumberModel(0,0,59,1);
        SpinnerNumberModel bounceHourModel = new SpinnerNumberModel(0,0,Integer.MAX_VALUE,1);
        JSpinner bounceSecondSpinner = new JSpinner(bounceSecondModel);
        JSpinner bounceMinuteSpinner = new JSpinner(bounceMinuteModel);
        JSpinner bounceHourSpinner = new JSpinner(bounceHourModel);
        TimeListener tl = new TimeListener(bounceHourSpinner, bounceMinuteSpinner, bounceSecondSpinner, controller);
        bounceSecondSpinner.addChangeListener(tl);
        bounceMinuteSpinner.addChangeListener(tl);
        bounceHourSpinner.addChangeListener(tl);
        JLabel bounceTime = new JLabel("Time Bounce Threshold (hh:mm:ss)");

        JPanel tgsPanel = new JPanel();
        tgsPanel.setLayout(new BoxLayout(tgsPanel, BoxLayout.LINE_AXIS));
        tgsPanel.add(tgsLabel);
        tgsPanel.add(tgs);

        JPanel pagePanel = new JPanel();
        pagePanel.setLayout(new BoxLayout(pagePanel, BoxLayout.LINE_AXIS));
        pagePanel.add(pageSpinner);
        pagePanel.add(bouncePageSpinner);

        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.LINE_AXIS));
        timePanel.add(bounceTime);
        timePanel.add(bounceHourSpinner);
        timePanel.add(new JLabel(":"));
        timePanel.add(bounceMinuteSpinner);
        timePanel.add(new JLabel(":"));
        timePanel.add(bounceSecondSpinner);


        cc.add(tgsPanel);
        cc.add(pagePanel);
        cc.add(timePanel);
//        cc.add(dow);
//        cc.add(tod);
//        cc.add(inc);
//        cc.add(gen);
//        cc.add(con);
//        cc.add(ag);

        return cc;
    }
    private class TimeListener implements ChangeListener{
        private JSpinner hours, minutes, seconds;
        private AuctionController controller;
        public TimeListener(JSpinner hours, JSpinner minutes, JSpinner seconds, AuctionController controller){
            this.hours=hours;
            this.minutes=minutes;
            this.seconds=seconds;
            this.controller=controller;
        }

        @Override
        public void stateChanged(ChangeEvent e){
            controller.setBounceTime((Integer)seconds.getValue()*1000 + (Integer)minutes.getValue()*60*1000 + (Integer)hours.getValue()*60*60*1000);
        }
    }
}
