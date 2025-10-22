package graphics;

import constants.GameConfig;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Iterator;

public class Game {
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean gameOver = false;

    private int lives = 3;
    private int score = 0;
    private ImageView[] hearts = new ImageView[3];
    private Text scoreText;
    private Line divider; // thêm biến thanh kẻ ngang

    public void show(Stage stage) {

        Image backgroundImage = new Image(getClass().getResource("/image/background.jpg").toExternalForm());
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(GameConfig.WINDOW_WIDTH);
        backgroundView.setFitHeight(GameConfig.WINDOW_HEIGHT);

        BrickDisplay brickDisplay = new BrickDisplay();
        brickDisplay.setPattern(Patterns.LevelPatterns.getPattern(1));
        Group brickGroup = brickDisplay.getBrickDisplay();

        Group root = new Group();
        root.getChildren().add(backgroundView);
        root.getChildren().add(brickGroup);

        Image paddleImg = new Image(getClass().getResource("/image/paddle.png").toExternalForm());
        Image ballImg = new Image(getClass().getResource("/image/ball2.png").toExternalForm());

        Paddle paddle = new Paddle(
                paddleImg,
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.PADDLE_WIDTH / 2.0,
                GameConfig.WINDOW_HEIGHT - 50,
                GameConfig.PADDLE_WIDTH,
                GameConfig.PADDLE_HEIGHT
        );

        Ball ball = new Ball(
                ballImg,
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.BALL_SIZE / 2.0,
                GameConfig.WINDOW_HEIGHT - 80,
                GameConfig.BALL_SIZE
        );

        root.getChildren().addAll(paddle.getPaddleView(), ball.getBallView());

        // Hiển thị số mạng (trái tim)
        Image heartImage = new Image(getClass().getResource("/image/heart.png").toExternalForm());
        for (int i = 0; i < lives; i++) {
            hearts[i] = new ImageView(heartImage);
            hearts[i].setFitWidth(30);
            hearts[i].setFitHeight(30);
            hearts[i].setX(20 + i * 35);
            hearts[i].setY(10);
            root.getChildren().add(hearts[i]);
        }

        // Điểm
        scoreText = new Text("Point: 0");
        scoreText.setFont(Font.font("Arial", 20));
        scoreText.setFill(Color.WHITE);
        scoreText.setX(GameConfig.WINDOW_WIDTH - 130);
        scoreText.setY(35);
        root.getChildren().add(scoreText);

        // Thanh kẻ ngang
        divider = new Line(0, 50, GameConfig.WINDOW_WIDTH, 50);
        divider.setStroke(Color.WHITE);
        divider.setStrokeWidth(2);
        root.getChildren().add(divider);

        Scene scene = new Scene(root, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) leftPressed = true;
            if (e.getCode() == KeyCode.RIGHT) rightPressed = true;
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) leftPressed = false;
            if (e.getCode() == KeyCode.RIGHT) rightPressed = false;
        });

        scene.setOnMouseClicked(e -> root.requestFocus());
        stage.setTitle("baiTapLonAnhEmBoTuc");
        stage.setScene(scene);
        stage.show();

        root.requestFocus();

        new AnimationTimer() {
            private long lastTime = System.nanoTime();

            @Override
            public void handle(long now) {
                if (gameOver) return;

                double deltaTime = (now - lastTime) / 1e9;
                if (deltaTime > 0.05) deltaTime = 0.05;
                lastTime = now;

                if (leftPressed) paddle.moveLeft(deltaTime);
                if (rightPressed) paddle.moveRight(deltaTime, GameConfig.WINDOW_WIDTH);

                ball.move();

                // Va chạm biên trái/phải/trên
                if (ball.getX() <= 0 || ball.getX() + ball.getWidth() >= GameConfig.WINDOW_WIDTH)
                    ball.reverseX();
                if (ball.getY() <= 0)
                    ball.reverseY();

                // Va chạm với thanh kẻ ngang
                if (ball.getY() <= divider.getEndY() && ball.getY() >= divider.getEndY() - 5) {
                    ball.reverseY();
                }

                // Va chạm paddle
                if (ball.getBallView().getBoundsInParent().intersects(paddle.getPaddleView().getBoundsInParent())) {
                    if (ball.getY() + ball.getHeight() <= paddle.getY() + 10) {
                        ball.reverseY();
                        ball.getBallView().setY(paddle.getY() - ball.getHeight() - 1);
                    }
                }

                // Bóng rơi -> mất mạng
                if (ball.getY() > GameConfig.WINDOW_HEIGHT) {
                    lives--;
                    root.getChildren().remove(hearts[lives]);

                    if (lives <= 0) {
                        gameOver = true;
                        showGameOverMenu(root, stage);
                    } else {
                        ball.getBallView().setX(GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.BALL_SIZE / 2.0);
                        ball.getBallView().setY(GameConfig.WINDOW_HEIGHT - 80);
                        ball.reverseY();
                    }
                }

                // Va chạm gạch
                Iterator<javafx.scene.Node> it = brickGroup.getChildren().iterator();
                while (it.hasNext()) {
                    javafx.scene.Node node = it.next();
                    if (node instanceof ImageView brick &&
                            node != paddle.getPaddleView() &&
                            node != ball.getBallView()) {
                        if (ball.getBallView().getBoundsInParent().intersects(brick.getBoundsInParent())) {
                            it.remove();
                            ball.reverseY();
                            score += 10;
                            scoreText.setText("Point: " + score);

                            if (brickGroup.getChildren().isEmpty()) {
                                gameOver = true;
                                showGameOverMenu(root, stage);
                            }
                            break;
                        }
                    }
                }
            }
        }.start();
    }

    // menu Game Over
    private void showGameOverMenu(Group root, Stage stage) {
        // nền mờ
        javafx.scene.shape.Rectangle overlay = new javafx.scene.shape.Rectangle(
                0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        overlay.setFill(Color.rgb(0, 0, 0, 0.6));
        root.getChildren().add(overlay);

        // hiện game over
        Text overText = new Text("GAME OVER");
        overText.setFont(Font.font("Arial", 48));
        overText.setFill(Color.RED);
        overText.setX(GameConfig.WINDOW_WIDTH / 2.0 - 150);
        overText.setY(GameConfig.WINDOW_HEIGHT / 2.0 - 50);

        // điểm
        Text finalScore = new Text("Your score: " + score);
        finalScore.setFont(Font.font("Arial", 28));
        finalScore.setFill(Color.WHITE);
        finalScore.setX(GameConfig.WINDOW_WIDTH / 2.0 - 100);
        finalScore.setY(GameConfig.WINDOW_HEIGHT / 2.0);

        // exit khỏi game
        Button exitBtn = new Button("Exit");
        exitBtn.setLayoutX(GameConfig.WINDOW_WIDTH / 2.0 - 130);
        exitBtn.setLayoutY(GameConfig.WINDOW_HEIGHT / 2.0 + 50);
        exitBtn.setPrefWidth(100);
        exitBtn.setOnAction(e -> stage.close());

        // mhinh chính
        Button restartBtn = new Button("Return to Menu");
        restartBtn.setLayoutX(GameConfig.WINDOW_WIDTH / 2.0 + 20);
        restartBtn.setLayoutY(GameConfig.WINDOW_HEIGHT / 2.0 + 50);
        restartBtn.setPrefWidth(150);
        restartBtn.setOnAction(e -> Menu.show(stage));

        root.getChildren().addAll(overText, finalScore, exitBtn, restartBtn);
    }
}
