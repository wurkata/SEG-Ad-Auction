package view;

import org.jfree.chart.ChartPanel;

import java.awt.BorderLayout;

import javax.swing.*;

/**
 * @author Zeno
 * @Date 02/03/2019
 * This is the basic layout for the UI
 */
public class BaseFrame extends JFrame {

    private KeyMetrics km = new KeyMetrics();
    private Header header = new Header();
    private ChartControl chart = new ChartControl();

    public BaseFrame() {

        setTitle("Ad-Auction-Dashboard");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setSize(1600, 900);

    }

    public void initUI() {
        add(header.displayHeader(), BorderLayout.NORTH);
        add(km.displayKeyMetrics(), BorderLayout.WEST);
        add(chart.displayChartControls(), BorderLayout.SOUTH);

        ChartPanel cp = new ChartPanel((new ChartDisplay()).getChart());

        add(cp, BorderLayout.CENTER);
        setVisible(true);
    }
}
