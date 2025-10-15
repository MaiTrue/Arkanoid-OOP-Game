package graphics;

import constants.GameConfig;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

public class BrickDisplay {
    private Image backgroundImage;
    private Image[] brickImages;
    private final Random random = new Random();
    private Image paddleImage;
    private Image ballImage;

    public BrickDisplay() {
        // Đường dẫn ảnh tuyệt đối trong thư mục project
        backgroundImage = new Image("file:src/image/background.jpg");
        paddleImage = new Image("file:src/image/paddle.png");
        ballImage = new Image("file:src/image/ball.png");

        brickImages = new Image[]{
                new Image("file:src/image/blue.png"),
                new Image("file:src/image/red.png"),
                new Image("file:src/image/gold.png"),
                new Image("file:src/image/green.png"),
                new Image("file:src/image/pink.png"),
                new Image("file:src/image/silver.png"),
                new Image("file:src/image/cyan.png"),
        };
    }

    /** Tạo toàn bộ màn hình với nền + gạch */
    public Group getBrickDisplay() {
        Group group = new Group();

        // 1️⃣ Thêm background trước
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(GameConfig.WINDOW_WIDTH);
        backgroundView.setFitHeight(GameConfig.WINDOW_HEIGHT);
        group.getChildren().add(backgroundView);

        // 2️⃣ Sau đó thêm gạch
        for (int row = 0; row < GameConfig.BRICK_ROWS; row++) {
            for (int col = 0; col < GameConfig.BRICK_COLS; col++) {
                Image brickImg = brickImages[random.nextInt(brickImages.length)];
                ImageView brickView = new ImageView(brickImg);

                double x = col * (GameConfig.BRICK_WIDTH + GameConfig.BRICK_MARGIN) + 28;
                double y = row * (GameConfig.BRICK_HEIGHT + GameConfig.BRICK_MARGIN) + 25;

                brickView.setX(x);
                brickView.setY(y);
                brickView.setFitWidth(GameConfig.BRICK_WIDTH);
                brickView.setFitHeight(GameConfig.BRICK_HEIGHT);

                group.getChildren().add(brickView);
            }
        }

        return group;
    }

    public Group resetBricks() {
        return getBrickDisplay();
    }

    public Image getPaddleImage() {
        return paddleImage;
    }

    public Image getBallImage() {
        return ballImage;
    }
}
