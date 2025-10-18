package graphics;

import constants.GameConfig;
import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import Patterns.PikachuPattern;


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
    private Button restartButton;
    private long lastFrameTime = 0;

    public GamePanel() {
        this.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        canvas = new Canvas(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);

        brickDisplay = new BrickDisplay();
        brickDisplay.setPattern(PikachuPattern.DATA);
        brickGroup = brickDisplay.getBrickDisplay();
        this.getChildren().add(brickGroup);

        paddle = new Paddle(brickDisplay.getPaddleImage(),
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.PADDLE_WIDTH / 2.0,
                GameConfig.WINDOW_HEIGHT - 60,
                GameConfig.PADDLE_WIDTH,
                GameConfig.PADDLE_HEIGHT);

        ball = new Ball(brickDisplay.getBallImage(),
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.BALL_SIZE / 2.0,
                GameConfig.WINDOW_HEIGHT - 100,
                GameConfig.BALL_SIZE);

        this.getChildren().addAll(paddle.getPaddleView(), ball.getBallView());

        setupRestartButton();
        setupControls();

        this.setFocusTraversable(true);
        this.requestFocus();

        startGameLoop();
    }

    // Xử lý phím điều khiển trái/phải
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

    // Nút restart
  private void setupRestartButton() {
        restartButton = new Button("Restart");
        restartButton.setLayoutX(GameConfig.WINDOW_WIDTH / 2.0 - 50);
        restartButton.setLayoutY(GameConfig.WINDOW_HEIGHT / 2.0 + 50);
        restartButton.setFont(new Font("Arial", 18));
        restartButton.setVisible(false);
        restartButton.setOnAction(e -> restartGame());
        this.getChildren().add(restartButton);
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

                double deltaTime = (now - lastFrameTime) / 1e9; // giây
                lastFrameTime = now;

                if (!gameOver) {
                    update(deltaTime);
                }
            }
        };
        timer.start();
    }

    // Hàm cập nhật logic game
    private void update(double deltaTime) {
        gc.clearRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        // Di chuyển paddle theo phím
        if (leftPressed) paddle.moveLeft(deltaTime);
        if (rightPressed) paddle.moveRight(deltaTime, GameConfig.WINDOW_WIDTH);

        // Cập nhật vị trí bóng
        ball.move();

        // Va chạm biên
        if (ball.getX() <= 0 || ball.getX() + ball.getWidth() >= GameConfig.WINDOW_WIDTH)
            ball.reverseX();
        if (ball.getY() <= 0)
            ball.reverseY();

        // Va chạm paddle (chỉ phản xạ nếu chạm mặt trên)
        if (ball.hitPaddle(paddle)) {
            ball.reverseY();
            ball.getBallView().setY(paddle.getY() - ball.getHeight() - 1);
        }

        // Va chạm gạch
        for (var node : brickGroup.getChildren()) {
            if (node instanceof javafx.scene.image.ImageView brickView) {
                if (ball.getBallView().getBoundsInParent().intersects(brickView.getBoundsInParent())) {
                    brickGroup.getChildren().remove(brickView);
                    ball.reverseY();
                    break;
                }
            }
        }

        // Bóng rơi xuống dưới đáy -> Game over
        if (ball.getY() > GameConfig.WINDOW_HEIGHT) {
            gameOver = true;
            showGameOver();
        }
    }

    // Hiển thị thông báo thua
    private void showGameOver() {
        gc.setFill(Color.RED);
        gc.setFont(new Font("Arial", 36));
        gc.fillText("GAME OVER", GameConfig.WINDOW_WIDTH / 2.0 - 120, GameConfig.WINDOW_HEIGHT / 2.0);
        restartButton.setVisible(true);
    }

    // Reset toàn bộ trạng thái
    private void restartGame() {
        gameOver = false;
        restartButton.setVisible(false);
        this.getChildren().remove(brickGroup);

        // Tạo lại gạch mới
        brickGroup = brickDisplay.resetBricks();
        this.getChildren().add(0, brickGroup);


    }
}
