package graphics;

import core.GameManager;
import javafx.scene.image.Image;

public class ShieldPowerUp extends PowerUp {
    private static final String POWERUP_TYPE = "Shield";
    private static final double NO_TIME_LIMIT = 0.0;
    public ShieldPowerUp(double x, double y, Image image) {
        super(x, y, POWERUP_TYPE, NO_TIME_LIMIT, image);
    }

    @Override
    public void applyEffect(GameManager manager) {
        manager.setShieldActive(true);
    }

    @Override
    public void removeEffect(GameManager manager) {
        manager.setShieldActive(false);
    }
}