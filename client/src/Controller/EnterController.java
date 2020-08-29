package Controller;

import Model.PageLoader;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class EnterController {
    public void enter(MouseEvent mouseEvent) throws IOException {
        new PageLoader().Load("/View/signinPage.fxml");
    }
}
