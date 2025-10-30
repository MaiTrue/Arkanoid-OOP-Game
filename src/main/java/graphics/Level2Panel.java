package graphics;

import Patterns.CharmanderPattern;
import javafx.stage.Stage;

public class Level2Panel extends GamePanel {
    public Level2Panel() {
        super(CharmanderPattern.DATA); // truyền pattern Charmander vào GamePanel
    }

    @Override
    public void show(Stage stage) {
        super.show(stage);
    }
}
