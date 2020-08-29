package Controller;

import Model.IO.Message;
import Model.IO.MessageType;
import Model.Main;
import Model.PageLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;

import static Model.Main.connection;
import static Model.Main.user;

public class changInfoController {
    @FXML
    public TextField nameField, lastNameField, oldVisPass, passVisibleField, oldPass, passField, phoneField;

    @FXML
    public Text invalid, incorrect, changed;

    public void clickedField(MouseEvent mouseEvent) {
        invalid.setVisible(false);
        incorrect.setVisible(false);

    }

    public void hidePass(MouseEvent mouseEvent) {
        passVisibleField.setVisible(false);
        oldVisPass.setVisible(false);
        passField.setVisible(true);
        oldPass.setVisible(true);
        oldPass.setText(oldVisPass.getText());
        passField.setText(passVisibleField.getText());
    }

    public void showPass(MouseEvent mouseEvent) {
        passField.setVisible(false);
        passVisibleField.setVisible(true);
        oldPass.setVisible(false);
        oldVisPass.setVisible(true);
        passVisibleField.setText(passField.getText());
        oldVisPass.setText(oldPass.getText());
    }

    public void changInfo(ActionEvent actionEvent) throws IOException {
        System.out.println(user.getPassword());
        boolean sucsseed = true;
        if (!oldPass.getText().isEmpty() && oldPass.isVisible()) {
            if (!oldPass.getText().equals(user.getPassword())) {
                incorrect.setVisible(true);
                sucsseed=false;
            } else if (!passField.getText().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$")) {
                invalid.setVisible(true);
                sucsseed = false;
            } else {
                user.setPassword(passField.getText());
            }
            System.out.println("hey");
        }
        if (!oldVisPass.getText().isEmpty() && oldVisPass.isVisible()) {
            if (!oldVisPass.getText().equals(user.getPassword())) {
                incorrect.setVisible(true);
                sucsseed=false;
            } else if (!passVisibleField.getText().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$")) {
                invalid.setVisible(true);
                sucsseed = false;
            } else {
                user.setPassword(passVisibleField.getText());
            }
        }
        if (!nameField.getText().isEmpty())
            user.setName(nameField.getText());
        if (!lastNameField.getText().isEmpty())
            user.setLastName(lastNameField.getText());
        if (!phoneField.getText().isEmpty())
            user.setPhoneNumber(phoneField.getText());
        if (sucsseed) {
            connection.sendRequest(new Message(MessageType.change, user.getUsername(), "", ""));
            connection.objectOutputStream.writeObject(user);
            connection.objectOutputStream.flush();
            changed.setVisible(true);
        }
    }


    public void changePro(ActionEvent actionEvent) {
    }

    public void backMain(ActionEvent actionEvent) throws IOException {
        new PageLoader().Load("/View/mainPage.fxml");
    }

    public void setImage(ActionEvent actionEvent) {
    }

    public void backtoCh(ActionEvent actionEvent) {
    }
}
