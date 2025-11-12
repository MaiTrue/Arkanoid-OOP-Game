package core;

import constants.GameConfig;
import graphics.*;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class GameManager {
    private final Paddle paddle;
    private final Ball ball;
    private Group brickGroup; // Group of ImageView bricks
    private final BrickDisplay brickDisplay;
    private int score = 0;
    private int lives = 3;
    private final List<PowerUp> fallingPowerUps = new ArrayList<>();
    private final Random random = new Random();
    private int bricksBrokenSinceLastDrop = 0;
    private boolean controlsReversed = false;
    private boolean isShieldActive = false;
    public List<PowerUp> getFallingPowerUps() {
        return fallingPowerUps;
    }
    private final Map<String, PauseTransition> activeEffects = new HashMap<>();
    public void applyPowerUp(PowerUp p) {
        if (p.getType().equals("Shield") && isShieldActive) {
            return;
        }

        if (activeEffects.containsKey(p.getType())) {
            activeEffects.get(p.getType()).stop();
        }

        p.applyEffect(this);
        PauseTransition timer =
                new PauseTransition(Duration.seconds(p.getDuration()));
        timer.setOnFinished(e -> {
            p.removeEffect(this);
            activeEffects.remove(p.getType());
        });
        if (!p.getType().equals("Shield")) {
            timer.play();
        }
        activeEffects.put(p.getType(), timer);
    }
    public GameManager(BrickDisplay brickDisplay, Paddle paddle, Ball ball) {
        this.brickDisplay = brickDisplay;
        this.paddle = paddle;
        this.paddle.setGameManager(this);
        this.ball = ball;
        brickGroup = brickDisplay.getBrickDisplay();
    }

    public Paddle getPaddle() {
        return paddle;
    }
    public Ball getBall() {
        return ball;
    }
    public Group getBrickGroup() {
        return brickGroup;
    }
    public int getScore() {
        return score;
    }
    public int getLives() {
        return lives;
    }

    public void setBrickGroup(Group g) {
        brickGroup = g;
    }
    public void setControlsReversed(boolean reversed) {
        this.controlsReversed = reversed;
    }
    public boolean isControlsReversed() {
        return controlsReversed;
    }
    public void setShieldActive(boolean active) {
        this.isShieldActive = active;
    }
    public boolean hasShield() {
        return isShieldActive;
    }
    public void update(double deltaTime) {
    }

    public void handleCollisions(Runnable onBrickDestroyed, Runnable onAllBricksDestroyed) {
        Iterator<javafx.scene.Node> it = brickGroup.getChildren().iterator();
        while (it.hasNext()) {
            javafx.scene.Node node = it.next();
            if (node instanceof javafx.scene.image.ImageView brick) {
                Bounds brickBounds = brick.getBoundsInParent();
                if (ball.getBallView().getBoundsInParent().intersects(brickBounds)) {
                    it.remove();
                    ball.reverseY();
                    score += 10;
                    bricksBrokenSinceLastDrop++;

                    if (bricksBrokenSinceLastDrop > 15 && random.nextDouble() < 0.07) {
                        double bx = brick.getX();
                        double by = brick.getY();
                        PowerUp powerUp;
                        int powerUpType = random.nextInt(4);
                        Image img;
                        switch (powerUpType) {
                            case 0:
                                img = brickDisplay.getExpandPowerUpImage();
                                powerUp = new ExpandPaddlePowerUp(bx, by, img);
                                break;
                            case 1:
                                img = brickDisplay.getFastBallPowerUpImage();
                                powerUp = new FastBallPowerUp(bx, by, img);
                                break;
                            case 2:
                                img = brickDisplay.getReverseControlsPowerUpImage();
                                powerUp = new ReverseControlsPowerUp(bx, by, img);
                                break;
                            case 3:
                            default:
                                img = brickDisplay.getShieldPowerUpImage();
                                powerUp = new ShieldPowerUp(bx, by, img);
                                break;
                        }
                        fallingPowerUps.add(powerUp);
                        bricksBrokenSinceLastDrop = 0;
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

    public void ballDropped(Runnable onGameOver) {
        if (isShieldActive) {
            setShieldActive(false);
            ball.resetPosition();
            return;
        }
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
        bricksBrokenSinceLastDrop = 0;
        controlsReversed = false;
        isShieldActive = false; // Reset trạng thái Shield
        activeEffects.values().forEach(PauseTransition::stop);
        activeEffects.clear();
    }
    public BrickDisplay getBrickDisplay() {
        return brickDisplay;
    }
}