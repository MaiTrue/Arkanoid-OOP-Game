package graphics;

import constants.GameConfig;
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
import Patterns.PikachuPattern;

import java.util.Iterator;

public class GamePanel extends Pane {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Paddle paddle;
    private final Ball ball;
    private final BrickDisplay brickDisplay;
    private Group brickGroup;
    private boolean gameOver = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private long lastFrameTime = 0;
    private int lives = 3;
    private int score = 0;
    private ImageView[] hearts = new ImageView[3];
    private Text scoreText;
    private Line divider;
    private Button restartButton;
    private Button returnButton;
    private javafx.scene.shape.Rectangle overlay;


    public GamePanel() {
        this.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        // Ảnh nền
        Image bg = new Image(getClass().getResource("/image/background.jpg").toExternalForm());
        ImageView bgView = new ImageView(bg);
        bgView.setFitWidth(GameConfig.WINDOW_WIDTH);
        bgView.setFitHeight(GameConfig.WINDOW_HEIGHT);
        this.getChildren().add(bgView);

        // Canvas (để vẽ text hoặc hiệu ứng)
        canvas = new Canvas(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        canvas.setMouseTransparent(true);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);

        // Overlay mờ khi Game Over
        overlay = new javafx.scene.shape.Rectangle(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        overlay.setFill(Color.rgb(0, 0, 0, 0.6));
        overlay.setVisible(false);
        this.getChildren().add(overlay);

        // Hiển thị gạch
        brickDisplay = new BrickDisplay();
        brickDisplay.setPattern(PikachuPattern.DATA);
        brickGroup = brickDisplay.getBrickDisplay();
        this.getChildren().add(brickGroup);

        // Paddle và Ball
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

        this.getChildren().addAll(paddle.getPaddleView(), ball.getBallView());

        // Hiển thị trái tim (mạng)
        Image heartImage = new Image(getClass().getResource("/image/heart.png").toExternalForm());
        for (int i = 0; i < lives; i++) {
            hearts[i] = new ImageView(heartImage);
            hearts[i].setFitWidth(30);
            hearts[i].setFitHeight(30);
            hearts[i].setX(20 + i * 35);
            hearts[i].setY(10);
            this.getChildren().add(hearts[i]);
        }

        // Hiển thị điểm
        scoreText = new Text("Point: 0");
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

    // Nút restart và return
    private void setupButtons() {
        restartButton = new Button("Restart");
        restartButton.setFont(new Font("Arial", 18));
        restartButton.setLayoutX(GameConfig.WINDOW_WIDTH / 2.0 - 120);
        restartButton.setLayoutY(GameConfig.WINDOW_HEIGHT / 2.0 + 40);
        restartButton.setVisible(false);
        restartButton.setOnAction(e -> restartGame());

        returnButton = new Button("Return to Menu");
        returnButton.setFont(new Font("Arial", 18));
        returnButton.setLayoutX(GameConfig.WINDOW_WIDTH / 2.0 - 10);
        returnButton.setLayoutY(GameConfig.WINDOW_HEIGHT / 2.0 + 40);
        returnButton.setVisible(false);
        returnButton.setOnAction(e -> Menu.show((javafx.stage.Stage) this.getScene().getWindow()));

        this.getChildren().addAll(restartButton, returnButton);
        this.getChildren().remove(canvas);
        this.getChildren().add(canvas);
    }

    // Phím điều khiển trái/phải
    private void setupControls() {
        this.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT -> leftPressed = true;
                case RIGHT -> rightPressed = true;
            }
        });
        this.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case LEFT -> leftPressed = false;
                case RIGHT -> rightPressed = false;
            }
        });
    }

    // Vòng lặp game
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

    // Logic chính của game
    private void update(double deltaTime) {
        gc.clearRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        if (leftPressed) paddle.moveLeft(deltaTime);
        if (rightPressed) paddle.moveRight(deltaTime, GameConfig.WINDOW_WIDTH);

        ball.move();

        // Va chạm biên
        if (ball.getX() <= 0 || ball.getX() + ball.getWidth() >= GameConfig.WINDOW_WIDTH)
            ball.reverseX();
        if (ball.getY() <= 0)
            ball.reverseY();

        // Va chạm paddle
        if (ball.hitPaddle(paddle)) {
            ball.reverseY();
            ball.getBallView().setY(paddle.getY() - ball.getHeight() - 1);
        }

        // Va chạm gạch
        Iterator<javafx.scene.Node> it = brickGroup.getChildren().iterator();
        while (it.hasNext()) {
            javafx.scene.Node node = it.next();
            if (node instanceof ImageView brick) {
                Bounds brickBounds = brick.getBoundsInParent();
                if (ball.getBallView().getBoundsInParent().intersects(brickBounds)) {
                    it.remove();
                    ball.reverseY();
                    score += 10;
                    scoreText.setText("Point: " + score);

                    if (brickGroup.getChildren().isEmpty()) {
                        gameOver = true;
                        showGameOver();
                    }
                    break;
                }
            }
        }

        // Bóng rơi -> mất mạng
        if (ball.getY() > GameConfig.WINDOW_HEIGHT) {
            lives--;
            this.getChildren().remove(hearts[lives]);

            if (lives <= 0) {
                gameOver = true;
                showGameOver();
            } else {
                ball.getBallView().setX(GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.BALL_SIZE / 2.0);
                ball.getBallView().setY(GameConfig.WINDOW_HEIGHT - 100);
                ball.reverseY();
            }
        }
    }

    // Hiện GAME OVER
    private void showGameOver() {
        overlay.setVisible(true);

        gc.setFill(Color.RED);
        gc.setFont(new Font("Arial", 48));
        gc.fillText("GAME OVER", GameConfig.WINDOW_WIDTH / 2.0 - 150, GameConfig.WINDOW_HEIGHT / 2.0 - 40);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 28));
        gc.fillText("Your score: " + score, GameConfig.WINDOW_WIDTH / 2.0 - 100, GameConfig.WINDOW_HEIGHT / 2.0);

        restartButton.setVisible(true);
        returnButton.setVisible(true);
    }

    // Reset toàn bộ game
    private void restartGame() {
        gameOver = false;
        restartButton.setVisible(false);
        returnButton.setVisible(false);
        score = 0;
        lives = 3;
        scoreText.setText("Point: 0");

        // Xóa tim cũ và thêm lại
        this.getChildren().removeIf(node -> node instanceof ImageView && node != paddle.getPaddleView() && node != ball.getBallView());
        Image heartImage = new Image(getClass().getResource("/image/heart.png").toExternalForm());
        for (int i = 0; i < lives; i++) {
            hearts[i] = new ImageView(heartImage);
            hearts[i].setFitWidth(30);
            hearts[i].setFitHeight(30);
            hearts[i].setX(20 + i * 35);
            hearts[i].setY(10);
            this.getChildren().add(hearts[i]);
        }

        // Reset bóng & paddle
        ball.getBallView().setX(GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.BALL_SIZE / 2.0);
        ball.getBallView().setY(GameConfig.WINDOW_HEIGHT - 100);
        paddle.getPaddleView().setX(GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.PADDLE_WIDTH / 2.0);

        // Reset gạch
        this.getChildren().remove(brickGroup);
        brickGroup = brickDisplay.resetBricks();
        this.getChildren().add(1, brickGroup);
    }

    public void show(Stage stage) {
        Scene scene = new Scene(this, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();

        this.requestFocus();
    }
}
