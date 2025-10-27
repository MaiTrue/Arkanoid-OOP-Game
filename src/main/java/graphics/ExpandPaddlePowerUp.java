package graphics;

public class ExpandPaddlePowerUp extends PowerUp {

    public ExpandPaddlePowerUp(double x, double y) {
        super(x, y, 25, 25, "Expand", 10);
    }

    @Override
    public void applyEffect(Paddle paddle) {
        paddle.getPaddleView().setFitWidth(paddle.getPaddleView().getFitWidth() * 1.5);
    }

    @Override
    public void removeEffect(Paddle paddle) {
        paddle.getPaddleView().setFitWidth(paddle.getPaddleView().getFitWidth() / 1.5);
    }
}
