package graphics;

public class FastBallPowerUp extends PowerUp {

    public FastBallPowerUp(double x, double y) {
        super(x, y, "FastBall", 10); // GỌI THEO CONSTRUCTOR MỚI
    }

    @Override
    public void applyEffect(Paddle paddle) {
        // TODO: Tăng tốc bóng ở đây nếu bạn muốn — ví dụ:
        // GameManager.getBall().setSpeed(GameManager.getBall().getSpeed() * 1.5);
    }

    @Override
    public void removeEffect(Paddle paddle) {
        // TODO: Trả tốc độ bóng về bình thường
    }
}
