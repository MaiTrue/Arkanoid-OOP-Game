package graphics;

import constants.GameConfig;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Menu {

    // Màu và font mặc định cho toàn menu
    private static final String BG_STYLE = """
        -fx-background-image: url('/image/menu1.png');
        -fx-background-size: cover;
        -fx-background-position: center center;
    """;

    private static final Font TITLE_FONT = Font.font("Verdana", FontWeight.EXTRA_BOLD, 64);
    private static final Font MENU_FONT = Font.font("Arial", FontWeight.BOLD, 36);

    // Màn hình menu chính
    public static void show(Stage stage) {
        VBox menuBox = createMenuVBox();

        Label title = createTitle("ARKANOID");
        Label newGame = createMenuItem("NEW GAME", () -> new Game().show(stage));
        Label levels  = createMenuItem("LEVELS", () -> showLevels(stage));
        Label exit    = createMenuItem("EXIT", stage::close);

        menuBox.getChildren().addAll(title, newGame, levels, exit);

        Scene scene = new Scene(new StackPane(menuBox), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ((StackPane) scene.getRoot()).setStyle(BG_STYLE);

        stage.setScene(scene);
        stage.setTitle("Arkanoid - Main Menu");
        stage.show();
    }

    // Màn hình chọn level
    private static void showLevels(Stage stage) {
        VBox levelBox = createMenuVBox();

        Label title = createTitle("SELECT LEVEL");
        Label level1 = createMenuItem("LEVEL 1", () -> new Game().show(stage));
        Label level2 = createMenuItem("LEVEL 2", () -> new Game().show(stage));
        Label level3 = createMenuItem("LEVEL 3", () -> new Game().show(stage));
        Label back   = createMenuItem("BACK", () -> show(stage));

        levelBox.getChildren().addAll(title, level1, level2, level3, back);

        Scene scene = new Scene(new StackPane(levelBox), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ((StackPane) scene.getRoot()).setStyle(BG_STYLE);

        stage.setScene(scene);
        stage.setTitle("Arkanoid - Levels");
    }

    // Tạo bố cục chính cho menu
    private static VBox createMenuVBox() {
        VBox box = new VBox(25);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    // Tạo tiêu đề menu
    private static Label createTitle(String text) {
        Label label = new Label(text);
        label.setFont(TITLE_FONT);
        label.setTextFill(Color.web("#FFD700"));
        label.setEffect(new DropShadow(15, Color.BLACK));
        return label;
    }

    // Tạo 1 item menu có hiệu ứng hover + click
    private static Label createMenuItem(String text, Runnable action) {
        Label label = new Label(text);
        label.setFont(MENU_FONT);
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(8, Color.BLACK));

        // Hiệu ứng hover
        label.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            label.setTextFill(Color.web("#00FFFF"));
            label.setScaleX(1.1);
            label.setScaleY(1.1);
        });
        label.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            label.setTextFill(Color.WHITE);
            label.setScaleX(1.0);
            label.setScaleY(1.0);
        });

        // Khi click
        label.setOnMouseClicked(e -> action.run());
        return label;
    }
}