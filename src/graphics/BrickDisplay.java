package graphics;

import constants.GameConfig;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Random;

public class BrickDisplay {
    private Image backgroundImage;
    private Image[] brickImage;
    private Random random = new Random();

    public BrickDisplay() {
        brickImage = new Image[] {
                new Image(getClass().getClassLoader().getResourceAsStream("image/blue.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("image/red.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("image/gold.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("image/green.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("image/pink.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("image/silver.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("image/cyan.png")),
        };
        backgroundImage = new Image(getClass().getClassLoader().getResourceAsStream("image/silun.jpg"));
    }

    public Group getBrickDisplay() {
        Group brickGroup = new Group();

        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(GameConfig.WINDOW_WIDTH);
        backgroundView.setFitHeight(GameConfig.WINDOW_HEIGHT);
        brickGroup.getChildren().add(backgroundView);

        for (int row = 0; row < GameConfig.BRICK_ROWS; row++) {
            for (int col = 0; col < GameConfig.BRICK_COLS; col++) {
                Image selectedImage = brickImage[random.nextInt(brickImage.length)];
                ImageView brickView = new ImageView(selectedImage);

                double x = col * (GameConfig.BRICK_WIDTH + GameConfig.BRICK_MARGIN) + 28;
                double y = row * (GameConfig.BRICK_HEIGHT + GameConfig.BRICK_MARGIN) + 25;

                brickView.setX(x);
                brickView.setY(y);
                brickView.setFitWidth(GameConfig.BRICK_WIDTH);
                brickView.setFitHeight(GameConfig.BRICK_HEIGHT);

                brickGroup.getChildren().add(brickView);
            }
        }
        return brickGroup;
    }
}
