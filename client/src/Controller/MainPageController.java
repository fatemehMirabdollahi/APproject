package Controller;

import Model.IO.Message;
import Model.IO.MessageType;
import Model.IO.conversation;
import Model.Main;
import Model.PageLoader;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import static Model.Main.connection;
import static Model.Main.user;

public class MainPageController {
    @FXML
    public Pane menuPane, composePage,sentPane,inboxPane,mainPane;
    @FXML
    public TextField subject, receiver;
    @FXML
    public javafx.scene.control.TextArea emailText;
    @FXML
    public Text notComplete, invalid, username, emailAddress, title;
    @FXML
    public ImageView profile, sendIcon;
    @FXML
    public StackPane processPane;
    @FXML
    public ListView<conversation> inboxList, sentList;
    @FXML
    public ListView<Message> conversionList;
    @FXML
    public Label sentLable,inboxLable;
    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
        try {
            profile.setImage(new Image(Paths.get(user.getImageURL()).toUri().toString()));
        } catch (Exception e) {

        }
        ArrayList<conversation> sent = null;
        ArrayList<conversation> inbox = null;
        username.setText(user.getUsername());
        emailAddress.setText(user.getUsername() + "@gmail.com");
        if (user.hassent()) {
            connection.sendRequest(new Message(MessageType.askSent, user.getUsername(), "", ""));
            sent = (ArrayList<conversation>) Main.connection.objectInputStream.readObject();
            if (sent != null)
                sentList.setItems(FXCollections.observableArrayList(sent));
            sentList.setCellFactory(e -> new UserListItem());

        }
        if (user.hasInbox()) {
            connection.sendRequest(new Message(MessageType.askInbox, user.getUsername(), "", ""));
            inbox = (ArrayList<conversation>) Main.connection.objectInputStream.readObject();
            if (inbox != null)
                inboxList.setItems(FXCollections.observableArrayList(inbox));
            inboxList.setCellFactory(userListView -> new UserListItem());
            inboxList.setVisible(true);
        }


    }

    public void search(MouseEvent mouseEvent) {
    }

    public void menu(MouseEvent mouseEvent) {
        menuPane.setVisible(true);
        TranslateTransition ttMenuPane = new TranslateTransition(Duration.millis(1000), menuPane);
        ttMenuPane.setToX(215);
        ttMenuPane.play();
    }

    public void menuBack(MouseEvent mouseEvent) {
        menuPane.setVisible(true);
        TranslateTransition ttMenuPane = new TranslateTransition(Duration.millis(1000), menuPane);
        ttMenuPane.setToX(-210);
        ttMenuPane.play();
    }

    public void composeMessage(MouseEvent mouseEvent) {
        composePage.setVisible(true);
    }

    public void backToMainPage(MouseEvent mouseEvent) {
        composePage.setVisible(false);
    }

    public void sendMessage(MouseEvent mouseEvent) {
        if (subject.getText().isEmpty() || emailText.getText().isEmpty() || receiver.getText().isEmpty())
            notComplete.setVisible(true);
        else if (!receiver.getText().contains("@gmail.com"))
            invalid.setVisible(true);
        else
            send(new Message(MessageType.Text, user.getUsername(), receiver.getText().substring(0, receiver.getText().indexOf("@")), emailText.getText(), subject.getText()));

    }

    public void inbox(ActionEvent actionEvent) {
        sentList.setVisible(false);
        inboxList.setVisible(true);
        title.setText("Inbox");
    }

    public void sent(ActionEvent actionEvent) {
        inboxList.setVisible(false);
        sentList.setVisible(true);
        title.setText("Sent");
    }

    public void changeInformation(ActionEvent actionEvent) throws IOException {
        new PageLoader().Load("/View/changeInformation.fxml");
    }

    public void blockedUser(ActionEvent actionEvent) {
    }

    public void outbox(ActionEvent actionEvent) {
    }


    public void composeSelect(MouseEvent mouseEvent) {
        notComplete.setVisible(false);
        invalid.setVisible(false);
    }

    public void send(Message message) {

        processPane.setVisible(true);
        sendIcon.setVisible(false);

        FadeTransition ft = new FadeTransition(Duration.millis(1000), processPane);
        ft.setFromValue(0);
        ft.setToValue(0.80);
        ft.playFromStart();

        Task<Void> sendFileTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Main.connection.sentMessage(message);
                return null;
            }
        };

        sendFileTask.setOnSucceeded(e -> {
            processPane.setVisible(false);
            sendIcon.setVisible(true);
        });

        new Thread(sendFileTask).start();
    }

    public void refresh(MouseEvent mouseEvent) throws IOException, ClassNotFoundException {
        ArrayList<conversation> sent = null;
        ArrayList<conversation> inbox = null;
        username.setText(user.getUsername());
        emailAddress.setText(user.getUsername() + "@gmail.com");
        if (user.hassent()) {
            connection.sendRequest(new Message(MessageType.askSent, user.getUsername(), "", ""));
            sent = (ArrayList<conversation>) Main.connection.objectInputStream.readObject();
            if (sent != null)
                sentList.setItems(FXCollections.observableArrayList(sent));
            sentList.setCellFactory(userListView -> new UserListItem());

        }
        if (user.hasInbox()) {
            connection.sendRequest(new Message(MessageType.askInbox, user.getUsername(), "", ""));
            inbox = (ArrayList<conversation>) Main.connection.objectInputStream.readObject();
            if (inbox != null)
                inboxList.setItems(FXCollections.observableArrayList(inbox));
            inboxList.setCellFactory(userListView -> new UserListItem());

        }
    }

    public void select(MouseEvent mouseEvent) {
        mainPane.setVisible(false);
        sentPane.setVisible(false);
        inboxPane.setVisible(false);
        conversation selected = null;
        conversionList.setVisible(true);
        if (inboxList.isVisible()) {
            inboxList.setVisible(false);
            selected = inboxList.getSelectionModel().getSelectedItem();
            inboxPane.setVisible(true);
            inboxLable.setText(selected.getReciver() + " , " + selected.getSender());
        }
        else if (sentList.isVisible()) {
            sentList.setVisible(false);
            selected = sentList.getSelectionModel().getSelectedItem();
            sentPane.setVisible(true);
            sentLable.setText(selected.getReciver() + " , " + selected.getSender());
        }
        if (selected!=null)
        conversionList.setItems(FXCollections.observableArrayList(selected.getMessages()));
        conversionList.setCellFactory(e -> new MessageListItem());

    }


    public void backtoSent(MouseEvent mouseEvent) {
        mainPane.setVisible(true);
        sentPane.setVisible(false);
        conversionList.setVisible(false);
        sentList.setVisible(true);
    }

    public void backtoInbox(MouseEvent mouseEvent) {
        mainPane.setVisible(true);
        inboxPane.setVisible(false);
        conversionList.setVisible(false);
        inboxList.setVisible(true);
    }
}
