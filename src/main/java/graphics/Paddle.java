package graphics;

import base.MovableObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.geometry.Rectangle2D;


/**
 * Paddle kế thừa MovableObject nhưng dùng ImageView để hiển thị.
 * Giữ nguyên hành vi cũ: moveLeft(moveRight) dùng deltaTime.
 */
public class Paddle extends MovableObject {
    private final ImageView paddleView;
    private double speed = 600; // pixel mỗi giây
    private final double originalWidth;

    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
        originalWidth = width;
        Image image = new Image(getClass().getResource("/image/paddle2.png").toExternalForm());
        paddleView = new ImageView(image);
        paddleView.setX(x);
        paddleView.setY(y);
        paddleView.setFitWidth(width);
        paddleView.setFitHeight(height);
    }
    public void setPaddleWidth(double newWidth) {
        paddleView.setFitWidth(newWidth);
        this.width = newWidth; // đồng bộ lại field width trong MovableObject
    }


    public double getOriginalWidth() {
        return originalWidth;
    }

    public Paddle(Image image, double x, double y, double width, double height) {
        super(x, y, width, height);
        originalWidth = width;
        paddleView = new ImageView(image);
        paddleView.setX(x);
        paddleView.setY(y);
        paddleView.setFitWidth(width);
        paddleView.setFitHeight(height);

        // đồng bộ fields
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    public ImageView getPaddleView() {
        return paddleView;
    }

    @Override
    public double getX() { return paddleView.getX(); }

    @Override
    public double getY() { return paddleView.getY(); }

    @Override
    public double getWidth() { return paddleView.getFitWidth(); }

    @Override
    public double getHeight() { return paddleView.getFitHeight(); }

    // Di chuyển sang trái — có deltaTime để mượt hơn
    public void moveLeft(double deltaTime) {
        dx = -speed;
        double newX = paddleView.getX() + dx * deltaTime;
        if (newX < 0) newX = 0;
        paddleView.setX(newX);
        this.x = newX;
    }

    // Di chuyển sang phải — có deltaTime để mượt hơn
    public void moveRight(double deltaTime, double windowWidth) {
        dx = speed;
        double newX = paddleView.getX() + dx * deltaTime;
        if (newX + paddleView.getFitWidth() > windowWidth)
            newX = windowWidth - paddleView.getFitWidth();
        paddleView.setX(newX);
        this.x = newX;
    }

    @Override
    public void update(double deltaTime) {
        // Ở thiết kế hiện tại GamePanel gọi moveLeft/moveRight trực tiếp.
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {
        // Không dùng, vì dùng ImageView
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }
}
