package graphics;

import constants.GameConfig;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Game {
    public void show(Stage stage) {
        BrickDisplay brickDisplay = new BrickDisplay();
        Group root = new Group(brickDisplay.getBrickDisplay());

        Scene scene = new Scene(root, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        stage.setTitle("Arkanoid");
        stage.setScene(scene);
        stage.show();
    }
}
