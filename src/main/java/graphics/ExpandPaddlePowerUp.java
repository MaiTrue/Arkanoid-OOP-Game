package graphics;

import javafx.scene.image.Image;
import core.GameManager;

public class ExpandPaddlePowerUp extends PowerUp {

    public ExpandPaddlePowerUp(double x, double y) {
        super(x, y, "Expand", 5);
        // Không cần hình riêng, có thể dùng chung effect2.png hoặc hình bạn thích
        imageView.setImage(new Image(getClass().getResource("/image/effect2.png").toExternalForm()));
    }

    @Override
    public void applyEffect(GameManager manager) {
        // Lấy paddle từ manager
        Paddle paddle = manager.getPaddle();
        paddle.setPaddleWidth(paddle.getOriginalWidth() * 1.5);
    }

    @Override
    public void removeEffect(GameManager manager) {
        // Lấy paddle từ manager
        Paddle paddle = manager.getPaddle();
        paddle.setPaddleWidth(paddle.getOriginalWidth());
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {}
}
