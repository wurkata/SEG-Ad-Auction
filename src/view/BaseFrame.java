package view;

import controller.AuctionController;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import java.awt.BorderLayout;

import javax.swing.*;

/**
 * @author Zeno
 * @Date 02/03/2019
 * This is the basic layout for the UI
 */
public class BaseFrame extends JFrame {

    private KeyMetrics km;
    private Header header = new Header();
    private ChartControl chart = new ChartControl();
    private AuctionController controller;

    public BaseFrame(AuctionController controller) {
        this.controller=controller;

        setTitle("Ad-Auction-Dashboard");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setSize(1600, 900);

    }

    public void initUI() {
        add(header.displayHeader(controller), BorderLayout.NORTH);
        ChartPanel cp = new ChartPanel(null);

        km=new KeyMetrics(controller, cp);
        add(km, BorderLayout.WEST);
        add(chart.displayChartControls(controller), BorderLayout.SOUTH);

        add(cp, BorderLayout.CENTER);
        setVisible(true);
    }
}
