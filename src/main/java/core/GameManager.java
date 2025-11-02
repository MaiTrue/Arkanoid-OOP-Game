package core;

import constants.GameConfig;
import graphics.*;
import javafx.geometry.Bounds;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * GameManager: quản lý paddle, ball, bricks, score, lives, powerups.
 * GamePanel sẽ tạo GameManager và gọi update/check logic.
 */
public class GameManager {
    private final Paddle paddle;
    private final Ball ball;
    private Group brickGroup; // Group of ImageView bricks
    private final BrickDisplay brickDisplay;
    private int score = 0;
    private int lives = 3;
    private final List<PowerUp> fallingPowerUps = new ArrayList<>();
    private final java.util.Random random = new java.util.Random();

    public List<PowerUp> getFallingPowerUps() {
        return fallingPowerUps;
    }

    private final java.util.Map<String, javafx.animation.PauseTransition> activeEffects = new java.util.HashMap<>();

    public void applyPowerUp(PowerUp p) {
        Paddle paddle = getPaddle();

        // Nếu loại này đang có hiệu ứng — reset lại timer
        if (activeEffects.containsKey(p.getType())) {
            activeEffects.get(p.getType()).stop();
        }

        // Áp dụng hiệu ứng
        p.applyEffect(paddle);

        // Tạo timer để remove sau duration
        javafx.animation.PauseTransition timer =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(p.getDuration()));
        timer.setOnFinished(e -> {
            p.removeEffect(paddle);
            activeEffects.remove(p.getType());
        });
        timer.play();

        // Ghi nhớ hiệu ứng đang hoạt động
        activeEffects.put(p.getType(), timer);
    }


    public GameManager(BrickDisplay brickDisplay) {
        this.brickDisplay = brickDisplay;
        brickGroup = brickDisplay.getBrickDisplay();

        paddle = new Paddle(
                brickDisplay.getPaddleImage(),
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.PADDLE_WIDTH / 2.0,
                GameConfig.WINDOW_HEIGHT - 60,
                GameConfig.PADDLE_WIDTH,
                GameConfig.PADDLE_HEIGHT
        );

        ball = new Ball(
                brickDisplay.getBallImage(),
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.BALL_SIZE / 2.0,
                GameConfig.WINDOW_HEIGHT - 100,
                GameConfig.BALL_SIZE
        );
    }

    // Trả về thành phần để GamePanel add vào scene
    public Paddle getPaddle() { return paddle; }
    public Ball getBall() { return ball; }
    public Group getBrickGroup() { return brickGroup; }

    public int getScore() { return score; }
    public int getLives() { return lives; }

    public void setBrickGroup(Group g) { brickGroup = g; }

    // Cập nhật logic va chạm giữa ball và bricks/paddle, tính điểm, mạng
    // Tương thích với GamePanel trước đó — deltatime giữ để tương thích nếu cần
    public void update(double deltaTime) {
        // collision logic handled by GamePanel previously; keep minimal here
        // The GamePanel handles movement inputs and calls ball.move()
        // This class can be expanded for AI, powerups, persistence, etc.
    }

    // Kiểm tra va chạm giữa ball & bricks, ball & paddle — trả về events
    public void handleCollisions(Runnable onBrickDestroyed, Runnable onAllBricksDestroyed) {
        // iterate over brickGroup children (ImageView)
        Iterator<javafx.scene.Node> it = brickGroup.getChildren().iterator();
        while (it.hasNext()) {
            javafx.scene.Node node = it.next();
            if (node instanceof javafx.scene.image.ImageView brick) {
                Bounds brickBounds = brick.getBoundsInParent();
                if (ball.getBallView().getBoundsInParent().intersects(brickBounds)) {
                    it.remove();
                    ball.reverseY();
                    score += 10;

                    if (random.nextDouble() < 0.1) {
                        double bx = brick.getX();
                        double by = brick.getY();
                        PowerUp powerUp;

                        // 50% spawn ExpandPaddlePowerUp, 50% spawn FallingEffect
                        if (random.nextBoolean()) {
                            powerUp = new ExpandPaddlePowerUp(bx, by); // có hiệu ứng phóng to paddle
                        } else {
                            powerUp = new FallingEffect(bx, by);
                        }

                        fallingPowerUps.add(powerUp);
                    }


                    if (onBrickDestroyed != null) onBrickDestroyed.run();

                    if (brickGroup.getChildren().isEmpty()) {
                        if (onAllBricksDestroyed != null) onAllBricksDestroyed.run();
                    }
                    break;
                }

            }
        }
    }

    // Khi bóng rơi
    public void ballDropped(Runnable onGameOver) {
        lives--;
        if (lives <= 0) {
            if (onGameOver != null) onGameOver.run();
        } else {
            ball.resetPosition();
        }
    }

    public void reset() {
        score = 0;
        lives = 3;
        brickGroup = brickDisplay.resetBricks();
        ball.resetPosition();
        paddle.getPaddleView().setX(GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.PADDLE_WIDTH / 2.0);
        fallingPowerUps.clear();

    }
    public BrickDisplay getBrickDisplay() {
        return brickDisplay;
    }

}
