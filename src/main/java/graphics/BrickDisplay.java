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
        System.out.println("Background: " + getClass().getResource("/image/background.jpg"));
        System.out.println("Current dir: " + System.getProperty("user.dir"));
        System.out.println("ClassLoader: " + getClass().getClassLoader().getResource("image/background.jpg"));

        backgroundImage = new Image(getClass().getResource("/image/background.jpg").toExternalForm());
        paddleImage = new Image(getClass().getResource("/image/paddle.png").toExternalForm());
        ballImage = new Image(getClass().getResource("/image/ball.png").toExternalForm());

        brickImages = new Image[]{
                new Image(getClass().getResource("/image/blue.png").toExternalForm()),
                new Image(getClass().getResource("/image/red.png").toExternalForm()),
                new Image(getClass().getResource("/image/gold.png").toExternalForm()),
                new Image(getClass().getResource("/image/green.png").toExternalForm()),
                new Image(getClass().getResource("/image/pink.png").toExternalForm()),
                new Image(getClass().getResource("/image/silver.png").toExternalForm()),
                new Image(getClass().getResource("/image/cyan.png").toExternalForm()),
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
        backgroundView.toBack();

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
