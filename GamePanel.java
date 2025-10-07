import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel implements Runnable {
    private Thread gameThread;
    private boolean running;

    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    private Paddle paddle;
    private Ball ball;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        paddle = new Paddle(350, 550, 100, 15);
        ball = new Ball(390, 300, 10);

        setFocusable(true);
        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                paddle.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                paddle.keyReleased(e);
            }
        });
    }

    public void startGameLoop() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        // game loop: update 60 lần / giây
        double fps = 60.0;
        double nsPerFrame = 1000000000 / fps;
        long lastTime = System.nanoTime();
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;

            while (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        paddle.update();
        ball.update();

        // Va chạm paddle
        if (ball.getRect().intersects(paddle.getRect())) {
            ball.reverseY();
        }

        // Va chạm tường
        if (ball.x <= 0 || ball.x + ball.diameter >= WIDTH) ball.reverseX();
        if (ball.y <= 0) ball.reverseY();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paddle.draw(g);
        ball.draw(g);
    }
}
