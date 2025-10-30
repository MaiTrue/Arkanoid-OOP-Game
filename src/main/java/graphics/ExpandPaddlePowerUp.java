package graphics;

import javafx.scene.image.Image;

public class ExpandPaddlePowerUp extends PowerUp {

    public ExpandPaddlePowerUp(double x, double y) {
        super(x, y, "Expand", 5);
        // Không cần hình riêng, có thể dùng chung effect2.png hoặc hình bạn thích
        imageView.setImage(new Image(getClass().getResource("/image/effect2.png").toExternalForm()));
    }

    @Override
    public void applyEffect(Paddle paddle) {
        // Reset lại kích thước gốc trước khi phóng to
        paddle.getPaddleView().setFitWidth(paddle.getOriginalWidth() * 1.5);
    }

    @Override
    public void removeEffect(Paddle paddle) {
        // Trả lại kích thước ban đầu
        paddle.getPaddleView().setFitWidth(paddle.getOriginalWidth());
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {}
}
