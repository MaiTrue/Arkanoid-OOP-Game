package graphics;

import constants.GameConfig;
import core.LeaderboardManager;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.function.Supplier;
import java.util.List;
import core.GameRecord;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;


public class Menu {

    private static ImageView helpImageView;

    private static final String BG_STYLE = """
        -fx-background-image: url('/image/menu1.png');
        -fx-background-size: cover;
        -fx-background-position: center center;
    """;

    private static final Font TITLE_FONT = Font.font("Verdana", FontWeight.EXTRA_BOLD, 64);
    private static final Font MENU_FONT = Font.font("Arial", FontWeight.BOLD, 36);


    // Màn hình menu chính
    public static void show(Stage stage) {
        Pane root = new Pane();
        root.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        VBox menuBox = createMenuVBox();

        Label title = createTitle("ARKANOID");
        Label newGame = createMenuItem("NEW GAME", () -> showNewGameLevels(stage));
        Label levels  = createMenuItem("BẢNG XẾP HẠNG", () -> showLeaderboard(stage));
        Label exit    = createMenuItem("EXIT", stage::close);

        menuBox.getChildren().addAll(title, newGame, levels, exit);

        StackPane stackPane = new StackPane(menuBox);
        stackPane.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        stackPane.setStyle(BG_STYLE);

        root.getChildren().add(stackPane);

        // ------------- THÊM BIỂU TƯỢNG DẤU CHẤM THAN (HELP ICON) -------------
        try {
            // Đảm bảo file help_icon.png tồn tại trong resources/image/
            Image helpIconImage = new Image(Menu.class.getResource("/image/help_icon1.png").toExternalForm());
            ImageView helpIcon = new ImageView(helpIconImage);
            helpIcon.setFitWidth(40);
            helpIcon.setFitHeight(40);
            helpIcon.setLayoutX(GameConfig.WINDOW_WIDTH - 50);
            helpIcon.setLayoutY(10);
            helpIcon.setPickOnBounds(true);
            helpIcon.setStyle("-fx-cursor: hand;");

            helpIcon.setOnMouseClicked(event -> {
                showHelp(root); // Gọi hàm hiển thị hướng dẫn
            });
            root.getChildren().add(helpIcon);
        } catch (Exception e) {
            System.err.println("Lỗi tải help_icon.png: " + e.getMessage());
        }
        // -----------------------------------------------------------------------


        Scene scene = new Scene(root, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Arkanoid - Main Menu");
        stage.show();
    }

    // Logic hiển thị/ẩn ảnh hướng dẫn
    private static void showHelp(Pane root) {
        if (helpImageView == null || !root.getChildren().contains(helpImageView)) {
            try {
                // Tải ảnh hướng dẫn
                Image helpGuideImage = new Image(Menu.class.getResource("/image/huongdan.png").toExternalForm());
                helpImageView = new ImageView(helpGuideImage);

                // Đặt kích cỡ ảnh (65% width)
                helpImageView.setFitWidth(GameConfig.WINDOW_WIDTH * 0.55);
                helpImageView.setPreserveRatio(true);

                // Đặt ảnh hướng dẫn ở giữa màn hình (theo chiều ngang)
                helpImageView.setLayoutX((GameConfig.WINDOW_WIDTH - helpImageView.getFitWidth()) / 2);

                // FIX LỖI LỆCH DƯỚI: Dịch chuyển ảnh lên trên 200px
                double Y_OFFSET = 200;
                helpImageView.setLayoutY((GameConfig.WINDOW_HEIGHT - helpImageView.getFitHeight()) / 2 - Y_OFFSET);

                // Tạo nền mờ (overlay)
                Pane overlay = new Pane();
                overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
                overlay.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

                // Click ra ngoài để đóng
                overlay.setOnMouseClicked(e -> {
                    root.getChildren().remove(overlay);
                    root.getChildren().remove(helpImageView);
                    helpImageView = null;
                });

                root.getChildren().add(overlay);
                root.getChildren().add(helpImageView);

            } catch (Exception e) {
                System.err.println("Lỗi tải huongdan.png: " + e.getMessage());
            }
        } else {
            // Logic đóng ảnh đã được xử lý bởi overlay.setOnMouseClicked
        }
    }


    private static void showNewGameLevels(Stage stage) {
        VBox levelBox = createMenuVBox();
        Label title = createTitle("CHỌN MÀN CHƠI");

        Label level1 = createMenuItem("LEVEL 1", () -> loadGame(stage, () -> new Level1Panel()));

        Label level2 = createMenuItem("LEVEL 2", () -> loadGame(stage, () -> new Level2Panel()));

        Label level3 = createMenuItem("LEVEL 3", () -> loadGame(stage, () -> new Level3Panel()));

        Label back = createMenuItem("BACK", () -> show(stage));

        levelBox.getChildren().addAll(title, level1, level2, level3, back);

        Scene scene = new Scene(new StackPane(levelBox), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ((StackPane) scene.getRoot()).setStyle(BG_STYLE);
        stage.setScene(scene);
        stage.setTitle("Arkanoid - Chọn Level");
    }

    private static VBox createMenuVBox() {
        VBox box = new VBox(25);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private static Label createTitle(String text) {
        Label label = new Label(text);
        label.setFont(TITLE_FONT);
        label.setTextFill(Color.web("#FFD700"));
        label.setEffect(new DropShadow(15, Color.BLACK));
        return label;
    }

    private static Label createMenuItem(String text, Runnable action) {
        Label label = new Label(text);
        label.setFont(MENU_FONT);
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(8, Color.BLACK));

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

        label.setOnMouseClicked(e -> action.run());
        return label;
    }

    private static Label createDisabledMenuItem(String text) {
        Label label = new Label(text);
        label.setFont(MENU_FONT);
        label.setTextFill(Color.GRAY);
        label.setOpacity(0.6);
        label.setEffect(new DropShadow(8, Color.BLACK));
        return label;
    }

    private static void loadGame(Stage stage, Supplier<GamePanel> gamePanelSupplier) {

        ProgressIndicator spinner = new ProgressIndicator();
        VBox loadingBox = createMenuVBox();
        loadingBox.getChildren().addAll(createTitle("LOADING..."), spinner);

        loadingBox.setStyle("-fx-background-color: #222;");

        Scene loadingScene = new Scene(new StackPane(loadingBox), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        stage.setScene(loadingScene);
        stage.setTitle("Loading...");

        Task<GamePanel> loadTask = new Task<>() {
            @Override
            protected GamePanel call() throws Exception {
                return gamePanelSupplier.get();
            }
        };

        loadTask.setOnSucceeded(e -> {
            GamePanel loadedPanel = loadTask.getValue();

            loadedPanel.show(stage);
        });
        loadTask.setOnFailed(e -> {
            loadTask.getException().printStackTrace();
            show(stage);
        });
        new Thread(loadTask).start();
    }

    // Phương thức gọi Leaderboard (Đã chuyển sang gọi Leaderboard.java)
    // Thêm vào các hằng số ở đầu lớp Menu
    private static final Font LEADERBOARD_TITLE_FONT = Font.font("Verdana", FontWeight.EXTRA_BOLD, 50);
    private static final Font LEADERBOARD_FONT = Font.font("Arial", FontWeight.NORMAL, 24);
    private static final Font LEADERBOARD_HEADER_FONT = Font.font("Arial", FontWeight.BOLD, 28);


    // Thay thế phương thức showLeaderboard(Stage stage) hiện tại bằng phương thức này:
    private static void showLeaderboard(Stage stage) {
        // 1. Tải 5 người chơi cao nhất (giả sử LeaderboardManager.loadLeaderboard() đã được sửa)
        List<GameRecord> records = LeaderboardManager.getInstance().getTopScores();

        VBox leaderboardBox = createMenuVBox();

        Label title = new Label("BẢNG XẾP HẠNG (TOP 5)");
        title.setFont(LEADERBOARD_TITLE_FONT);
        title.setTextFill(Color.web("#FFFFFF")); // Màu trắng nổi bật
        title.setEffect(new DropShadow(10, Color.web("#FF4500"))); // Bóng đỏ cam

        // 2. Tạo GridPane để định dạng dữ liệu
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(30); // Khoảng cách cột
        grid.setVgap(15); // Khoảng cách dòng
        grid.setLayoutY(title.getFont().getSize() + 50); // Đặt lưới xuống dưới tiêu đề

        // Tiêu đề cột
        grid.add(createLeaderboardLabel("RANK", LEADERBOARD_HEADER_FONT), 0, 0);
        grid.add(createLeaderboardLabel("TÊN", LEADERBOARD_HEADER_FONT), 1, 0);
        grid.add(createLeaderboardLabel("ĐIỂM", LEADERBOARD_HEADER_FONT), 2, 0);
        grid.add(createLeaderboardLabel("THỜI GIAN", LEADERBOARD_HEADER_FONT), 3, 0);


        // 3. Hiển thị dữ liệu
        for (int i = 0; i < records.size(); i++) {
            GameRecord record = records.get(i);
            int rank = i + 1;

            // Định dạng thời gian (ví dụ: 01:30)
            long totalSeconds = record.getTimeElapsedSeconds() ;
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            String timeStr = String.format("%02d:%02d", minutes, seconds);

            grid.add(createLeaderboardLabel(String.valueOf(rank), LEADERBOARD_FONT), 0, i + 1);
            grid.add(createLeaderboardLabel(record.getPlayerName(), LEADERBOARD_FONT), 1, i + 1);
            grid.add(createLeaderboardLabel(String.valueOf(record.getScore()), LEADERBOARD_FONT), 2, i + 1);
            grid.add(createLeaderboardLabel(timeStr, LEADERBOARD_FONT), 3, i + 1);
        }

        Label back = createMenuItem("BACK", () -> show(stage));

        leaderboardBox.getChildren().addAll(title, grid, back);
        VBox.setVgrow(grid, javafx.scene.layout.Priority.ALWAYS); // Cho phép grid giãn nở

        // Đặt Scene
        Scene scene = new Scene(new StackPane(leaderboardBox), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ((StackPane) scene.getRoot()).setStyle(BG_STYLE); // Dùng background chính của Menu
        stage.setScene(scene);
        stage.setTitle("Arkanoid - Bảng Xếp Hạng");
    }


    // Thêm phương thức helper mới để tạo Label cho Leaderboard
    private static Label createLeaderboardLabel(String text, Font font) {
        Label label = new Label(text);
        label.setFont(font);
        label.setTextFill(Color.WHITE); // In chữ trắng lên nền
        label.setEffect(new DropShadow(2, Color.BLACK)); // Thêm bóng để nổi bật hơn
        return label;
    }
}