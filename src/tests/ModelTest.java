package tests;

import controller.AuctionController;
import controller.FXController;
import model.Model;
import org.junit.*;

import java.io.File;

import static org.junit.Assert.*;

public class ModelTest {
    private Model myModel;

    public ModelTest() {
        try {
            myModel = new Model(
                    new File("input/impression_log.csv"),
                    new File("input/click_log.csv"),
                    new File("input/server_log.csv")
            );
        } catch (Exception e) {
            System.out.println("Input load failed: " + e);
        }
    }

    @Test
    public void clickTotalCostTest() {
        myModel.setCostMode(false);
        assertEquals(117610.8657, myModel.getTotalCost(), 1);
    }

    @Test
    public void impressionTotalCostTest() {
        myModel.setCostMode(true);
        assertEquals(487.055498, myModel.getTotalCost(), 1);
    }

    @Test
    public void costPerClickTest() {
        myModel.setCostMode(false);
        assertEquals(5, myModel.getClickCost(), 0.1);
    }

    @Test
    public void cpmTest() {
        myModel.setCostMode(true);
        assertEquals(1, myModel.getCPM(), 0.1);
    }

    @Test
    public void ctrTest() {
        assertEquals(0.05, myModel.getCTR(), 0.001);
    }

    @Test
    public void cpaTest() {
        myModel.setCostMode(true);
        assertEquals(0.24, myModel.getCPA(), 0.001);
        myModel.setCostMode(false);
        assertEquals(58, myModel.getCPA(), 0.1);
    }

    @Test
    public void impressionNumTest() {
        assertEquals(486104, myModel.getNumOfImpressions(), 0);
    }

    @Test
    public void clickNumTest() {
        assertEquals(23923, myModel.getNumOfClicks(), 0);
    }

    @Test
    public void uniqueClickNumTest() {
        assertEquals(23806, myModel.getNumOfUniqueClicks(), 0);
    }

    @Test
    public void bounceNumTest() {
        assertEquals(21897, myModel.getNumOfBounces(), 0);
    }

    @Test
    public void bounceRateTest() {
        assertEquals(0.91, myModel.getBounceRate(), 0.01);
    }

    @Test
    public void conversionNumTest() {
        assertEquals(2026, myModel.getNumOfConversions(), 0);
    }
}