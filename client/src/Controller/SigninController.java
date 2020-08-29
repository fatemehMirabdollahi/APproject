package Controller;

import Model.Connection;
import Model.Main;
import Model.PageLoader;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.IOException;

public class SigninController {
    @FXML
    public TextField userField, passVisibleField;
    @FXML
    public Text wrongPass, notComplete;
    @FXML
    public ImageView showIcon, hideIcon;
    @FXML
    public PasswordField passField;
    @FXML
    public StackPane processPane;
    public static Connection client;

    //    public static void stop() throws IOException {
//        client.objectInputStream.close();
//        client.objectOutputStream.close();
//        client.client.close();
//    }
    public void clickedField(MouseEvent mouseEvent) {
        wrongPass.setVisible(false);
        notComplete.setVisible(false);
    }

    public void signIn(ActionEvent actionEvent) {

        if ((passField.isVisible() && passField.getText().isEmpty()) || (passVisibleField.isVisible() && passVisibleField.getText().isEmpty()) || (userField.getText().isEmpty()))
            notComplete.setVisible(true);
        else {
            client = new Connection(userField.getText() + ":" + (passVisibleField.isVisible() ? passVisibleField.getText() : passField.getText()));

            String respond = client.getRespond();

            if (respond.equals("ok")) {
                Main.InStop = true;
                Main.connection=client;
                init();
            } else {

                wrongPass.setVisible(true);
            }
        }
    }

    public void signUpPage(MouseEvent mouseEvent) throws IOException {
        new PageLoader().Load("/View/signupPage.fxml");

    }

    public void showPass(MouseEvent mouseEvent) {
        showIcon.setVisible(true);
        hideIcon.setVisible(false);
        passVisibleField.setText(passField.getText());
        passField.setVisible(false);
        passVisibleField.setVisible(true);
    }

    public void hidePass(MouseEvent mouseEvent) {
        showIcon.setVisible(false);
        hideIcon.setVisible(true);
        passVisibleField.setVisible(false);
        passField.setVisible(true);
        passField.setText(passVisibleField.getText());
    }

    public void forgetPassPage(MouseEvent mouseEvent) {
    }

    public void init() {
        processPane.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(1000), processPane);
        ft.setFromValue(0);
        ft.setToValue(0.80);
        ft.playFromStart();
        Task<Void> initTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                client.getAndSaveInformation();
                return null;
            }
        };

        initTask.setOnSucceeded(e -> {
            try {
                new PageLoader().Load("/View/mainPage.fxml");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        new Thread(initTask).start();
    }

}
