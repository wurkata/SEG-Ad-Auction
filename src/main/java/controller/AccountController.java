package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import model.DBTasks.CheckAccount;

import java.net.URL;
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
    Label STATE;

    private String _STATE;
    private boolean isValidInputPwd;
    private boolean isValidInputUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _STATE = STATE.textProperty().toString();
        isValidInputUser = false;
        isValidInputPwd = false;

        signBtn.setOnMouseReleased(e -> {
            switch (_STATE) {
                case "Login":
                    CheckAccount checkAccount = new CheckAccount();
                    checkAccount.setUser(usernameField.textProperty().toString());
                    checkAccount.setPassword(passwordField.textProperty().toString());
                    new Thread(checkAccount).start();
                    break;
                case "Register":
                    // TODO: Load register.fxml;
                    break;
                default:
                    System.err.println("Got lost in the Space-Time Continuum.");
                    break;
            }
        });

        usernameField.textProperty().addListener(((observable, oldValue, newValue) -> {
            CheckAccount checkAccount = new CheckAccount();
            checkAccount.setOnSucceeded( -> {
                boolean res = checkAccount.getValue();

                signBtn.setDisable(!res);
                isValidInputUser = res;

                if(res && isValidInputPwd) signBtn.setDisable(false);
            });

            if(newValue.length() > 2) {
                checkAccount.setUser(newValue);
            } else {
                isValidInputUser = false;
                signBtn.setDisable(true);
            }

            new Thread(checkAccount).start();
        }));

        passwordField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue.length() > 2) {
                isValidInputPwd = true;

                if(isValidInputUser) signBtn.setDisable(false);
            } else {
                isValidInputPwd = false;
                signBtn.setDisable(true);
            }
        }));
    }
}
