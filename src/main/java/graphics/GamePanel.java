package graphics;

import constants.GameConfig;
import javafx.util.Duration;
import core.GameManager;
import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import graphics.SoundManager;
import Patterns.PikachuPattern;

import java.util.Iterator;

/**
 * GamePanel: UI layer. Giữ nguyên logic gốc, bây giờ dùng GameManager để lưu trạng thái.
 * SỬA: bổ sung constructor nhận pattern để các LevelPanel truyền pattern khác nhau.
 */
public class GamePanel extends Pane {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GameManager manager;
    private Group brickGroup;
    private boolean gameOver = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private long lastFrameTime = 0;
    private ImageView[] hearts = new ImageView[3];
    private Text scoreText;
    private Line divider;
    private Button restartButton;
    private Button returnButton;
    private boolean ballMoving = false; // lúc đầu bóng chưa chạy
    private SoundManager soundManager;
    private ImageView background;
    private ImageView backgroundEndView;

    /**
     * Constructor chính: cho phép truyền pattern cho BrickDisplay.
     * Subclasses (Level panels) nên gọi super(pattern).
     */
    public GamePanel(int[][] pattern) {
        this.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        // Ảnh nền
        Image bg = new Image(getClass().getResource("/image/background.jpg").toExternalForm());
        background = new ImageView(bg); // <-- Gán vào biến thành viên 'background'
        background.setFitWidth(GameConfig.WINDOW_WIDTH);
        background.setFitHeight(GameConfig.WINDOW_HEIGHT);
        this.getChildren().add(background);

        // Canvas (để vẽ text hoặc hiệu ứng)
        canvas = new Canvas(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        canvas.setMouseTransparent(true);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);

        // back kết thúc
        Image backgroundEnd = new Image(getClass().getResource("/image/background_end.jpg").toExternalForm());
        backgroundEndView = new ImageView(backgroundEnd);
        backgroundEndView.setFitWidth(GameConfig.WINDOW_WIDTH);
        backgroundEndView.setFitHeight(GameConfig.WINDOW_HEIGHT);

        // Âm thanh
        soundManager = new SoundManager();
        soundManager.playBackgroundMusic();

        // BrickDisplay + GameManager (dùng pattern được truyền vào)
        BrickDisplay brickDisplay = new BrickDisplay();
        brickDisplay.setPattern(pattern);
        manager = new GameManager(brickDisplay);

        brickGroup = manager.getBrickGroup();
        this.getChildren().add(brickGroup);

        // Paddle và Ball lấy từ manager
        Paddle paddle = manager.getPaddle();
        Ball ball = manager.getBall();

        this.getChildren().addAll(paddle.getPaddleView(), ball.getBallView());

        // Hiển thị trái tim (mạng)
        Image heartImage = new Image(getClass().getResource("/image/heart.png").toExternalForm());
        for (int i = 0; i < manager.getLives(); i++) {
            hearts[i] = new ImageView(heartImage);
            hearts[i].setFitWidth(30);
            hearts[i].setFitHeight(30);
            hearts[i].setX(20 + i * 35);
            hearts[i].setY(10);
            this.getChildren().add(hearts[i]);
        }

        // Hiển thị điểm
        scoreText = new Text("Point: " + manager.getScore());
        scoreText.setFont(Font.font("Arial", 20));
        scoreText.setFill(Color.WHITE);
        scoreText.setX(GameConfig.WINDOW_WIDTH - 130);
        scoreText.setY(35);
        this.getChildren().add(scoreText);

        // Thanh kẻ ngang
        divider = new Line(0, 50, GameConfig.WINDOW_WIDTH, 50);
        divider.setStroke(Color.WHITE);
        divider.setStrokeWidth(2);
        this.getChildren().add(divider);

        setupButtons();
        setupControls();

        this.setFocusTraversable(true);
        this.requestFocus();

        startGameLoop();
    }

    /**
     * Constructor mặc định giữ tương thích trước đây: vẫn load Pikachu nếu không truyền pattern.
     */
    public GamePanel() {
        this(PikachuPattern.DATA);
    }

    private void setupButtons() {
        restartButton = new Button("Restart");
        restartButton.setFont(new Font("Arial", 18));
        restartButton.setLayoutX(GameConfig.WINDOW_WIDTH / 2.0 - 120);
        restartButton.setLayoutY(GameConfig.WINDOW_HEIGHT / 2.0 + 170);
        restartButton.setVisible(false);
        restartButton.setOnAction(e -> restartGame());

        returnButton = new Button("Return to Menu");
        returnButton.setFont(new Font("Arial", 18));
        returnButton.setLayoutX(GameConfig.WINDOW_WIDTH / 2.0 - 10);
        returnButton.setLayoutY(GameConfig.WINDOW_HEIGHT / 2.0 + 170);
        returnButton.setVisible(false);
        returnButton.setOnAction(e -> Menu.show((Stage) this.getScene().getWindow()));

        this.getChildren().addAll(restartButton, returnButton);
        this.getChildren().remove(canvas);
        this.getChildren().add(canvas);
    }

    private void setupControls() {
        this.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT -> leftPressed = true;
                case RIGHT -> rightPressed = true;
                case SPACE -> ballMoving = true;
            }
        });
        this.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case LEFT -> leftPressed = false;
                case RIGHT -> rightPressed = false;
            }
        });
    }

    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastFrameTime == 0) {
                    lastFrameTime = now;
                    return;
                }

                double deltaTime = (now - lastFrameTime) / 1e9;
                lastFrameTime = now;

                if (!gameOver) {
                    update(deltaTime);
                }
            }
        };
        timer.start();
    }

    private void update(double deltaTime) {
        gc.clearRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        Paddle paddle = manager.getPaddle();
        Ball ball = manager.getBall();

        if (leftPressed) paddle.moveLeft(deltaTime);
        if (rightPressed) paddle.moveRight(deltaTime, GameConfig.WINDOW_WIDTH);

        if (ballMoving) {
            ball.move();

            // Va chạm biên
            if (ball.getX() <= 0 || ball.getX() + ball.getWidth() >= GameConfig.WINDOW_WIDTH) {
                ball.reverseX();
                soundManager.playCollisionSound();
            }

            if (ball.getY() <= 50) {
                ball.reverseY();
                soundManager.playCollisionSound();
            }

            // Va chạm paddle
            if (ball.hitPaddle(paddle)) {
                ball.reverseY();
                ball.getBallView().setY(paddle.getY() - ball.getHeight() - 1);
                soundManager.playCollisionSound();
            }

            // Va chạm gạch xài GameManager.handleCollisions
            manager.handleCollisions(() -> {
                // onBrickDestroyed: update điểm text
                scoreText.setText("Point: " + manager.getScore());
                soundManager.playBrickHitSound();
            }, () -> {
                // onAllBricksDestroyed: game over win
                gameOver = true;
                showGameOver();
            });

            // Bóng rơi -> mất mạng
            if (ball.getY() > GameConfig.WINDOW_HEIGHT) {
                soundManager.playDieSound();

                // xóa trái tim tương ứng
                int livesLeft = manager.getLives();
                manager.ballDropped(() -> {
                    // onGameOver
                    gameOver = true;
                    showGameOver();
                });

                // cập nhật hearts: remove last heart if exists
                int newLives = manager.getLives();
                if (newLives >= 0 && newLives < hearts.length) {
                    this.getChildren().remove(hearts[newLives]);
                }

                if (!gameOver) {
                    ball.resetPosition();
                    ballMoving = false;
                }
            }
        } else {
            ball.getBallView().setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
            ball.getBallView().setY(paddle.getY() - ball.getHeight() - 1);
        }
        // --- Cập nhật PowerUps ---
        Iterator<PowerUp> iterator = manager.getFallingPowerUps().iterator();
        while (iterator.hasNext()) {
            PowerUp p = iterator.next();

            // Nếu chưa add vào Scene thì add
            if (!this.getChildren().contains(p.getImageView())) {
                this.getChildren().add(p.getImageView());
            }

            // Cập nhật vị trí rơi
            p.update(deltaTime);

            // Nếu chạm paddle
            Bounds powerBounds = p.getImageView().getBoundsInParent();
            Bounds paddleBounds = manager.getPaddle().getPaddleView().getBoundsInParent();
            if (powerBounds.intersects(paddleBounds)) {
                manager.applyPowerUp(p);
                this.getChildren().remove(p.getImageView());
                iterator.remove();
                continue;
            }

            // Nếu rơi khỏi màn hình
            if (p.getY() > GameConfig.WINDOW_HEIGHT) {
                this.getChildren().remove(p.getImageView());
                iterator.remove();
            }
        }

    }

    private void showGameOver() {
        this.getChildren().removeIf(node ->
                node != canvas && node != restartButton && node != returnButton && node != scoreText
        );
        // Clear tất cả PowerUps
        manager.getFallingPowerUps().clear();


        if (!this.getChildren().contains(backgroundEndView)) {
            this.getChildren().add(0, backgroundEndView); // đặt ở dưới cùng
        }

        gc.setFill(Color.RED);
        gc.setFont(new Font("Arial", 48));
        gc.fillText("GAME OVER", GameConfig.WINDOW_WIDTH / 2.0 - 150, GameConfig.WINDOW_HEIGHT / 2.0 - 200);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 28));
        gc.fillText("Your score: " + manager.getScore(), GameConfig.WINDOW_WIDTH / 2.0 - 100, GameConfig.WINDOW_HEIGHT / 2.0 - 150);

        restartButton.setVisible(true);
        returnButton.setVisible(true);
    }

    private void restartGame() {
        gameOver = false;
        restartButton.setVisible(false);
        returnButton.setVisible(false);

        // reset lại manager
        manager.reset();

        // xóa hết mọi thứ trừ canvas (để vẽ text) và các nút
        this.getChildren().removeIf(node ->
                node != background &&
                        node != canvas &&
                        node != restartButton &&
                        node != returnButton &&
                        node != scoreText
        );

        this.getChildren().add(0, background);
        this.getChildren().add(divider);
        brickGroup = manager.getBrickGroup(); // thêm lại brickGroup, paddle, ball
        this.getChildren().addAll(brickGroup,
                manager.getPaddle().getPaddleView(),
                manager.getBall().getBallView());

        // reset mạng (hearts)
        Image heartImage = new Image(getClass().getResource("/image/heart.png").toExternalForm());
        for (int i = 0; i < manager.getLives(); i++) {
            hearts[i] = new ImageView(heartImage);
            hearts[i].setFitWidth(30);
            hearts[i].setFitHeight(30);
            hearts[i].setX(20 + i * 35);
            hearts[i].setY(10);
            this.getChildren().add(hearts[i]);
        }

        // reset score
        scoreText.setText("Point: 0");

        // reset vị trí bóng và paddle
        manager.getBall().resetPosition();
        manager.getPaddle().getPaddleView().setX(GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.PADDLE_WIDTH / 2.0);
        manager.getPaddle().getPaddleView().setY(GameConfig.WINDOW_HEIGHT - 60);

        ballMoving = false; // đợi SPACE để chạy lại

        // Đảm bảo nhận phím trở lại
        this.requestFocus();
    }

    public void show(Stage stage) {
        Scene scene = new Scene(this, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();

        this.requestFocus();
    }

    public GameManager getManager() {
        return manager;
    }
}
