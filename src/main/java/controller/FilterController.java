package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import common.Filters.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import model.Model;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by furqan on 18/03/2019.
 */
public class FilterController implements Initializable {
    private Model model;
    private CampaignController controller;

    @FXML
    private JFXComboBox<String> filterTypeBox;

    @FXML
    private JFXComboBox<String> ageBox;

    @FXML
    private JFXComboBox<String> genderBox;

    @FXML
    private JFXComboBox<String> contextBox;

    @FXML
    private JFXComboBox<String> incomeBox;

    @FXML
    private VBox dateBox;

    @FXML
    private JFXDatePicker beforeDate;

    @FXML
    private JFXDatePicker afterDate;

    @FXML
    private JFXButton submitFilterButton;

    FilterController(Model model, CampaignController c){
        this.model=model;
        this.controller=c;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterTypeBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                ageBox.setVisible(false);
                genderBox.setVisible(false);
                contextBox.setVisible(false);
                incomeBox.setVisible(false);
                dateBox.setVisible(false);
                System.out.println(newValue.toString());
                switch(newValue){
                    case "Age":
                        ageBox.setVisible(true);
                        break;
                    case "Gender":
                        genderBox.setVisible(true);
                        break;
                    case "Context":
                        contextBox.setVisible(true);
                        break;
                    case "Income":
                        incomeBox.setVisible(true);
                        break;
                    case "Date Range":
                        dateBox.setVisible(true);
                        break;
                }
            });

        submitFilterButton.setOnMouseClicked(event -> {
            int id = controller.getUsableID(model);
            switch(filterTypeBox.getSelectionModel().getSelectedItem()){
                case "Age":
                    System.out.println(ageBox.getSelectionModel().getSelectedItem()+" "+ageBox.getSelectionModel().getSelectedIndex());
                    AgeFilter af = new AgeFilter(ageBox.getSelectionModel().getSelectedItem().equals("Under 25")?"<25":ageBox.getSelectionModel().getSelectedItem(), model.getSubjects());
                    af.setFilterName("Age Filter - "+ageBox.getSelectionModel().getSelectedItem());
                    model.addFilter(af, id);
//                    controller.addFilter(id+": "+af.getFilterName());

                    break;
                case "Gender":
                    System.out.println(genderBox.getSelectionModel().getSelectedItem());
                    GenderFilter gf = new GenderFilter(genderBox.getSelectionModel().getSelectedItem(), model.getSubjects());
                    gf.setFilterName("Gender Filter - "+genderBox.getSelectionModel().getSelectedItem());
                    model.addFilter(gf, id);
//                    controller.addFilter(id+": "+gf.getFilterName());
                    break;
                case "Context":
                    System.out.println(contextBox.getSelectionModel().getSelectedItem());
                    ContextFilter cf = new ContextFilter(contextBox.getSelectionModel().getSelectedItem());
                    cf.setFilterName("Context Filter - "+contextBox.getSelectionModel().getSelectedItem());
                    model.addFilter(cf, id);
//                    controller.addFilter(id+": "+cf.getFilterName());
                    break;
                case "Income":
                    System.out.println(incomeBox.getSelectionModel().getSelectedItem());
                    IncomeFilter iF = new IncomeFilter(incomeBox.getSelectionModel().getSelectedItem(), model.getSubjects());
                    iF.setFilterName("Income Filter - "+incomeBox.getSelectionModel().getSelectedItem());
                    model.addFilter(iF, id);
//                    controller.addFilter(id+": "+iF.getFilterName());
                    break;
                case "Date Range":
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    System.out.println(beforeDate.getValue());
                    Date bDate = null;
                    Date aDate=null;
                    try {
                        bDate = df.parse(beforeDate.getValue().toString());
                        aDate = df.parse(afterDate.getValue().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(bDate == null && aDate == null){
                        return;
                    }else if (bDate==null){
                        DateFilter dateFilter = new DateFilter(aDate, new Date(Long.MAX_VALUE));
                        dateFilter.setFilterName("Date Filter - After "+aDate.toString());
                        model.addFilter(dateFilter, id);
//                        controller.addFilter(id+": "+dateFilter.getFilterName());
                    }else if (aDate==null){
                        DateFilter dateFilter = new DateFilter(new Date(0), bDate);
                        dateFilter.setFilterName("Date Filter - Before "+bDate.toString());
                        model.addFilter(dateFilter, id);
//                        controller.addFilter(id+": "+dateFilter.getFilterName());
                    }else{
                        DateFilter dateFilter = new DateFilter(aDate, bDate);
                        dateFilter.setFilterName("Date Filter - "+aDate.toString()+" to "+bDate.toString());
                        model.addFilter(dateFilter, id);
//                        controller.addFilter(id+": "+dateFilter.getFilterName());
                    }
                    break;
            }
        });

    }

}
