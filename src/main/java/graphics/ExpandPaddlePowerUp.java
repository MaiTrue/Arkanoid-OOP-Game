package graphics;

import javafx.scene.image.Image;
import core.GameManager;

public class ExpandPaddlePowerUp extends PowerUp {

    public ExpandPaddlePowerUp(double x, double y, Image image) {
        super(x, y, "Expand", 5, image);
    }

    @Override
    public void applyEffect(GameManager manager) {
        Paddle paddle = manager.getPaddle();
        paddle.setPaddleWidth(paddle.getOriginalWidth() * 1.5);
    }

    @Override
    public void removeEffect(GameManager manager) {
        Paddle paddle = manager.getPaddle();
        paddle.setPaddleWidth(paddle.getOriginalWidth());
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {}
}
