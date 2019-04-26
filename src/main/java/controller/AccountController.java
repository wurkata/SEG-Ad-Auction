package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import model.DBTasks.CheckAccount;
import model.DBTasks.Insert;
import model.Model;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AccountController implements Initializable {
    @FXML
    JFXTextField usernameField;

    @FXML
    JFXPasswordField passwordField;

    @FXML
    JFXButton signBtn;

    @FXML
    JFXButton goToBtn;

    @FXML
    Label feedbackMsg;

    @FXML
    ProgressIndicator signProgress;

    @FXML
    Label STATE;

    private Model model;
    private String _STATE;
    private boolean isValidInputPwd;
    private boolean isValidInputUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new Model();

        signProgress.setVisible(false);

        _STATE = STATE.textProperty().getValue();
        isValidInputUser = false;
        isValidInputPwd = false;

        feedbackMsg.textProperty().setValue("");

        usernameField.textProperty().addListener(((observable, oldValue, newValue) -> {
            isValidInputUser = newValue.length() > 2;
            signBtn.setDisable(!(isValidInputUser && isValidInputPwd));
        }));

        passwordField.textProperty().addListener(((observable, oldValue, newValue) -> {
            isValidInputPwd = newValue.length() > 2;
            signBtn.setDisable(!(isValidInputUser && isValidInputPwd));
        }));

        signBtn.setOnMouseReleased(e -> {
            signProgress.setVisible(true);
            signBtn.setDisable(true);

            if (_STATE.equals("Login")) {
                CheckAccount checkAccount = new CheckAccount();
                checkAccount.setOnSucceeded(a -> {
                    boolean res = checkAccount.getValue();

                    if (res) {
                        signProgress.setVisible(false);
                        feedbackMsg.textProperty().setValue("Login successful. Taking you to Dashboard...");
                        model.setClient(usernameField.textProperty().getValue());

                        try {
                            Thread.sleep(1000);
                            goTo("dashboard");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        feedbackMsg.textProperty().setValue("Wrong username or password.");
                        signProgress.setVisible(false);
                        signBtn.setDisable(false);
                    }
                });
                checkAccount.setUser(usernameField.textProperty().getValue());
                checkAccount.setPassword(passwordField.textProperty().getValue());
                new Thread(checkAccount).start();
            } else if (_STATE.equals("Register")) {
                Map<String, String> params = new HashMap<>();
                params.put("username", usernameField.textProperty().getValue());
                params.put("password", passwordField.textProperty().getValue());

                Insert insertTask = new Insert();
                insertTask.setACTION("ACCOUNT");
                insertTask.setParams(params);

                insertTask.setOnSucceeded(a -> {
                    boolean res = insertTask.getValue();

                    if (res) {
                        signProgress.setVisible(false);

                        feedbackMsg.textProperty().setValue("Successfully registered. Taking you to Login...");

                        try {
                            Thread.sleep(1000);
                            goTo("login");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        feedbackMsg.textProperty().setValue("Username is taken.");
                        // feedbackMsg.textProperty().setValue("Error in Database. Contact System Administrator!");
                        signProgress.setVisible(false);
                        signBtn.setDisable(false);
                    }
                });

                insertTask.setOnFailed(a -> {
                    feedbackMsg.textProperty().setValue("Error in Database. Contact System Administrator!");
                    signProgress.setVisible(false);
                    signBtn.setDisable(false);
                });

                new Thread(insertTask).start();
            }
        });

        goToBtn.setOnMouseReleased(e -> {
            try {
                if (_STATE.equals("Login"))
                    goTo("register");
                else
                    goTo("login");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void goTo(String scene) throws IOException {
        DashboardController controller = new DashboardController(model);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + scene + ".fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        Scene campaignsScene = new Scene(root);

        Stage window = (Stage) goToBtn.getScene().getWindow();

        window.setScene(campaignsScene);
        window.setResizable(false);
        window.show();
    }
}
