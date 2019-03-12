package view;

import common.Metric;
import common.Observer;
import controller.AuctionController;
import org.jfree.chart.ChartPanel;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class KeyMetrics extends JPanel implements Observer {

    /**
     * @Author Zeno
     * @Date 02/03/2019
     */
    //private static final long serialVersionUID = 6340049980224403816L;
    private Font titleFont = new Font("Comic Sans Ms", Font.BOLD + Font.ITALIC, 12);
    private DecimalFormat df = new DecimalFormat("#.####");

    private AuctionController controller;
    private ChartPanel cp;
    private ChartDisplay cd;

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

    GridBagConstraints constraints = new GridBagConstraints();

    public KeyMetrics(AuctionController controller, ChartPanel cp) {
        controller.addObserver(this);

        this.controller = controller;
        this.cp = cp;
        cd = new ChartDisplay(controller);

        init();
    }

    public void init() {
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Metrics", TitledBorder.LEFT, TitledBorder.TOP,
                titleFont,
                new Color(100, 100, 100)
        ));
        this.setBackground(new Color(255, 251, 209));
        constraints.fill = GridBagConstraints.BOTH;

        numOfImpression = new JRadioButton("Number of Impressions");
        numOfImpression.addActionListener(e -> {
            cp.setChart(cd.getChart(Metric.NUM_OF_IMPRESSIONS));
            repaint();
        });

        numOfClick = new JRadioButton("Number of Clicks");
        numOfClick.addActionListener(e -> {
            cp.setChart(cd.getChart(Metric.NUM_OF_CLICKS));
            repaint();
        });

        numOfUnique = new JRadioButton("Number of Unique Clicks");
        numOfUnique.addActionListener(e -> {
            cp.setChart(cd.getChart(Metric.NUM_OF_UNIQUE_CLICKS));
            repaint();
        });

        numOfBounce = new JRadioButton("Number of Bounces");
        numOfBounce.addActionListener(e -> {
            cp.setChart(cd.getChart(Metric.NUM_OF_BOUNCES));
            repaint();
        });

        numOfConversion = new JRadioButton("Number of Conversions");
        numOfConversion.addActionListener(e -> {
            cp.setChart(cd.getChart(Metric.NUM_OF_CONVERSIONS));
            repaint();
        });

        totalCost = new JRadioButton("Total Cost");
        totalCost.addActionListener(e -> {
            cp.setChart(cd.getChart(Metric.TOTAL_COST));
            repaint();
        });

        ctr = new JRadioButton("Click-Through-Rate");
        ctr.addActionListener(e -> {
            cp.setChart(cd.getChart(Metric.CTR));
            repaint();
        });

        cpc = new JRadioButton("Cost-per-Click");
        cpc.addActionListener(e -> {
            cp.setChart(cd.getChart(Metric.CPC));
            repaint();
        });

        cpm = new JRadioButton("Cost-per-Thousand Impressions");
        cpm.addActionListener(e -> {
            cp.setChart(cd.getChart(Metric.CPM));
            repaint();
        });

        cpa = new JRadioButton("Cost-per-Acquisition");
        cpa.addActionListener(e -> {
            cp.setChart(cd.getChart(Metric.CPA));
            repaint();
        });

        bounceRate = new JRadioButton("Bounce Rate");
        bounceRate.addActionListener(e -> {
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

        addComponent(numOfImpression, 0, 0);
        addComponent(numOfImpressionLabel, 1, 0);
        addComponent(numOfClick, 0, 1);
        addComponent(numOfClickLabel, 1, 1);
        addComponent(numOfBounce, 0, 2);
        addComponent(numOfBounceLabel, 1, 2);
        addComponent(numOfUnique, 0, 3);
        addComponent(numOfUniqueLabel, 1, 3);
        addComponent(numOfConversion, 2, 0);
        addComponent(numOfConversionLabel, 3, 0);
        addComponent(totalCost, 2, 1);
        addComponent(totalCostLabel, 3, 1);
        addComponent(ctr, 2, 2);
        addComponent(ctrLabel, 3, 2);
        addComponent(cpc, 2, 3);
        addComponent(cpcLabel, 3, 3);
        addComponent(cpm, 4, 0);
        addComponent(cpmLabel, 5, 0);
        addComponent(cpa, 4, 1);
        addComponent(cpaLabel, 5, 1);
        addComponent(bounceRate, 4, 2);
        addComponent(bounceRateLabel, 5, 2);
    }

    public void updateLabels() {
        numOfImpressionLabel.setText(df.format(controller.getNumOfImpressions()));
        numOfClickLabel.setText(df.format(controller.getNumOfClicks()));
        numOfUniqueLabel.setText(df.format(controller.getNumOfUniqueClicks()));
        numOfBounceLabel.setText(df.format(controller.getNumOfBounces()));
        numOfConversionLabel.setText(df.format(controller.getNumOfConversions()));
        totalCostLabel.setText(df.format(controller.getTotalCost()) + " pence");
        ctrLabel.setText(df.format(controller.getCTR()));
        cpcLabel.setText(df.format(controller.getClickCost()) + " pence");
        cpmLabel.setText(df.format(controller.getCPM()) + " pence");
        cpaLabel.setText(df.format(controller.getCPA()) + " pence");
        bounceRateLabel.setText(df.format(controller.getBounceRate()));
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

        if (bg.getSelection() != null) {
            bg.getSelection().setArmed(true);
            bg.getSelection().setPressed(true);
            bg.getSelection().setPressed(false);
            bg.getSelection().setArmed(false);
        }
        updateLabels();
    }

    private void addComponent(JComponent comp, int x, int y) {
        if (x % 2 == 1) {
            constraints.insets = new Insets(5, 5, 5, 40);
        } else {
            constraints.insets = new Insets(5, 5, 5, 5);
            comp.setFont(titleFont);
            comp.setForeground(new Color(80,80,80));
        }

        constraints.gridx = x;
        constraints.gridy = y;
        this.add(comp, constraints);
    }
}
