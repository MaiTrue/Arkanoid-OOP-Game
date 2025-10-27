package graphics;

public class FastBallPowerUp extends PowerUp {
    public FastBallPowerUp(double x, double y) {
        super(x, y, 25, 25, "FastBall", 10);
    }

    @Override
    public void applyEffect(Paddle paddle) {
        // Demo: không implement trực tiếp vì Ball giữ dx/dy cứng. Có thể set flag trong GameManager.
    }

    @Override
    public void removeEffect(Paddle paddle) {}
}
