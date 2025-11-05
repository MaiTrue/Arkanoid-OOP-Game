package graphics;

import javafx.scene.image.Image;
import core.GameManager;

public class FastBallPowerUp extends PowerUp {

    public FastBallPowerUp(double x, double y, Image image) {
        super(x, y, "FastBall", 3, image);
    }

    @Override
    public void applyEffect(GameManager manager) {
        // Lấy Ball từ manager và tăng tốc
        manager.getBall().setSpeed(600);
    }

    @Override
    public void removeEffect(GameManager manager) {
        // Trả tốc độ bóng về bình thường
        manager.getBall().resetSpeed();
    }
}
