package base;

public abstract class MovableObject extends GameObject {
    protected double dx = 0;
    protected double dy = 0;
    public MovableObject(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
    public void move(double deltaTime) {
        x += dx * deltaTime;
        y += dy * deltaTime;
    }
}
