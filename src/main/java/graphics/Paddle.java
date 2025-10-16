package graphics;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Paddle {
    private final ImageView paddleView;

    public Paddle(Image image, double x, double y, double width, double height) {
        paddleView = new ImageView(image);
        paddleView.setX(x);
        paddleView.setY(y);
        paddleView.setFitWidth(width);
        paddleView.setFitHeight(height);
    }

    public ImageView getPaddleView() {
        return paddleView;
    }

    public double getX() {
        return paddleView.getX();
    }

    public double getY() {
        return paddleView.getY();
    }

    public double getWidth() {
        return paddleView.getFitWidth();
    }

    public double getHeight() {
        return paddleView.getFitHeight();
    }

    // Di chuyển sang trái — có deltaTime để mượt hơn
    public void moveLeft(double deltaTime) {
        double speed = 600; // pixel mỗi giây
        double newX = paddleView.getX() - speed * deltaTime;
        if (newX < 0) newX = 0;
        paddleView.setX(newX);
    }

    // Di chuyển sang phải — có deltaTime để mượt hơn
    public void moveRight(double deltaTime, double windowWidth) {
        double speed = 600; // pixel mỗi giây
        double newX = paddleView.getX() + speed * deltaTime;
        if (newX + paddleView.getFitWidth() > windowWidth)
            newX = windowWidth - paddleView.getFitWidth();
        paddleView.setX(newX);
    }
}
