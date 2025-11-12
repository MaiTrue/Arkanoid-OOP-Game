package graphics;

import Patterns.CharmanderPattern;
import javafx.stage.Stage;

public class Level2Panel extends GamePanel {
    public Level2Panel() {
        super(CharmanderPattern.DATA, 2);
    }
    @Override
    public void show(Stage stage) {
        super.show(stage);
    }
}
