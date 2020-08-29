package Controller;

import Model.IO.conversation;
import javafx.scene.control.ListCell;
import java.io.IOException;

public class UserListItem extends ListCell<conversation> {

    @Override
    public void updateItem(conversation conversation, boolean empty) {
        super.updateItem(conversation, empty);
        if (conversation != null) {
            setStyle("-fx-background-color: #adb4bc");
            try {
                if(conversation!=null)
                setGraphic
                        (new listItemController(conversation).init());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
