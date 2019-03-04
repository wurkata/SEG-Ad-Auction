package model;

/**
 * Created by furqan on 04/03/2019.
 */
public class SubjectLog {
    private String gender;
    private int ageRange;
    private String income;


    public SubjectLog(String gender, int ageRange, String income){
        this.gender=gender;
        this.ageRange=ageRange;
        this.income=income;
    }

    public String getGender() {
        return gender;
    }

    public int getAgeRange() {
        return ageRange;
    }

    public String getIncome() {
        return income;
    }

}
