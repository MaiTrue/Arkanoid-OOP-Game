package graphics;

import core.GameManager;
import javafx.scene.image.Image;

public class ReverseControlsPowerUp extends PowerUp {

    public ReverseControlsPowerUp(double x, double y, Image image) {
        super(x, y, "Reverse", 3, image);
    }

    @Override
    public void applyEffect(GameManager manager) {
        manager.setControlsReversed(true);
    }

    @Override
    public void removeEffect(GameManager manager) {
        manager.setControlsReversed(false);
    }
}