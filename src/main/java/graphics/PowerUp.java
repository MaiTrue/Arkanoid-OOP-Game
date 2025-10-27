package graphics;

import base.GameObject;
import javafx.scene.canvas.GraphicsContext;

/**
 * PowerUp: trừu tượng, rơi xuống khi brick bị phá.
 * Ở phiên bản này là khung — integration có thể thực hiện sau.
 */
public abstract class PowerUp extends GameObject {
    protected String type;
    protected double duration;

    public PowerUp(double x, double y, double width, double height, String type, double duration) {
        super(x, y, width, height);
        this.type = type; this.duration = duration;
    }

    public abstract void applyEffect(Paddle paddle);
    public abstract void removeEffect(Paddle paddle);

    @Override
    public void update(double deltaTime) {
        y += 150 * deltaTime; // rơi xuống
    }

    @Override
    public void render(GraphicsContext gc) {}
}
