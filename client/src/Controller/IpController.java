package Controller;

import Model.Main;
import Model.PageLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.io.IOException;

public class IpController {
    @FXML
    public TextField ip;
    public void start(ActionEvent actionEvent) throws IOException {
        Main.IP = ip.getText();
        new PageLoader().Load("/View/enterPage.fxml");
    }
}
