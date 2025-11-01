package graphics;

import javafx.scene.image.Image;
import core.GameManager;

public class FastBallPowerUp extends PowerUp {

    public FastBallPowerUp(double x, double y) {
        super(x, y, "FastBall", 3);
        imageView.setImage(new Image(getClass().getResource("/image/fast_ball.png").toExternalForm()));// GỌI THEO CONSTRUCTOR MỚI
    }

    @Override
    public void applyEffect(GameManager manager) {
        // Lấy Ball từ manager và tăng tốc
        manager.getBall().setSpeed(4);
    }

    @Override
    public void removeEffect(GameManager manager) {
        // Trả tốc độ bóng về bình thường
        manager.getBall().resetSpeed();
    }
}
