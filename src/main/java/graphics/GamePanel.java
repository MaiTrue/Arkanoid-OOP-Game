package graphics;

import constants.GameConfig;
import core.GameManager;
import core.GameRecord;
import core.LeaderboardManager;
import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import Patterns.PikachuPattern;
import java.util.Iterator;
import javafx.application.Platform;
import javafx.animation.Timeline;


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
    private AnimationTimer gameTimer;
    private long startTimeNano;
    public LeaderboardManager leaderboardManager;
    private boolean isWin = false; // Cờ check trạng thái thắng
    // --------------------------------------------------------

    /**
     * Constructor chính: cho phép truyền pattern cho BrickDisplay.
     * Subclasses (Level panels) nên gọi super(pattern).
     */
    public GamePanel(int[][] pattern, int level) {
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
            } else if (ball.getX() + ball.getWidth() >= GameConfig.WINDOW_WIDTH) {
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

        // 🌟 1. DỌN DẸP MÀN HÌNH 🌟
        this.getChildren().removeIf(node ->
                node != canvas && node != restartButton && node != returnButton && node != scoreText && node != background
        );
        manager.getFallingPowerUps().clear();

        if (!this.getChildren().contains(background)) {
            this.getChildren().add(0, background);
        }

        // 🌟 2. TẠO VÀ THÊM OVERLAY (StackPane) 🌟
        StackPane overlay = new StackPane();
        overlay.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        this.getChildren().add(1, overlay);

        long endTimeNano = System.nanoTime();
        long timeElapsedSeconds = (endTimeNano - startTimeNano) / 1_000_000_000;

        // KHAI BÁO CÁC BIẾN CẦN THIẾT (Chỉ khai báo một lần tại đây)
        String resultText = isWin ? "YOU WIN!" : "GAME OVER";
        String formattedTime = String.format("%02d:%02d:%02d",
                timeElapsedSeconds / 3600, (timeElapsedSeconds % 3600) / 60, timeElapsedSeconds % 60);

        // Xóa Canvas để chuẩn bị vẽ tiêu đề lớn
        gc.clearRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        // Vẽ Tiêu đề lớn GAME OVER/YOU WIN lên Canvas
        gc.setFill(isWin ? Color.LIMEGREEN : Color.RED);
        gc.setFont(Font.font("Arial", 48));
        gc.fillText(resultText, GameConfig.WINDOW_WIDTH / 2.0 - 150, GameConfig.WINDOW_HEIGHT / 2.0 - 200);

        // --------------------------------------------------------
        // 🌟 3. TẠO VÀ HIỂN THỊ CÁC THÀNH PHẦN INLINE (Sử dụng VBox) 🌟
        // --------------------------------------------------------

        // VBox chứa thông tin và ô nhập liệu
        VBox resultInputBox = new VBox(15);
        resultInputBox.setAlignment(Pos.CENTER);
        resultInputBox.setTranslateY(-50); // Dịch lên trên để tránh nút Restart/Return

        // 3a. Label kết quả
        Label scoreLabel = new Label("Your score: " + manager.getScore());
        Label timeLabel = new Label("Time: " + formattedTime);

        scoreLabel.setFont(Font.font("Arial", 28));
        timeLabel.setFont(Font.font("Arial", 28));
        scoreLabel.setTextFill(Color.WHITE);
        timeLabel.setTextFill(Color.WHITE);

        // 3b. HBox chứa ô nhập tên
        TextField nameInput = new TextField();
        nameInput.setPromptText("Enter your name...");
        nameInput.setMaxWidth(250);
        nameInput.setFont(Font.font("Arial", 16));

        Label promptLabel = new Label("Enter your name:");
        promptLabel.setFont(Font.font("Arial", 18));
        promptLabel.setTextFill(Color.WHITE);

        HBox inputRow = new HBox(10, promptLabel, nameInput);
        inputRow.setAlignment(Pos.CENTER);

        // 3c. Nút SAVE
        Button saveButton = new Button("SAVE SCORE");
        saveButton.setFont(Font.font("Arial", 18));
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        // Thêm tất cả vào VBox
        resultInputBox.getChildren().addAll(scoreLabel, timeLabel, inputRow, saveButton);
        overlay.getChildren().add(resultInputBox); // Thêm VBox vào StackPane Overlay

        // 🌟 4. LOGIC LƯU ĐIỂM (Gắn vào nút SAVE) 🌟
        saveButton.setOnAction(e -> {
            String playerName = nameInput.getText().trim();
            if (playerName.isEmpty()) {
                playerName = "Player_" + (System.currentTimeMillis() % 1000);
            }

            // Ghi nhận GameRecord
            GameRecord record = new GameRecord(playerName, manager.getScore(), timeElapsedSeconds);
            leaderboardManager.addRecord(record);

            // --- XÓA VÀ VẼ LẠI NỘI DUNG TĨNH ---
            overlay.getChildren().remove(resultInputBox);
            gc.clearRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT); // Xóa Canvas cũ

            // Lấy lại thời gian định dạng từ record vừa lưu
            String finalFormattedTime = record.getFormattedTime(); // Dùng biến mới để tránh lỗi

            // 1. Vẽ Tiêu đề lớn GAME OVER/YOU WIN
            gc.setFill(isWin ? Color.LIMEGREEN : Color.RED);
            gc.setFont(Font.font("Arial", 48));
            gc.fillText(resultText, GameConfig.WINDOW_WIDTH / 2.0 - 150, GameConfig.WINDOW_HEIGHT / 2.0 - 200);

            // 2. Vẽ thông tin chi tiết (Player Name, Score, Time)
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 24));

            double startY = GameConfig.WINDOW_HEIGHT / 2.0 - 140;
            double offsetX = GameConfig.WINDOW_WIDTH / 2.0 - 150;

            gc.fillText("Player Name: " + playerName, offsetX, startY);

            gc.fillText("Your Score: " + manager.getScore(), offsetX, startY + 40);

            gc.fillText("Time: " + finalFormattedTime, offsetX, startY + 80);


            restartButton.setVisible(true);
            returnButton.setVisible(true);

            saveButton.setDisable(true);
        });
    }


    private void restartGame() {
        gameOver = false;
        isWin = false; // <-- BỔ SUNG: Reset cờ thắng
        restartButton.setVisible(false);
        returnButton.setVisible(false);

        manager.reset();

        // 🌟 Khắc phục: Xóa các node động cũ, bao gồm Overlay (StackPane) 🌟
        this.getChildren().removeIf(node ->
                (node instanceof StackPane && node.getStyle() != null && node.getStyle().contains("rgba(0, 0, 0, 0.8)")) || // Xóa Overlay
                        node != background &&
                                node != canvas &&
                                node != restartButton &&
                                node != returnButton &&
                                node != scoreText
        );
        // Xóa text Game Over/Score cũ trên Canvas
        gc.clearRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        this.getChildren().add(0, background);
        this.getChildren().add(divider);
        brickGroup = manager.getBrickGroup(); // thêm lại brickGroup, paddle, ball
        this.getChildren().addAll(brickGroup,
                manager.getPaddle().getPaddleView(),
                manager.getBall().getBallView());

        Image heartImage = new Image(getClass().getResource("/image/heart.png").toExternalForm());

        hearts = new ImageView[manager.getLives()];
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