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
    private final Random random = new Random();
    private int bricksBrokenSinceLastDrop = 0;

    // ✨ Thêm trạng thái điều khiển ngược
    private boolean controlsReversed = false;

    // ✨ THÊM TRẠNG THÁI LÁ CHẮN (SHIELD)
    private boolean isShieldActive = false;

    public List<PowerUp> getFallingPowerUps() {
        return fallingPowerUps;
    }

    private final Map<String, PauseTransition> activeEffects = new HashMap<>();

    public void applyPowerUp(PowerUp p) {
        // ✨ LOGIC NGĂN CHẶN CỘNG DỒN CHO SHIELD
        if (p.getType().equals("Shield") && isShieldActive) {
            return; // Đã có Shield, không làm gì cả.
        }

        // Nếu loại này đang có hiệu ứng — reset lại timer
        if (activeEffects.containsKey(p.getType())) {
            activeEffects.get(p.getType()).stop();
        }

        // Áp dụng hiệu ứng
        p.applyEffect(this);

        // Tạo timer để remove sau duration
        // Shield có duration lớn, timer này sẽ không bao giờ hết hạn tự động
        PauseTransition timer =
                new PauseTransition(Duration.seconds(p.getDuration()));
        timer.setOnFinished(e -> {
            p.removeEffect(this);
            activeEffects.remove(p.getType());
        });

        // Nếu không phải Shield, chạy timer
        // (Nếu là Shield, timer này chỉ cần được lưu để hủy khi reset game)
        if (!p.getType().equals("Shield")) {
            timer.play();
        }

        // Ghi nhớ hiệu ứng đang hoạt động
        activeEffects.put(p.getType(), timer);
    }


    public GameManager(BrickDisplay brickDisplay, Paddle paddle, Ball ball) {
        this.brickDisplay = brickDisplay;
        this.paddle = paddle;
        this.paddle.setGameManager(this);
        this.ball = ball;
        brickGroup = brickDisplay.getBrickDisplay();
    }

    // Trả về thành phần để GamePanel add vào scene
    public Paddle getPaddle() { return paddle; }
    public Ball getBall() { return ball; }
    public Group getBrickGroup() { return brickGroup; }

    public int getScore() { return score; }
    public int getLives() { return lives; }

    public void setBrickGroup(Group g) { brickGroup = g; }

    // ✨ Setter cho trạng thái điều khiển ngược
    public void setControlsReversed(boolean reversed) {
        this.controlsReversed = reversed;
    }

    // ✨ Getter cho trạng thái điều khiển ngược (cần thiết cho Paddle hoặc GamePanel)
    public boolean isControlsReversed() {
        return controlsReversed;
    }

    // ✨ SETTER VÀ GETTER MỚI CHO SHIELD (ĐÃ KHẮC PHỤC LỖI)
    public void setShieldActive(boolean active) {
        this.isShieldActive = active;
        // Logic hiển thị/ẩn thanh chắn có thể được đặt ở đây hoặc trong GamePanel
    }

    public boolean hasShield() {
        return isShieldActive;
    }


    // Cập nhật logic va chạm giữa ball và bricks/paddle, tính điểm, mạng
    public void update(double deltaTime) {
        // Logic update có thể được mở rộng ở đây
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
                    bricksBrokenSinceLastDrop++;

                    if (bricksBrokenSinceLastDrop > 15 && random.nextDouble() < 0.07) {
                        double bx = brick.getX();
                        double by = brick.getY();
                        PowerUp powerUp;

                        // ✨ CẬP NHẬT LOGIC DROP POWER-UP (Bây giờ có 4 loại):
                        // Sử dụng random.nextInt(4) để chọn ngẫu nhiên giữa 4 loại PowerUp.
                        int powerUpType = random.nextInt(4); // 0, 1, 2, hoặc 3

                        Image img;
                        switch (powerUpType) {
                            case 0:
                                // ExpandPaddlePowerUp
                                img = brickDisplay.getExpandPowerUpImage();
                                powerUp = new ExpandPaddlePowerUp(bx, by, img);
                                break;
                            case 1:
                                // FastBallPowerUp
                                img = brickDisplay.getFastBallPowerUpImage();
                                powerUp = new FastBallPowerUp(bx, by, img);
                                break;
                            case 2:
                                // ReverseControlsPowerUp
                                img = brickDisplay.getReverseControlsPowerUpImage();
                                powerUp = new ReverseControlsPowerUp(bx, by, img);
                                break;
                            case 3:
                            default:
                                // ✨ ShieldPowerUp MỚI
                                img = brickDisplay.getShieldPowerUpImage(); // Cần đảm bảo BrickDisplay có getter này
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

    // Khi bóng rơi
    public void ballDropped(Runnable onGameOver) {
        // ✨ LOGIC MỚI: KIỂM TRA LÁ CHẮN TRƯỚC KHI GIẢM MẠNG
        if (isShieldActive) {
            setShieldActive(false); // Vô hiệu hóa shield sau khi đỡ
            ball.resetPosition();   // Đưa bóng về vị trí ban đầu
            // Không giảm lives
            return;
        }

        // Logic cũ (nếu không có shield)
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
        // ✨ Đảm bảo hiệu ứng bị hủy khi reset
        controlsReversed = false;
        isShieldActive = false; // Reset trạng thái Shield
        activeEffects.values().forEach(PauseTransition::stop);
        activeEffects.clear();
    }

    public BrickDisplay getBrickDisplay() {
        return brickDisplay;
    }


}