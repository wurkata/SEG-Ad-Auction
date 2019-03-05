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


    private JRadioButton numOfImpression, numOfClick, numOfBounce, numOfUnique, numOfConversion, totalCost, ctr, cpc, cpm, cpa, bounceRate;
    private JLabel numOfImpressionLabel = new JLabel("n/a");
    private JLabel numOfClickLabel = new JLabel("n/a");
    private JLabel numOfUniqueLabel = new JLabel("n/a");
    private JLabel numOfBounceLabel = new JLabel("n/a");
    private JLabel numOfConversionLabel = new JLabel("n/a");
    private JLabel totalCostLabel = new JLabel("n/a");
    private JLabel ctrLabel = new JLabel("n/a");
    private JLabel cpcLabel = new JLabel("n/a");
    private JLabel cpmLabel = new JLabel("n/a");
    private JLabel cpaLabel = new JLabel("n/a");
    private JLabel bounceRateLabel = new JLabel("n/a");
    private ButtonGroup bg = new ButtonGroup();


    public void init() {
        this.setLayout(new GridLayout(12, 2, 0, 0));



        numOfImpressionLabel.setHorizontalAlignment(JLabel.CENTER);
        numOfClickLabel.setHorizontalAlignment(JLabel.CENTER);
        numOfUniqueLabel.setHorizontalAlignment(JLabel.CENTER);
        numOfBounceLabel.setHorizontalAlignment(JLabel.CENTER);
        numOfConversionLabel.setHorizontalAlignment(JLabel.CENTER);
        totalCostLabel.setHorizontalAlignment(JLabel.CENTER);
        ctrLabel.setHorizontalAlignment(JLabel.CENTER);
        cpcLabel.setHorizontalAlignment(JLabel.CENTER);
        cpmLabel.setHorizontalAlignment(JLabel.CENTER);
        cpaLabel.setHorizontalAlignment(JLabel.CENTER);
        bounceRateLabel.setHorizontalAlignment(JLabel.CENTER);


        numOfImpression = new JRadioButton("Number of Impressions");
        numOfImpression.addActionListener(e->{
            cp.setChart(cd.getChart(Metric.NUM_OF_IMPRESSIONS));
            repaint();
        });

        numOfClick = new JRadioButton("Number of Clicks");
        numOfClick.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.NUM_OF_CLICKS));
            repaint();
        });

        numOfUnique = new JRadioButton("Number of Unique Clicks");
        numOfUnique.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.NUM_OF_UNIQUE_CLICKS));
            repaint();
        });

        numOfBounce = new JRadioButton("Number of Bounces");
        numOfBounce.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.NUM_OF_BOUNCES));
            repaint();
        });

        numOfConversion = new JRadioButton("Number of Conversions");
        numOfConversion.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.NUM_OF_CONVERSIONS));
            repaint();
        });

        totalCost = new JRadioButton("Total Cost");
        totalCost.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.TOTAL_COST));
            repaint();
        });

        ctr = new JRadioButton("Click-Through-Rate");
        ctr.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.CTR));
            repaint();
        });

        cpc = new JRadioButton("Cost-per-Click");
        cpc.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.CPC));
            repaint();
        });

        cpm = new JRadioButton("Cost-per-Thousand Impressions");
        cpm.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.CPM));
            repaint();
        });

        cpa = new JRadioButton("Cost-per-Acquisition");
        cpa.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.CPA));
            repaint();
        });

        bounceRate = new JRadioButton("Bounce Rate");
        bounceRate.addActionListener(e->{

            cp.setChart(cd.getChart(Metric.BOUNCE_RATE));
            repaint();
        });

        numOfImpression.setEnabled(false);
        numOfClick.setEnabled(false);
        numOfBounce.setEnabled(false);
        numOfUnique.setEnabled(false);
        numOfConversion.setEnabled(false);
        totalCost.setEnabled(false);
        ctr.setEnabled(false);
        cpc.setEnabled(false);
        cpm.setEnabled(false);
        cpa.setEnabled(false);
        bounceRate.setEnabled(false);


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

        JLabel metric = new JLabel("Metric:");
        metric.setHorizontalAlignment(JLabel.CENTER);
        JLabel val = new JLabel("Overall Campaign Value:");
        val.setHorizontalAlignment(JLabel.CENTER);

        this.add(metric);
        this.add(val);
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
        numOfImpression.setEnabled(true);
        numOfClick.setEnabled(true);
        numOfBounce.setEnabled(true);
        numOfUnique.setEnabled(true);
        numOfConversion.setEnabled(true);
        totalCost.setEnabled(true);
        ctr.setEnabled(true);
        cpc.setEnabled(true);
        cpm.setEnabled(true);
        cpa.setEnabled(true);
        bounceRate.setEnabled(true);

        if(bg.getSelection()!=null) {
            bg.getSelection().setArmed(true);
            bg.getSelection().setPressed(true);
            bg.getSelection().setPressed(false);
            bg.getSelection().setArmed(false);
        }
        updateLabels();
    }
}
