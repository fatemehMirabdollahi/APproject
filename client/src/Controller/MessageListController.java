package Controller;

import Model.IO.Message;
import Model.PageLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;


public class MessageListController {
    @FXML
    public AnchorPane item;
    @FXML
    public Text subject,texts;

    private Message message;

    public MessageListController(Message message) throws IOException {
        this.message = message;
        new PageLoader().Load("/View/conversationItem.fxml", this);

    }

    public AnchorPane init() {
        subject.setText(message.getSubject());
        texts.setText(message.getMessageText());
        return item;
    }
}
