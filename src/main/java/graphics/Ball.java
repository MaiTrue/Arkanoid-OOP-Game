package graphics;

import base.MovableObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Ball kế thừa MovableObject, hiển thị bằng ImageView.
 * Giữ nguyên logic di chuyển/fizx collisions như cậu có.
 */
public class Ball extends MovableObject {
    private final ImageView ballView;
    private double dx = 300;  // tốc độ ngang (pixel per frame như code gốc)
    private double dy = -300; // tốc độ dọc

    private final double originalDx = 300;
    private final double originalDy = -300;

    private final double startX;
    private final double startY;

    public Ball(Image ballImage, double startX, double startY, double size) {
        super(startX, startY, size, size);
        ballView = new ImageView(ballImage);
        ballView.setFitWidth(size);
        ballView.setFitHeight(size);
        ballView.setX(startX);
        ballView.setY(startY);

        this.startX = startX;
        this.startY = startY;

        this.x = startX; this.y = startY; this.width = size; this.height = size;

        this.dx = originalDx;
        this.dy = originalDy;
    }

    public ImageView getBallView() {
        return ballView;
    }

    public void move(double deltaTime) {
        ballView.setX(ballView.getX() + dx * deltaTime);
        ballView.setY(ballView.getY() + dy * deltaTime);
        this.x = ballView.getX();
        this.y = ballView.getY();
    }

    public void reverseX() { dx = -dx; }
    public void reverseY() { dy = -dy; }

    public boolean hitPaddle(Paddle paddle) {
        double ballBottom = getY() + getHeight();
        double paddleTop = paddle.getY();
        double ballCenterX = getX() + getWidth() / 2;

        boolean withinXRange = ballCenterX >= paddle.getX() && ballCenterX <= paddle.getX() + paddle.getWidth();
        boolean touchingTop = ballBottom >= paddleTop && ballBottom <= paddleTop + (Math.abs(dy) * 0.05);

        return withinXRange && touchingTop;
    }

    /**
     * Đặt tốc độ mới, giữ nguyên hướng.
     * @param newSpeed Độ lớn tốc độ mới (ví dụ: 4)
     */
    public void setSpeed(double newSpeed) {
        dx = (dx > 0) ? newSpeed : -newSpeed;
        dy = (dy > 0) ? newSpeed : -newSpeed;
    }

    /**
     * Trả về tốc độ gốc, giữ nguyên hướng.
     */
    public void resetSpeed() {
        dx = (dx > 0) ? originalDx : -originalDx;
        dy = (dy > 0) ? originalDy : -originalDy;
    }

    public void resetPosition() {
        ballView.setX(startX);
        ballView.setY(startY);

        dx = originalDx;
        dy = originalDy;
        this.x = startX; this.y = startY;
    }

    @Override
    public void update(double deltaTime) {
        // giữ trống — GamePanel gọi move() trực tiếp.
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {
        // not used (using ImageView)
    }

    public double getX() { return ballView.getX(); }
    public double getY() { return ballView.getY(); }
    public double getWidth() { return ballView.getFitWidth(); }
    public double getHeight() { return ballView.getFitHeight(); }
}
