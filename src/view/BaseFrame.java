package view;

import controller.AuctionController;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import java.awt.*;

import javax.swing.*;

/**
 * @author Zeno
 * @Date 02/03/2019
 * This is the basic layout for the UI
 */
public class BaseFrame extends JFrame {

    private ChartPanel chartPanel;
    private KeyMetrics chartMetrics;
    private Header header;
    private ChartControl chartControl;
    private AuctionController controller;
    private JPanel chartHolder;

    public BaseFrame(AuctionController controller) {
        this.controller = controller;

        setTitle("Ad-Auction-Dashboard");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setSize(1600, 900);

        chartPanel = new ChartPanel(null);
        chartMetrics = new KeyMetrics(controller, chartPanel);
        header = new Header();
        chartControl = new ChartControl();

        chartHolder = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        chartHolder.add(chartPanel, gbc);
        gbc.weighty = 0.1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10,60,10,10);
        chartHolder.add(chartMetrics, gbc);
    }

    public void initUI() {
        chartMetrics.setPreferredSize(new Dimension(300, 100));
        chartMetrics.setAlignmentX(RIGHT_ALIGNMENT);


        add(chartHolder, BorderLayout.CENTER);
        // add(chartMetrics, BorderLayout.SOUTH);
        add(chartControl.displayChartControls(controller), BorderLayout.WEST);
        add(header.displayHeader(controller), BorderLayout.NORTH);
        setVisible(true);
    }
}
