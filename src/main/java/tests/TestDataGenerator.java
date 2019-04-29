package tests;

import org.jfree.data.time.Hour;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by furqan on 24/04/2019.
 */

public class TestDataGenerator {
    //--------------------------------Change these values to change amount of data generated---------------------------
    private static int minImpressionsPerHour = 5;
    private static int maxImpressionsPerHour=100;
    private static int numOfDays = 365*2;
    //--------------------------------------------------------------------------------------------------------------

    //Files generated into "input" folder with other logs
    public static void main(String[] args){
        Random rng = new Random();

        ArrayList<String> impLogs = new ArrayList<>();
        ArrayList<String> clickLogs = new ArrayList<>();
        ArrayList<String> serverLogs = new ArrayList<>();

        String[] genders = {"Male", "Female"};
        String[] ages = {"<25", "25-34", "35-44", "45-54", ">54"};
        String[] incomes = {"Low", "Medium", "High"};
        String[] contexts = {"News", "Shopping", "Social", "Media", "Blog", "Hobbies", "Travel"};

        int maxID = 0;

        Hour h = new Hour(new Date(119, 0, 1));
        for(int i=0; i<numOfDays; i++){

            for(int j=0; j<24; j++){
                for(int k=0; k<minImpressionsPerHour+rng.nextInt(maxImpressionsPerHour-minImpressionsPerHour+1); k++) {
                    String entry = "";
                    String hour = hourToString(h);
                    entry += hour;
                    entry += ",";

                    maxID++;
                    entry += maxID;
                    entry += ",";

                    entry += genders[rng.nextInt(2)];
                    entry += ",";

                    entry += ages[rng.nextInt(5)];
                    entry += ",";

                    entry += incomes[rng.nextInt(3)];
                    entry += ",";

                    entry += contexts[rng.nextInt(7)];
                    entry += ",";

                    entry += rng.nextDouble() * 15.0;

                    impLogs.add(entry);

                    if(rng.nextInt(2)==1){
                        //create click and server logs
                        String clickEntry = "";

                        clickEntry+=hour;
                        clickEntry+=",";

                        clickEntry+=maxID;
                        clickEntry+=",";

                        clickEntry+=rng.nextDouble()*20.0;

                        clickLogs.add(clickEntry);


                        String serverEntry = "";

                        serverEntry+=hour;
                        serverEntry+=",";

                        serverEntry+=maxID;
                        serverEntry+=",";

                        Hour exit= (Hour) h.next();
                        for(int l=0; l<rng.nextInt(10); l++){
                            exit=(Hour)exit.next();
                        }
                        serverEntry+=hourToString(exit);
                        serverEntry+=",";

                        serverEntry+=rng.nextInt(999);
                        serverEntry+=",";

                        serverEntry+=rng.nextInt(2)==1?"Yes":"No";

                        serverLogs.add(serverEntry);

                    }
                }
                h=(Hour)h.next();
            }
        }


        writeOutput(impLogs, "genImpressionLog"+minImpressionsPerHour+"-"+maxImpressionsPerHour+"-"+numOfDays+"Days.csv", "Date,ID,Gender,Age,Income,Context,Impression Cost\n");
        writeOutput(clickLogs, "genClickLog"+minImpressionsPerHour+"-"+maxImpressionsPerHour+"-"+numOfDays+"Days.csv", "Date,ID,Click Cost\n");
        writeOutput(serverLogs, "genServerLog"+minImpressionsPerHour+"-"+maxImpressionsPerHour+"-"+numOfDays+"Days.csv", "Entry Date,ID,Exit Date,Pages Viewed,Conversion\n");

    }

    private static void writeOutput(ArrayList<String> arrayList, String filename, String header){
        File out = new File("input/"+filename);
        File dir = new File("input");
        dir.mkdir();

        if(!out.exists()){
            try {
                out.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(out));
            writer.write(header);
            writer.flush();
            for (String s : arrayList) {
                writer.write(s);
                writer.write("\n");
                writer.flush();
            }
        }catch (Exception e){

        }
    }

    private static String hourToString(Hour h){
        String hString = h.toString();
        hString = hString.substring(1, hString.length()-1);
        String[] hStringArr = hString.split(",");

        String[] dateArr = hStringArr[1].split("/");

        Random random = new Random();

        return String.format("%s-%02d-%02d %02d:%02d:%02d", Integer.parseInt(dateArr[2]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[0]), Integer.parseInt(hStringArr[0]), random.nextInt(60), random.nextInt(60));
    }
}
