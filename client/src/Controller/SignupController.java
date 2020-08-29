package Controller;

import Model.Connection;
import Model.IO.User;
import Model.Main;
import Model.PageLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static Model.Main.connection;
import static Model.Main.user;

public class SignupController {

    @FXML
    public TextField userField, nameField, lastNameField, confirmVisibleField, passVisibleField;
    @FXML
    public PasswordField passField, confirmPassField;
    @FXML
    public Text userExisted, notSamePass, invalidPass, invalidUsername, notComplete;
    @FXML
    public ImageView showIcon, hideIcon;
    @FXML
    public DatePicker date;
    public static Connection client;

    public void clickedField(MouseEvent mouseEvent) {
        userExisted.setVisible(false);
        notSamePass.setVisible(false);
        invalidPass.setVisible(false);
        invalidUsername.setVisible(false);
        notComplete.setVisible(false);
    }

    public void informatinPage(ActionEvent actionEvent) throws IOException {
        Main.upStop = true;
        boolean valid = true;
        String pass, confirmPass;
        if (passField.isVisible()) {
            pass = passField.getText();
            confirmPass = confirmPassField.getText();
        } else {
            pass = passVisibleField.getText();
            confirmPass = confirmVisibleField.getText();
        }
        if (pass.length() == 0 || confirmPass.length() == 0 || nameField.getText().length() == 0 || lastNameField.getText().length() == 0 || userField.getText().length() == 0 || date.getValue() == null) {
            notComplete.setVisible(true);
            valid = false;
        } else {
            if (userField.getText().matches("[^A-Za-z0-9.]")) {
                invalidUsername.setVisible(true);//valid=false;
            }
            if (pass.length() < 8 && pass.length() >= 1) {
                invalidPass.setVisible(true);
                valid = false;
            } else if (!pass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$")) {
                invalidPass.setVisible(true);
                valid = false;
            } else if (!pass.equals(confirmPass)) {
                notSamePass.setVisible(true);
                valid = false;
            }
        }
        if (valid) {
            client = new Connection(userField.getText(), "checkUserUp");
            connection=client;
            String a = client.getRespond();
            if (a.equals("userExist")) {
                userExisted.setVisible(true);
            } else if (a.equals("ok")) {
                user = new User(userField.getText(), pass, nameField.getText(), lastNameField.getText(), date.getValue().toString(), client.objectInputStream, client.objectOutputStream);
                Path path = Paths.get(user.getUsername());
                File file = new File("\\" + user.getUsername() + "\\" + user.getImageName());
                Files.createDirectory(path);
                ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream(user.getUsername() + "\\" + user.getImageName() + ".ser"));
                obj.writeObject(user);
                new PageLoader().Load("/View/informationPage.fxml");
            }
        }
    }

    public void hidePass(MouseEvent mouseEvent) {
        showIcon.setVisible(false);
        hideIcon.setVisible(true);
        passField.setText(passVisibleField.getText());
        passField.setVisible(true);
        passVisibleField.setVisible(false);
        confirmPassField.setText(confirmVisibleField.getText());
        confirmPassField.setVisible(false);
        confirmVisibleField.setVisible(false);
        confirmPassField.setVisible(true);
    }

    public void showPass(MouseEvent mouseEvent) {
        showIcon.setVisible(true);
        hideIcon.setVisible(false);
        passVisibleField.setText(passField.getText());
        passField.setVisible(false);
        passVisibleField.setVisible(true);
        confirmVisibleField.setText(confirmPassField.getText());
        confirmPassField.setVisible(false);
        confirmVisibleField.setVisible(true);
    }

    public void signinPage(MouseEvent mouseEvent) throws IOException {
        new PageLoader().Load("/View/signinPage.fxml");
    }

}
