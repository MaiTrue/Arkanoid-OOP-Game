package graphics;

import Patterns.BulbasaurPattern;
import javafx.stage.Stage;

public class Level3Panel extends GamePanel {
    public Level3Panel() {
        super(BulbasaurPattern.DATA); // truyền pattern Bulbasaur
    }

    @Override
    public void show(Stage stage) {
        super.show(stage);
    }
}
