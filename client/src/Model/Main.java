package Model;
import Model.IO.User;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static User user;
    public static Connection connection;
    public static String IP = "localhost";
    public static int PORT = 8080;
    public static boolean InStop=false;
    public static boolean upStop= false;
    @Override
    public void init() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        PageLoader.setMainStage(primaryStage);
        new PageLoader().Load("/View/ipPage.fxml");
    }



    public static void main(String[] args) {
        launch(args);
    }
}
