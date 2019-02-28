import java.util.Date;

/**
 * Created by furqan on 26/02/2019.
 */
public class ImpressionLog {
    private long subjectID;
    private String gender;
    private String ageRange;
    private String income;
    private String context;
    private Date impressionDate;
    private double impressionCost;



    public ImpressionLog(Date impressionDate, long subjectID, String gender, String ageRange, String income, String context, double impressionCost){
        this.impressionDate=impressionDate;
        this.subjectID=subjectID;
        this.gender=gender;
        this.ageRange=ageRange;
        this.income=income;
        this.context=context;
        this.impressionCost=impressionCost;
    }

    public long getSubjectID() {
        return subjectID;
    }

    public String getGender() {
        return gender;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public String getIncome() {
        return income;
    }

    public Date getImpressionDate() {
        return impressionDate;
    }

    public double getImpressionCost() {
        return impressionCost;
    }

    public String getContext() {
        return context;
    }
}
