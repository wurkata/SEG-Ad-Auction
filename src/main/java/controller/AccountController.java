package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import model.DBTasks.CheckAccount;
import model.DBTasks.Insert;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AccountController extends GlobalController implements Initializable {
    public static boolean online = true;

    @FXML
    JFXTextField usernameField;

    @FXML
    JFXPasswordField passwordField;

    @FXML
    private JFXButton useOfflineBtn;

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

    private String _STATE;
    private boolean isValidInputPwd;
    private boolean isValidInputUser;

    public static User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        signProgress.setVisible(false);

        _STATE = STATE.textProperty().getValue();
        isValidInputUser = false;
        isValidInputPwd = false;

        feedbackMsg.textProperty().setValue("");

        if (_STATE.equals("Login"))
            useOfflineBtn.setOnMouseReleased(e -> {
                try {
                    online = false;
                    goTo("dashboard", (Stage) useOfflineBtn.getScene().getWindow(), new DashboardController(null));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });

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
                    User res = checkAccount.getValue();

                    if (res != null) {
                        signProgress.setVisible(false);
                        feedbackMsg.textProperty().setValue("Login successful. Taking you to Dashboard...");
                        user = res;

                        try {
                            Thread.sleep(1000);
                            goTo("dashboard", (Stage) signBtn.getScene().getWindow(), new DashboardController(user));
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
                params.put("pwd", passwordField.textProperty().getValue());

                Insert insertTask = new Insert();
                insertTask.setTable("users");
                insertTask.setParams(params);

                insertTask.setOnSucceeded(a -> {
                    boolean res = insertTask.getValue();

                    if (res) {
                        signProgress.setVisible(false);

                        feedbackMsg.textProperty().setValue("Successfully registered. Taking you to Login...");

                        try {
                            Thread.sleep(1000);
                            goTo("login", (Stage) signBtn.getScene().getWindow(), null);
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
                    goTo("register", (Stage) goToBtn.getScene().getWindow(), null);
                else
                    goTo("login", (Stage) goToBtn.getScene().getWindow(), null);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }
}
