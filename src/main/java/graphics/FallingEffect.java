package graphics;

import javafx.scene.image.Image;

public class FallingEffect extends PowerUp {

    public FallingEffect(double x, double y) {
        super(x, y, "Effect", 0);
        imageView.setImage(new Image(getClass().getResource("/image/effect2.png").toExternalForm()));
    }

    @Override
    public void applyEffect(Paddle paddle) {}
    @Override
    public void removeEffect(Paddle paddle) {}
    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {}
}
