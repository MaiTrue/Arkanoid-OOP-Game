package  image;
import graphics.BrickDisplay;
import constants.GameConfig;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        BrickDisplay brickDisplay = new BrickDisplay();
        Group root = brickDisplay.getBrickDisplay();

        Scene scene = new Scene(root, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        stage.setTitle("Arkanoid");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
