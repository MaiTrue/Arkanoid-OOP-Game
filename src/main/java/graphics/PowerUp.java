package graphics;

import base.GameObject;
import core.GameManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public abstract class PowerUp extends GameObject {
    protected String type;
    protected double duration;
    protected ImageView imageView;
    protected static final double FIXED_SIZE = 50; // ép size chung cho mọi vật phẩm

    public PowerUp(double x, double y, String type, double duration, Image image) {
        super(x, y, FIXED_SIZE, FIXED_SIZE);
        this.type = type;
        this.duration = duration;

        imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(FIXED_SIZE);
        imageView.setFitHeight(FIXED_SIZE);
        imageView.setX(x);
        imageView.setY(y);
    }

    public abstract void applyEffect(GameManager manager);
    public abstract void removeEffect(GameManager manager);

    @Override
    public void update(double deltaTime) {
        y += 150 * deltaTime; // rơi xuống
        imageView.setY(y);
    }

    @Override
    public void render(GraphicsContext gc) {}

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }

    public String getType() { return type; }
    public double getDuration() { return duration; }
    public ImageView getImageView() { return imageView; }
}
