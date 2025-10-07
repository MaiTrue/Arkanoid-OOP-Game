import java.awt.*;

public class Ball {
    int x, y, diameter;
    int dx = 4, dy = -4;

    public Ball(int x, int y, int diameter) {
        this.x = x; this.y = y;
        this.diameter = diameter;
    }

    public void update() {
        x += dx;
        y += dy;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, diameter, diameter);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, diameter, diameter);
    }

    public void reverseX() { dx = -dx; }
    public void reverseY() { dy = -dy; }
}
