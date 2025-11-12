package graphics;

import base.MovableObject;
import core.GameManager;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Paddle kế thừa MovableObject nhưng dùng ImageView để hiển thị.
 * Giữ nguyên hành vi cũ: moveLeft(moveRight) dùng deltaTime.
 */
public class Paddle extends MovableObject {
    private final ImageView paddleView;
    private double speed = 600; // pixel mỗi giây
    private final double originalWidth;
    private GameManager manager;

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

    public Paddle(Image image, double x, double y, double width, double height) {
        super(x, y, width, height);
        originalWidth = width;
        paddleView = new ImageView(image);
        paddleView.setX(x);
        paddleView.setY(y);
        paddleView.setFitWidth(width);
        paddleView.setFitHeight(height);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setGameManager(GameManager manager) {
        this.manager = manager;
    }

    public ImageView getPaddleView() {
        return paddleView;
    }

    @Override
    public double getX() {
        return paddleView.getX();
    }

    @Override
    public double getY() {
        return paddleView.getY();
    }

    @Override
    public double getWidth() {
        return paddleView.getFitWidth();
    }

    @Override
    public double getHeight() {
        return paddleView.getFitHeight();
    }

    public void setPaddleWidth(double newWidth) {
        paddleView.setFitWidth(newWidth);
        this.width = newWidth; // đồng bộ lại field width trong MovableObject
    }

    public double getOriginalWidth() {
        return originalWidth;
    }

    public void moveLeft(double deltaTime) {
        boolean reversed = (manager != null && manager.isControlsReversed());

        dx = reversed ? speed : -speed; // Nếu đảo, đi ngược hướng
        double newX = paddleView.getX() + dx * deltaTime;

        if (newX < 0) newX = 0;
        if (newX + paddleView.getFitWidth() > 800) // hoặc GameConfig.WINDOW_WIDTH
            newX = 800 - paddleView.getFitWidth();

        paddleView.setX(newX);
        this.x = newX;
    }

    public void moveRight(double deltaTime, double windowWidth) {
        boolean reversed = (manager != null && manager.isControlsReversed());

        dx = reversed ? -speed : speed; // Nếu đảo, đi ngược hướng
        double newX = paddleView.getX() + dx * deltaTime;

        if (newX < 0) newX = 0;
        if (newX + paddleView.getFitWidth() > windowWidth)
            newX = windowWidth - paddleView.getFitWidth();

        paddleView.setX(newX);
        this.x = newX;
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }

    public void move(double deltaTime, boolean movingLeft, boolean movingRight, double windowWidth) {
        if (manager == null) return;

        double actualSpeed = speed;
        double newX = paddleView.getX();

        boolean reversed = manager.isControlsReversed();

        if (movingLeft) {
            newX += (reversed ? actualSpeed : -actualSpeed) * deltaTime;
        } else if (movingRight) {
            newX += (reversed ? -actualSpeed : actualSpeed) * deltaTime;
        }

        if (newX < 0) newX = 0;
        if (newX + paddleView.getFitWidth() > windowWidth)
            newX = windowWidth - paddleView.getFitWidth();

        paddleView.setX(newX);
        this.x = newX;
    }

}
