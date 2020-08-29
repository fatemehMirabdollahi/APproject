package Controller;

import Model.IO.Message;

import javafx.scene.control.ListCell;

import java.io.IOException;

public class MessageListItem extends ListCell<Message> {
    @Override
    public void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);
        if (message != null) {
            setStyle("-fx-background-color: #adb4bc");
            try {
                if(message!=null)
                    setGraphic
                            (new MessageListController(message).init());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
