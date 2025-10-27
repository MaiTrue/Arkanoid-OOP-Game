package base;

import javafx.scene.canvas.GraphicsContext;

/**
 * GameObject: lớp trừu tượng cơ sở cho mọi đối tượng game
 * chứa vị trí và kích thước.
 */
public abstract class GameObject {
    protected double x, y;
    protected double width, height;

    public GameObject(double x, double y, double width, double height) {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    // Cập nhật trạng thái (được gọi mỗi frame)
    public abstract void update(double deltaTime);

    // Vẽ (nếu dùng Canvas); nếu dùng ImageView có thể để trống
    public abstract void render(GraphicsContext gc);

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
