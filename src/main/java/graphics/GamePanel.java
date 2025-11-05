package graphics;

import constants.GameConfig;
import core.GameManager;
import core.GameRecord;
import core.LeaderboardManager;
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
import Patterns.PikachuPattern; // Đảm bảo lớp này tồn tại
import java.util.Iterator;
import javafx.scene.control.TextInputDialog; // <-- Bổ sung
import java.util.Optional;                   // <-- Bổ sung
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;



/**
 * GamePanel: UI layer. Giữ nguyên logic gốc, bây giờ dùng GameManager để lưu trạng thái.
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
    private Timeline timer;

    // --- THUỘC TÍNH BỔ SUNG CHO LEADERBOARD VÀ THỜI GIAN ---
    private long startTimeNano; // Thời gian bắt đầu chơi (nanoseconds)
    public LeaderboardManager leaderboardManager;
    private boolean isWin = false; // Cờ check trạng thái thắng
    // --------------------------------------------------------

    /**
     * Constructor chính: cho phép truyền pattern cho BrickDisplay.
     * Subclasses (Level panels) nên gọi super(pattern).
     */
    public GamePanel(int[][] pattern,int level) {
        this.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        // Ảnh nền
        Image bg = new Image(getClass().getResource("/image/background.jpg").toExternalForm());
        background = new ImageView(bg);
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
        soundManager = new SoundManager(); // Giả định SoundManager tồn tại
        // Sửa lỗi: Đảm bảo SoundManager không null trước khi gọi
        if (soundManager != null) {
            soundManager.playBackgroundMusic();
        }


        // BrickDisplay + GameManager (dùng pattern được truyền vào)
        BrickDisplay brickDisplay = new BrickDisplay(); // Giả định BrickDisplay tồn tại
        brickDisplay.setPattern(pattern);

        Paddle paddle = new Paddle( // Giả định Paddle tồn tại
                brickDisplay.getPaddleImage(),
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.PADDLE_WIDTH / 2.0,
                GameConfig.WINDOW_HEIGHT - 60,
                GameConfig.PADDLE_WIDTH,
                GameConfig.PADDLE_HEIGHT
        );

        Ball ball = new Ball( // Giả định Ball tồn tại
                brickDisplay.getBallImage(),
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.BALL_SIZE / 2.0,
                GameConfig.WINDOW_HEIGHT - 100,
                GameConfig.BALL_SIZE
        );

        manager = new GameManager(brickDisplay, paddle, ball); // Giả định GameManager tồn tại

        brickGroup = manager.getBrickGroup();
        this.getChildren().add(brickGroup);

        // Paddle và Ball lấy từ manager
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

        // --- BỔ SUNG: KHỞI TẠO LEADERBOARD VÀ THỜI GIAN ---
        // SỬA LỖI: Thay thế new LeaderboardManager() bằng LeaderboardManager.getInstance()
        // để truy cập thể hiện Singleton.
        this.leaderboardManager = LeaderboardManager.getInstance();
        this.startTimeNano = System.nanoTime(); // Bắt đầu tính giờ
        // ----------------------------------------------------

        startGameLoop();
    }

    /**
     * Constructor mặc định giữ tương thích trước đây: vẫn load Pikachu nếu không truyền pattern.
     */
    public GamePanel() {
        this(PikachuPattern.DATA, 1);
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
                case SPACE -> {
                    // Cần kiểm tra nếu trò chơi đang kết thúc, không cho phép di chuyển
                    if (!gameOver) {
                        ballMoving = true;
                    }
                }
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

        paddle.move(deltaTime, leftPressed, rightPressed, GameConfig.WINDOW_WIDTH);

        if (ballMoving) {
            ball.move(deltaTime);

            // Va chạm biên
            if (ball.getX() <= 0) {
                ball.reverseX();
                ball.getBallView().setX(0);
                if (soundManager != null) soundManager.playCollisionSound();
            }
            else if (ball.getX() + ball.getWidth() >= GameConfig.WINDOW_WIDTH) {
                ball.reverseX();
                ball.getBallView().setX(GameConfig.WINDOW_WIDTH - ball.getWidth());
                if (soundManager != null) soundManager.playCollisionSound();
            }

            // 3. Va chạm biên TRÊN
            if (ball.getY() <= 50) {
                ball.reverseY();
                ball.getBallView().setY(50);
                if (soundManager != null) soundManager.playCollisionSound();
            }

            // Va chạm paddle
            if (ball.hitPaddle(paddle)) {
                ball.reverseY();
                ball.getBallView().setY(paddle.getY() - ball.getHeight() - 1);
                if (soundManager != null) soundManager.playCollisionSound();
            }

            // Va chạm gạch xài GameManager.handleCollisions
            manager.handleCollisions(() -> {
                // onBrickDestroyed: update điểm text
                scoreText.setText("Point: " + manager.getScore());
                if (soundManager != null) soundManager.playBrickHitSound();
            }, () -> {
                // onAllBricksDestroyed: game over win
                gameOver = true;
                isWin = true; // <-- BỔ SUNG: Đặt cờ thắng
                showGameOver();
            });

            // Bóng rơi -> mất mạng
            if (ball.getY() > GameConfig.WINDOW_HEIGHT) {
                if (soundManager != null) soundManager.playDieSound();

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
        Iterator<PowerUp> iterator = manager.getFallingPowerUps().iterator(); // Giả định PowerUp tồn tại
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
                if (soundManager != null) soundManager.playPowerUpSound();
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
        if (timer != null) timer.stop(); // Dừng vòng lặp vẽ

        this.getChildren().removeIf(node ->
                node != canvas && node != restartButton && node != returnButton && node != scoreText && node != backgroundEndView
        );
        manager.getFallingPowerUps().clear();

        if (!this.getChildren().contains(backgroundEndView)) {
            this.getChildren().add(0, backgroundEndView);
        }

        long endTimeNano = System.nanoTime();
        long timeElapsedSeconds = (endTimeNano - startTimeNano) / 1_000_000_000;
        String resultText = isWin ? "YOU WIN!" : "GAME OVER";
        String defaultPlayerName = "Player";

        Platform.runLater(() -> {
            String formattedTime = String.format("%02d:%02d:%02d",
                    timeElapsedSeconds / 3600, (timeElapsedSeconds % 3600) / 60, timeElapsedSeconds % 60);

            Stage currentStage = (Stage) this.getScene().getWindow();
            TextInputDialog dialog = new TextInputDialog(defaultPlayerName);
            dialog.initOwner(currentStage);
            dialog.setTitle(resultText + " - Save Score");
            dialog.setHeaderText("Kết quả của bạn:\nĐiểm: " + manager.getScore() + "\nThời gian: " + formattedTime);
            dialog.setContentText("Nhập tên người chơi:");

            Optional<String> result = dialog.showAndWait();
            String playerName = result.isPresent() && !result.get().trim().isEmpty()
                    ? result.get().trim()
                    : defaultPlayerName + "_" + System.currentTimeMillis() % 1000;

            GameRecord record = new GameRecord(playerName, manager.getScore(), timeElapsedSeconds);
            leaderboardManager.addRecord(record);

            gc.setFill(isWin ? Color.LIMEGREEN : Color.RED);
            gc.setFont(new Font("Arial", 48));
            gc.fillText(resultText, GameConfig.WINDOW_WIDTH / 2.0 - 150, GameConfig.WINDOW_HEIGHT / 2.0 - 200);

            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 28));
            gc.fillText("Your score: " + manager.getScore(), GameConfig.WINDOW_WIDTH / 2.0 - 100, GameConfig.WINDOW_HEIGHT / 2.0 - 150);
            gc.fillText("Time: " + record.getFormattedTime(), GameConfig.WINDOW_WIDTH / 2.0 - 100, GameConfig.WINDOW_HEIGHT / 2.0 - 110);
            gc.fillText("Saved as: " + playerName, GameConfig.WINDOW_WIDTH / 2.0 - 100, GameConfig.WINDOW_HEIGHT / 2.0 - 70);

            restartButton.setVisible(true);
            returnButton.setVisible(true);
        });
    }


    private void restartGame() {
        gameOver = false;
        isWin = false; // <-- BỔ SUNG: Reset cờ thắng
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

        // --- BỔ SUNG: Reset thời gian
        this.startTimeNano = System.nanoTime();
        // --------------------------

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