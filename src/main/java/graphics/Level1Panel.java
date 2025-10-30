package graphics;

import Patterns.PikachuPattern;
import javafx.stage.Stage;

public class Level1Panel extends GamePanel {
    public Level1Panel() {
        super(PikachuPattern.DATA); // truyền pattern Pikachu vào GamePanel
    }

    @Override
    public void show(Stage stage) {
        super.show(stage);
    }
}
