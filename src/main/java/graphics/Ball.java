package graphics;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Ball {
    private final ImageView ballView;
    private double dx = 2;  // tốc độ ngang
    private double dy = -2; // tốc độ dọc

    private final double startX;
    private final double startY;

    public Ball(Image ballImage, double startX, double startY, double size) {
        ballView = new ImageView(ballImage);
        ballView.setFitWidth(size);
        ballView.setFitHeight(size);
        ballView.setX(startX);
        ballView.setY(startY);

        this.startX = startX;
        this.startY = startY;
    }

    public ImageView getBallView() {
        return ballView;
    }

    public void move() {
        ballView.setX(ballView.getX() + dx);
        ballView.setY(ballView.getY() + dy);
    }

    public void reverseX() {
        dx = -dx;
    }

    public void reverseY() {
        dy = -dy;
    }

    // Khi bóng chạm vào paddle chỉ nảy nếu va vào mặt trên
    public boolean hitPaddle(Paddle paddle) {
        double ballBottom = getY() + getHeight();
        double paddleTop = paddle.getY();
        double ballCenterX = getX() + getWidth() / 2;

        boolean withinXRange = ballCenterX >= paddle.getX() && ballCenterX <= paddle.getX() + paddle.getWidth();
        boolean touchingTop = ballBottom >= paddleTop && ballBottom <= paddleTop + Math.abs(dy);

        return withinXRange && touchingTop;
    }

    public void resetPosition() {
        ballView.setX(startX);
        ballView.setY(startY);
        dx = 2;
        dy = -2;
    }

    public double getX() {
        return ballView.getX();
    }

    public double getY() {
        return ballView.getY();
    }

    public double getWidth() {
        return ballView.getFitWidth();
    }

    public double getHeight() {
        return ballView.getFitHeight();
    }
}
