package model;

/**
 * Created by furqan on 04/03/2019.
 */
public class Subject {
    private String id;
    private String gender;
    private String age;
    private String income;


    public Subject(String id, String gender, String age, String income) {
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.income = income;
    }

    public String getId() { return id; }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getIncome() {
        return income;
    }

}
