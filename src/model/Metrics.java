package model;

import common.Metric;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Metrics {

    private StringProperty noImpressions = new SimpleStringProperty();
    private StringProperty noClicks = new SimpleStringProperty();
    private StringProperty noUniqueClicks = new SimpleStringProperty();
    private StringProperty noConversions = new SimpleStringProperty();
    private StringProperty noBounces = new SimpleStringProperty();
    private StringProperty bounceRate = new SimpleStringProperty();
    private StringProperty totalCost = new SimpleStringProperty();
    private StringProperty CTR = new SimpleStringProperty();
    private StringProperty CPC = new SimpleStringProperty();
    private StringProperty CPM = new SimpleStringProperty();
    private StringProperty CPA = new SimpleStringProperty();

    public StringProperty MetricsProperty(Metric metric) {
        switch (metric) {
            case NUM_OF_IMPRESSIONS:
                return noImpressions;
            case NUM_OF_CLICKS:
                return noClicks;
            case NUM_OF_UNIQUE_CLICKS:
                return noUniqueClicks;
            case NUM_OF_CONVERSIONS:
                return noConversions;
            case NUM_OF_BOUNCES:
                return noBounces;
            case BOUNCE_RATE:
                return bounceRate;
            case TOTAL_COST:
                return totalCost;
            case CTR:
                return CTR;
            case CPC:
                return CPC;
            case CPM:
                return CPM;
            case CPA:
                return CPA;
            default:
                return null;
        }
    }

    public void setNoImpressions(String noImpressions) {
        this.noImpressions.set(noImpressions);
    }

    public void setNoClicks(String noClicks) {
        this.noClicks.set(noClicks);
    }

    public void setNoUniqueClicks(String noUniqueClicks) {
        this.noUniqueClicks.set(noUniqueClicks);
    }

    public void setNoConversions(String noConversions) {
        this.noConversions.set(noConversions);
    }

    public void setNoBounces(String noBounces) {
        this.noBounces.set(noBounces);
    }

    public void setBounceRate(String bounceRate) {
        this.bounceRate.set(bounceRate);
    }

    public void setTotalCost(String totalCost) {
        this.totalCost.set(totalCost);
    }

    public void setCTR(String CTR) {
        this.CTR.set(CTR);
    }

    public void setCPC(String CPC) {
        this.CPC.set(CPC);
    }

    public void setCPM(String CPM) {
        this.CPM.set(CPM);
    }

    public void setCPA(String CPA) {
        this.CPA.set(CPA);
    }
}
