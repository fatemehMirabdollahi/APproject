package Controller;

import Model.IO.conversation;
import Model.PageLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.io.IOException;

public class listItemController {
    @FXML
    public Text texts, username, time;
    @FXML
    public AnchorPane item;

    private conversation conversation;

    public listItemController(conversation conversation) throws IOException {
        this.conversation = conversation;
        new PageLoader().Load("/View/listItems.fxml", this);

    }

    public AnchorPane init() {
        username.setText( " from " + conversation.getSender()+" to "+conversation.getReciver());
        texts.setText(conversation.getText() + " ...");
        return item;
    }
}
