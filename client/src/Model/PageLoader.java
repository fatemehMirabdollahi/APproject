package Model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Paths;

import static Controller.InformationController.RECOURSE_URL;

public class PageLoader {
    private static final int WIDTH_PAGE = 450;
    private static final int HEGTH_PAGE = 500;
    private static Stage mainStage;

    public static void setMainStage(Stage stage) {
        mainStage = stage;
        mainStage.setTitle("Gmail");
        mainStage.setResizable(false);
        mainStage.getIcons().add(new Image(Paths.get(RECOURSE_URL + "\\appicon.png").toUri().toString()));
    }

    public void Load(String uri) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(uri));
        mainStage.setScene(new Scene(root, WIDTH_PAGE, HEGTH_PAGE));
        mainStage.show();
    }

    public void Load(String url, Object controller) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(url));
        fxmlLoader.setController(controller);
        fxmlLoader.load();
    }

}
