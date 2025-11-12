package core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Renderer {
    private GraphicsContext gc;
    public Renderer(GraphicsContext gc) {
        this.gc = gc;
    }

    public void clear() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 1000, 770);
    }
}
