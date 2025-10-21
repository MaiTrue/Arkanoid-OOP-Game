package graphics;

import constants.GameConfig;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Patterns.PikachuPattern;

import java.util.Random;

public class BrickDisplay {
    private Image[] brickImages;
    private int[][] pattern;
    private final Random random = new Random();
    private Image paddleImage;
    private Image ballImage;

    public BrickDisplay() {
        paddleImage = new Image(getClass().getResource("/image/paddle.png").toExternalForm());
        ballImage = new Image(getClass().getResource("/image/ball2.png").toExternalForm());

        brickImages = new Image[]{
                new Image(getClass().getResource("/image/yellow.png").toExternalForm()), // 1 = vàng
                new Image(getClass().getResource("/image/red1.png").toExternalForm()),   // 2 = đỏ
                new Image(getClass().getResource("/image/black.jpg").toExternalForm()),  // 3 = đen
                new Image(getClass().getResource("/image/white1.jpg").toExternalForm())   // 4 = trắng
        };
    }
    public void setPattern(int[][] pattern) {
        this.pattern = pattern;
    }

    // Tạo toàn bộ màn hình với nền + gạch
    public Group  getBrickDisplay () {
        Group group = new Group();

        if (pattern == null) {
            System.out.println(" Không có pattern nào được đặt trong BrickDisplay!");
            return group;
        }

        for (int row = 0; row < pattern.length; row++) {
            for (int col = 0; col < pattern[row].length; col++) {
                int value = pattern[row][col];
                if (value == 0) continue; // 0 = trống, bỏ qua

                // Lấy ảnh tương ứng: 1->yellow, 2->red, 3->black, 4->white
                Image brickImg = brickImages[value - 1];
                ImageView brickView = new ImageView(brickImg);

                double offsetX = (GameConfig.WINDOW_WIDTH - pattern[0].length * (GameConfig.BRICK_WIDTH + GameConfig.BRICK_MARGIN)) / 2;
                double offsetY = (GameConfig.WINDOW_HEIGHT - pattern.length * (GameConfig.BRICK_HEIGHT + GameConfig.BRICK_MARGIN)) / 2 - 75;

                double x = offsetX + col * (GameConfig.BRICK_WIDTH + GameConfig.BRICK_MARGIN);
                double y = offsetY + row * (GameConfig.BRICK_HEIGHT + GameConfig.BRICK_MARGIN);


                brickView.setX(x);
                brickView.setY(y);
                brickView.setFitWidth(GameConfig.BRICK_WIDTH);
                brickView.setFitHeight(GameConfig.BRICK_HEIGHT);

                group.getChildren().add(brickView);
            }
        }

        return group;
    }


    public Group resetBricks() {return getBrickDisplay();}

    public Image getPaddleImage() {
        return paddleImage;
    }

    public Image getBallImage() {return ballImage;}
}
