package graphics;

import constants.GameConfig;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.Iterator;

public class Game {
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean gameOver = false;

    public void show(Stage stage) {
        //  Load background (đặt dưới cùng)
        Image backgroundImage = new Image(getClass().getResource("/image/background.jpg").toExternalForm());
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(GameConfig.WINDOW_WIDTH);
        backgroundView.setFitHeight(GameConfig.WINDOW_HEIGHT);

        //  Tạo group chứa nền + gạch
        BrickDisplay brickDisplay = new BrickDisplay();
        brickDisplay.setPattern(Patterns.LevelPatterns.getPattern(1));
        Group brickGroup = brickDisplay.getBrickDisplay();

        //  Group root chứa toàn bộ đối tượng trong game
        Group root = new Group();
        root.getChildren().add(backgroundView); // nền luôn nằm dưới cùng
        root.getChildren().add(brickGroup);     // gạch chồng lên nền

        //  Ảnh paddle và bóng
        Image paddleImg = new Image(getClass().getResource("/image/paddle.png").toExternalForm());
        Image ballImg = new Image(getClass().getResource("/image/ball2.png").toExternalForm());

        //  Paddle
        Paddle paddle = new Paddle(
                paddleImg,
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.PADDLE_WIDTH / 2.0,
                GameConfig.WINDOW_HEIGHT - 50,
                GameConfig.PADDLE_WIDTH,
                GameConfig.PADDLE_HEIGHT
        );

        //  Ball
        Ball ball = new Ball(
                ballImg,
                GameConfig.WINDOW_WIDTH / 2.0 - GameConfig.BALL_SIZE / 2.0,
                GameConfig.WINDOW_HEIGHT - 80,
                GameConfig.BALL_SIZE
        );

        root.getChildren().addAll(paddle.getPaddleView(), ball.getBallView());

        //  Tạo Scene
        Scene scene = new Scene(root, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        //  Sự kiện bàn phím
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) leftPressed = true;
            if (e.getCode() == KeyCode.RIGHT) rightPressed = true;
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) leftPressed = false;
            if (e.getCode() == KeyCode.RIGHT) rightPressed = false;
        });

        //  Focus để bắt phím
        scene.setOnMouseClicked(e -> root.requestFocus());
        stage.setTitle("baiTapLonAnhEmBoTuc");
        stage.setScene(scene);
        stage.show();

        //  Focus ban đầu
        root.requestFocus();

        //  Game Loop
        new AnimationTimer() {
            private long lastTime = System.nanoTime();

            @Override
            public void handle(long now) {
                if (gameOver) return;

                double deltaTime = (now - lastTime) / 1e9;
                if (deltaTime > 0.05) deltaTime = 0.05; // tránh khựng frame
                lastTime = now;

                //  Di chuyển paddle
                if (leftPressed) paddle.moveLeft(deltaTime);
                if (rightPressed) paddle.moveRight(deltaTime, GameConfig.WINDOW_WIDTH);

                //  Di chuyển bóng
                ball.move();

                //  Va chạm tường
                if (ball.getX() <= 0 || ball.getX() + ball.getWidth() >= GameConfig.WINDOW_WIDTH)
                    ball.reverseX();
                if (ball.getY() <= 0)
                    ball.reverseY();

                //  Va chạm paddle
                if (ball.getBallView().getBoundsInParent().intersects(paddle.getPaddleView().getBoundsInParent())) {
                    if (ball.getY() + ball.getHeight() <= paddle.getY() + 10) {
                        ball.reverseY();
                        ball.getBallView().setY(paddle.getY() - ball.getHeight() - 1);
                    }
                }

                //  Va chạm đáy (Game Over)
                if (ball.getY() > GameConfig.WINDOW_HEIGHT) {
                    gameOver = true;
                    System.out.println("Game Over!");
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
                            break;
                        }
                    }
                }
            }
        }.start();
    }
}