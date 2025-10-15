package graphics;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Game game = new Game();
        game.show(stage); // gọi giao diện và logic của Game
    }

    public static void main(String[] args) {
        launch(args);
    }
}
