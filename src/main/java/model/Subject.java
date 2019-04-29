package model;

/**
 * Created by furqan on 04/03/2019.
 */
public class Subject {
    private String gender;
    private String age;
    private String income;


    public Subject(String gender, String age, String income) {
        this.gender = gender;
        this.age = age;
        this.income = income;
    }

    public String getGender() {
        return gender;
    }

    public String getAgeRange() {
        return age;
    }

    public String getIncome() {
        return income;
    }

}
