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
import javafx.scene.control.ButtonType;



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
    private boolean ballMoving = false;
    private SoundManager soundManager;
    private ImageView background;
    private ImageView backgroundEndView;
    private Timeline timer;
    private AnimationTimer gameTimer;
    private long startTimeNano;
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

        soundManager = new SoundManager();

        if (soundManager != null) {
            soundManager.playBackgroundMusic();
        }



        BrickDisplay brickDisplay = new BrickDisplay();
        brickDisplay.setPattern(pattern);

        Paddle paddle = new Paddle(
                brickDisplay.getPaddleImage(),
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.PADDLE_WIDTH / 2.0,
                GameConfig.WINDOW_HEIGHT - 60,
                GameConfig.PADDLE_WIDTH,
                GameConfig.PADDLE_HEIGHT
        );

        Ball ball = new Ball(
                brickDisplay.getBallImage(),
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.BALL_SIZE / 2.0,
                GameConfig.WINDOW_HEIGHT - 100,
                GameConfig.BALL_SIZE
        );

        manager = new GameManager(brickDisplay, paddle, ball);

        brickGroup = manager.getBrickGroup();
        this.getChildren().add(brickGroup);

        this.getChildren().addAll(paddle.getPaddleView(), ball.getBallView());

        Image heartImage = new Image(getClass().getResource("/image/heart.png").toExternalForm());
        for (int i = 0; i < manager.getLives(); i++) {
            hearts[i] = new ImageView(heartImage);
            hearts[i].setFitWidth(30);
            hearts[i].setFitHeight(30);
            hearts[i].setX(20 + i * 35);
            hearts[i].setY(10);
            this.getChildren().add(hearts[i]);
        }

        scoreText = new Text("Point: " + manager.getScore());
        scoreText.setFont(Font.font("Arial", 20));
        scoreText.setFill(Color.WHITE);
        scoreText.setX(GameConfig.WINDOW_WIDTH - 130);
        scoreText.setY(35);
        this.getChildren().add(scoreText);

        divider = new Line(0, 50, GameConfig.WINDOW_WIDTH, 50);
        divider.setStroke(Color.WHITE);
        divider.setStrokeWidth(2);
        this.getChildren().add(divider);

        setupButtons();
        setupControls();

        this.setFocusTraversable(true);
        this.requestFocus();
        this.leaderboardManager = LeaderboardManager.getInstance();
        this.startTimeNano = System.nanoTime();

        startGameLoop();
    }


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
        gameTimer = new AnimationTimer() { // <-- Gán vào biến thành viên
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
        gameTimer.start(); // <-- Dùng gameTimer
    }

    private void update(double deltaTime) {
        gc.clearRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        Paddle paddle = manager.getPaddle();
        Ball ball = manager.getBall();

        paddle.move(deltaTime, leftPressed, rightPressed, GameConfig.WINDOW_WIDTH);

        if (ballMoving) {
            ball.move(deltaTime);

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

            if (ball.getY() <= 50) {
                ball.reverseY();
                ball.getBallView().setY(50);
                if (soundManager != null) soundManager.playCollisionSound();
            }

            if (ball.hitPaddle(paddle)) {
                ball.reverseY();
                ball.getBallView().setY(paddle.getY() - ball.getHeight() - 1);
                if (soundManager != null) soundManager.playCollisionSound();
            }


            manager.handleCollisions(() -> {

                scoreText.setText("Point: " + manager.getScore());
                if (soundManager != null) soundManager.playBrickHitSound();
            }, () -> {

                gameOver = true;
                isWin = true;
                showGameOver();
            });


            if (ball.getY() > GameConfig.WINDOW_HEIGHT) {
                if (soundManager != null) soundManager.playDieSound();


                int livesLeft = manager.getLives();
                manager.ballDropped(() -> {

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

        Iterator<PowerUp> iterator = manager.getFallingPowerUps().iterator();
        while (iterator.hasNext()) {
            PowerUp p = iterator.next();


            if (!this.getChildren().contains(p.getImageView())) {
                this.getChildren().add(p.getImageView());
            }


            p.update(deltaTime);


            Bounds powerBounds = p.getImageView().getBoundsInParent();
            Bounds paddleBounds = manager.getPaddle().getPaddleView().getBoundsInParent();
            if (powerBounds.intersects(paddleBounds)) {
                if (soundManager != null) soundManager.playPowerUpSound();
                manager.applyPowerUp(p);
                this.getChildren().remove(p.getImageView());
                iterator.remove();
                continue;
            }

            if (p.getY() > GameConfig.WINDOW_HEIGHT) {
                this.getChildren().remove(p.getImageView());
                iterator.remove();
            }
        }
    }

    private void showGameOver() {
        if (gameTimer != null) gameTimer.stop();

        // 🌟 BỎ backgroundEndView, CHỈ GIỮ LẠI background GỐC 🌟
        this.getChildren().removeIf(node ->
                node != canvas && node != restartButton && node != returnButton && node != scoreText && node != background
        );
        manager.getFallingPowerUps().clear();

        // Đảm bảo background GỐC được thêm vào
        if (!this.getChildren().contains(background)) {
            this.getChildren().add(0, background);
        }

        // 🌟 BƯỚC MỚI: TẠO OVERLAY NỬA TRONG SUỐT 🌟
        javafx.scene.layout.StackPane overlay = new javafx.scene.layout.StackPane();
        overlay.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        // Nền đen trong suốt (RGBA: Đen 80% độ mờ)
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

        // Thêm overlay lên trên background nhưng dưới các thành phần UI khác
        this.getChildren().add(1, overlay);

        long endTimeNano = System.nanoTime();
        long timeElapsedSeconds = (endTimeNano - startTimeNano) / 1_000_000_000;
        String resultText = isWin ? "YOU WIN!" : "GAME OVER";
        String defaultPlayerName = "Player";

        Platform.runLater(() -> {
            String formattedTime = String.format("%02d:%02d:%02d",
                    timeElapsedSeconds / 3600, (timeElapsedSeconds % 3600) / 60, timeElapsedSeconds % 60);

            Stage currentStage = (Stage) this.getScene().getWindow();

            // Định nghĩa ButtonType SAVE
            ButtonType saveButton = new ButtonType("SAVE", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);

            TextInputDialog dialog = new TextInputDialog(defaultPlayerName);
            dialog.initOwner(currentStage);
            dialog.setTitle(resultText + " - Save Score");
            dialog.setHeaderText("Kết quả của bạn:\nĐiểm: " + manager.getScore() + "\nThời gian: " + formattedTime);
            dialog.setContentText("Nhập tên người chơi:");

            // 🌟 Thay thế nút OK bằng SAVE (theo yêu cầu trước) 🌟
            dialog.getDialogPane().getButtonTypes().clear();
            dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

            Optional<String> result = dialog.showAndWait();

            // Xóa Overlay sau khi dialog đóng để chuẩn bị cho việc vẽ lại UI
            this.getChildren().remove(overlay);

            if (result.isPresent() && result.get() != null) {
                String playerName = !result.get().trim().isEmpty()
                        ? result.get().trim()
                        : defaultPlayerName + "_" + System.currentTimeMillis() % 1000;

                GameRecord record = new GameRecord(playerName, manager.getScore(), timeElapsedSeconds);
                leaderboardManager.addRecord(record);

                // 1. Vẽ lại Overlay để hiển thị thông tin điểm
                this.getChildren().add(1, overlay); // Thêm lại Overlay

                gc.clearRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT); // Xóa canvas cũ

                gc.setFill(isWin ? Color.LIMEGREEN : Color.RED);
                gc.setFont(new Font("Arial", 48));
                gc.fillText(resultText, GameConfig.WINDOW_WIDTH / 2.0 - 150, GameConfig.WINDOW_HEIGHT / 2.0 - 200);

                gc.setFill(Color.RED); // Màu đỏ theo yêu cầu trước
                gc.setFont(new Font("Arial", 28));
                gc.fillText("Your score: " + manager.getScore(), GameConfig.WINDOW_WIDTH / 2.0 - 100, GameConfig.WINDOW_HEIGHT / 2.0 - 150);
                gc.fillText("Time: " + record.getFormattedTime(), GameConfig.WINDOW_WIDTH / 2.0 - 100, GameConfig.WINDOW_HEIGHT / 2.0 - 110);
                gc.fillText("Saved as: " + playerName, GameConfig.WINDOW_WIDTH / 2.0 - 100, GameConfig.WINDOW_HEIGHT / 2.0 - 70);

                restartButton.setVisible(true);
                returnButton.setVisible(true);
            } else {
                // Nếu người dùng bấm Cancel, chỉ hiển thị màn hình Game Over/You Win cơ bản
                gc.clearRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
                this.getChildren().add(1, overlay);

                gc.setFill(isWin ? Color.LIMEGREEN : Color.RED);
                gc.setFont(new Font("Arial", 48));
                gc.fillText(resultText, GameConfig.WINDOW_WIDTH / 2.0 - 150, GameConfig.WINDOW_HEIGHT / 2.0 - 200);

                restartButton.setVisible(true);
                returnButton.setVisible(true);
            }
        });
    }


    private void restartGame() {
        gameOver = false;
        isWin = false; // <-- BỔ SUNG: Reset cờ thắng
        restartButton.setVisible(false);
        returnButton.setVisible(false);

        manager.reset();

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

        Image heartImage = new Image(getClass().getResource("/image/heart.png").toExternalForm());
        for (int i = 0; i < manager.getLives(); i++) {
            hearts[i] = new ImageView(heartImage);
            hearts[i].setFitWidth(30);
            hearts[i].setFitHeight(30);
            hearts[i].setX(20 + i * 35);
            hearts[i].setY(10);
            this.getChildren().add(hearts[i]);
        }
        scoreText.setText("Point: 0");

        manager.getBall().resetPosition();
        manager.getPaddle().getPaddleView().setX(GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.PADDLE_WIDTH / 2.0);
        manager.getPaddle().getPaddleView().setY(GameConfig.WINDOW_HEIGHT - 60);

        ballMoving = false;
        this.startTimeNano = System.nanoTime();
        this.requestFocus();
        if (gameTimer != null) {
            gameTimer.start();
        }
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