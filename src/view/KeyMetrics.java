package view;

import common.Metric;
import common.Observer;
import controller.AuctionController;
import org.jfree.chart.ChartPanel;

import java.awt.*;
import javax.swing.*;

public class KeyMetrics extends JPanel implements Observer {

    /**
     * @Author Zeno
     * @Date 02/03/2019
     */
    //private static final long serialVersionUID = 6340049980224403816L;
    private AuctionController controller;
    private ChartPanel cp;
    private ChartDisplay cd;
    public KeyMetrics(AuctionController controller, ChartPanel cp){
        this.controller=controller;
        controller.addObserver(this);
        this.cp=cp;
        cd=new ChartDisplay(controller);
        init();
    }



    private JLabel numOfImpressionLabel = new JLabel();
    private JLabel numOfClickLabel = new JLabel();
    private JLabel numOfUniqueLabel = new JLabel();
    private JLabel numOfBounceLabel = new JLabel();
    private JLabel numOfConversionLabel = new JLabel();
    private JLabel totalCostLabel = new JLabel();
    private JLabel ctrLabel = new JLabel();
    private JLabel cpcLabel = new JLabel();
    private JLabel cpmLabel = new JLabel();
    private JLabel cpaLabel = new JLabel();
    private JLabel bounceRateLabel = new JLabel();
    private ButtonGroup bg = new ButtonGroup();


    public void init() {


        this.setLayout(new GridLayout(11, 2, 0, 0));

        JRadioButton numOfImpression = new JRadioButton("Number of Impressions");
        numOfImpression.addActionListener(e->{
            cp.setChart(cd.getChart(Metric.NUM_OF_IMPRESSIONS));
            repaint();
        });

        JRadioButton numOfClick = new JRadioButton("Number of Clicks");
        numOfClick.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.NUM_OF_CLICKS));
            repaint();
        });

        JRadioButton numOfUnique = new JRadioButton("Number of Unique Clicks");
        numOfUnique.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.NUM_OF_UNIQUE_CLICKS));
            repaint();
        });

        JRadioButton numOfBounce = new JRadioButton("Number of Bounces");
        numOfBounce.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.NUM_OF_BOUNCES));
            repaint();
        });

        JRadioButton numOfConversion = new JRadioButton("Number of Conversions");
        numOfConversion.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.NUM_OF_CONVERSIONS));
            repaint();
        });

        JRadioButton totalCost = new JRadioButton("Total Cost");
        totalCost.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.TOTAL_COST));
            repaint();
        });

        JRadioButton ctr = new JRadioButton("Click-through-rate");
        ctr.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.CTR));
            repaint();
        });

        JRadioButton cpc = new JRadioButton("Cost-per-click");
        cpc.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.CPC));
            repaint();
        });

        JRadioButton cpm = new JRadioButton("Cost-per-thousand Impressions");
        cpm.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.CPM));
            repaint();
        });

        JRadioButton cpa = new JRadioButton("Cost-per-acquisition");
        cpa.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.CPA));
            repaint();
        });

        JRadioButton bounceRate = new JRadioButton("Bounce Rate");
        bounceRate.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.BOUNCE_RATE));
            repaint();
        });

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

        this.add(numOfImpression);
        this.add(numOfImpressionLabel);
        this.add(numOfClick);
        this.add(numOfClickLabel);
        this.add(numOfBounce);
        this.add(numOfBounceLabel);
        this.add(numOfUnique);
        this.add(numOfUniqueLabel);
        this.add(numOfConversion);
        this.add(numOfConversionLabel);
        this.add(totalCost);
        this.add(totalCostLabel);
        this.add(ctr);
        this.add(ctrLabel);
        this.add(cpc);
        this.add(cpcLabel);
        this.add(cpm);
        this.add(cpmLabel);
        this.add(cpa);
        this.add(cpaLabel);
        this.add(bounceRate);
        this.add(bounceRateLabel);

    }

    public void updateLabels(){
        numOfImpressionLabel.setText(Long.toString(controller.getNumOfImpressions()));
        numOfClickLabel.setText(Long.toString(controller.getNumOfClicks()));
        numOfUniqueLabel.setText(Long.toString(controller.getNumOfUniqueClicks()));
        numOfBounceLabel.setText(Long.toString(controller.getNumOfBounces()));
        numOfConversionLabel.setText(Long.toString(controller.getNumOfConversions()));
        totalCostLabel.setText(Double.toString(controller.getTotalCost())+" pence");
        ctrLabel.setText(Double.toString(controller.getCTR()));
        cpcLabel.setText(Double.toString(controller.getClickCost())+" pence");
        cpmLabel.setText(Double.toString(controller.getCPM())+" pence");
        cpaLabel.setText(Double.toString(controller.getCPA())+" pence");
        bounceRateLabel.setText(Double.toString(controller.getBounceRate()));
    }

    @Override
    public void update() {
        if(bg.getSelection()!=null) {
            bg.getSelection().setArmed(true);
            bg.getSelection().setPressed(true);
            bg.getSelection().setPressed(false);
            bg.getSelection().setArmed(false);
        }
        updateLabels();
    }
}
