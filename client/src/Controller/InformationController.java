package Controller;

import Model.IO.Message;
import Model.IO.MessageType;
import Model.Main;
import Model.PageLoader;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static Controller.SignupController.client;
import static Model.Main.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class InformationController {
    /**
    the address of the recourse folder set it before running
     **/
    public static String RECOURSE_URL = "C:\\Users\\Client\\Desktop\\final\\client\\src\\Resources";
    @FXML
    public TextField phoneField, imagePath;
    @FXML
    public RadioButton female, male;
    @FXML
    public Text notFound;
    @FXML
    public Circle circle;
    ToggleGroup tg = new ToggleGroup();
    @FXML
    public StackPane processPane;

    @FXML
    public void initialize() {
        circle.setFill(new ImagePattern(new Image(Paths.get(RECOURSE_URL + "\\unknown.png").toUri().toString())));
        female.setToggleGroup(tg);
        male.setToggleGroup(tg);
    }

    public void clickedField(MouseEvent mouseEvent) {
        notFound.setVisible(false);
    }

    public void signup(ActionEvent actionEvent) throws InterruptedException, IOException {
        user.setPhoneNumber(phoneField.getText());
        if (tg.getSelectedToggle() == female)
            user.setGender("female");
        else if (tg.getSelectedToggle() == male)
            user.setGender("male");
        client.sendRequest(new Message(MessageType.signUp, user.getUsername(), "", "sendFile"));
        client.objectOutputStream.flush();
        File file = new File(user.getImageURL());
        user.imageSize = file.length();
        client.objectOutputStream.writeObject(user);
        client.objectOutputStream.flush();
        Main.connection = client;
        sendImage();
    }

    public void sendImage() {
        processPane.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(1000), processPane);
        ft.setFromValue(0);
        ft.setToValue(0.80);
        ft.playFromStart();

        Task<Void> sendFileTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                client.sendProFile(user);
                return null;
            }
        };

        sendFileTask.setOnSucceeded(e -> {
            try {
                new PageLoader().Load("/View/mainPage.fxml");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        new Thread(sendFileTask).start();
    }

    public void signPage(MouseEvent mouseEvent) throws IOException {
        new PageLoader().Load("/View/signupPage.fxml");
    }

    public void setImage(ActionEvent actionEvent) {
        ImagePattern pattern = null;
        user.setPhoneNumber(phoneField.getText());
        String temp = RECOURSE_URL + "\\first.png";
        if (imagePath.getText().contains("png")) {
            File file = new File(imagePath.getText());
            if (file.exists()) {
                user.imageSize = (long) file.length();
                System.out.println(user.imageSize);
                pattern = new ImagePattern(new Image(Paths.get(imagePath.getText()).toUri().toString()));
                circle.setFill(pattern);
                user.setImageURL(imagePath.getText());
                user.setImageName(imagePath.getText().substring(imagePath.getText().lastIndexOf("\\") + 1, imagePath.getText().indexOf(".")) + ".png");
                System.out.println(user.getImageURL());
                user.hasImage = true;
            } else notFound.setVisible(true);


        } else {

            File file = new File(temp.replace("first", imagePath.getText()));
            if (file.exists()) {
                pattern = new ImagePattern(new Image(Paths.get(temp.replace("first", imagePath.getText())).toUri().toString()));
                circle.setFill(pattern);
                user.setImageURL(temp.replace("first", imagePath.getText()));
                user.imageSize = (long) file.length();
                user.setImageName(imagePath.getText() + ".png");
                user.hasImage = true;
            } else notFound.setVisible(true);

        }
    }
}
