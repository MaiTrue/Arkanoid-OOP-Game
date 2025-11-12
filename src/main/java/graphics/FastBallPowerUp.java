package graphics;

import javafx.scene.image.Image;
import core.GameManager;

public class FastBallPowerUp extends PowerUp {

    public FastBallPowerUp(double x, double y, Image image) {
        super(x, y, "FastBall", 3, image);
    }

    @Override
    public void applyEffect(GameManager manager) {
        manager.getBall().setSpeed(600);
    }

    @Override
    public void removeEffect(GameManager manager) {
        manager.getBall().resetSpeed();
    }
}
