import java.awt.*;
import java.awt.event.KeyEvent;

public class Paddle {
    int x, y, width, height;
    int speed = 6;
    boolean leftPressed, rightPressed;

    public Paddle(int x, int y, int width, int height) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
    }

    public void update() {
        if (leftPressed && x > 0) x -= speed;
        if (rightPressed && x + width < 800) x += speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }
}
