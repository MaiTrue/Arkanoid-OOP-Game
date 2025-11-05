package graphics;

import constants.GameConfig;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import java.util.function.Supplier;
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
import Patterns.PikachuPattern;

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
        Label newGame = createMenuItem("NEW GAME", () -> showNewGameLevels(stage)); // SỬA: Gọi hàm chọn level
        Label levels  = createMenuItem("BẢNG XẾP HẠNG", () -> showLeaderboard(stage)); // SỬA: Đổi tên và đổi hàm
        Label exit    = createMenuItem("EXIT", stage::close);

        menuBox.getChildren().addAll(title, newGame, levels, exit);

        Scene scene = new Scene(new StackPane(menuBox), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ((StackPane) scene.getRoot()).setStyle(BG_STYLE);

        stage.setScene(scene);
        stage.setTitle("Arkanoid - Main Menu");
        stage.show();
    }

    // Màn hình chọn level MỚI (khi bấm NEW GAME)
    private static void showNewGameLevels(Stage stage) {
        VBox levelBox = createMenuVBox();
        Label title = createTitle("CHỌN MÀN CHƠI"); // Đổi tiêu đề
        // --- Level 1 (Luôn bật) ---
        Label level1 = createMenuItem("LEVEL 1", () -> loadGame(stage, () -> new Level1Panel()));

        // --- Level 2 (Luôn bật) ---
        Label level2 = createMenuItem("LEVEL 2", () -> loadGame(stage, () -> new Level2Panel()));

        // --- Level 3 (Luôn bật) ---
        Label level3 = createMenuItem("LEVEL 3", () -> loadGame(stage, () -> new Level3Panel()));

        // --- Nút Back ---
        Label back = createMenuItem("BACK", () -> show(stage));

        // Thêm tất cả vào
        levelBox.getChildren().addAll(title, level1, level2, level3, back);

        // (Code Scene cũ của bạn)
        Scene scene = new Scene(new StackPane(levelBox), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ((StackPane) scene.getRoot()).setStyle(BG_STYLE);
        stage.setScene(scene);
        stage.setTitle("Arkanoid - Chọn Level");
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

    private static Label createDisabledMenuItem(String text) {
        Label label = new Label(text);
        label.setFont(MENU_FONT);
        label.setTextFill(Color.GRAY); // Màu xám
        label.setOpacity(0.6); // Hơi mờ đi
        label.setEffect(new DropShadow(8, Color.BLACK));
        return label;
    }

    private static void loadGame(Stage stage, Supplier<GamePanel> gamePanelSupplier) {

        // --- BƯỚC 1: TẠO MÀN HÌNH LOADING ---
        // (Chạy trên Luồng UI)
        ProgressIndicator spinner = new ProgressIndicator(); // Vòng quay
        VBox loadingBox = createMenuVBox(); // Dùng lại hàm cũ của bạn
        loadingBox.getChildren().addAll(createTitle("LOADING..."), spinner);

        // Đặt nền tối cho màn hình loading
        loadingBox.setStyle("-fx-background-color: #222;");

        Scene loadingScene = new Scene(new StackPane(loadingBox), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        stage.setScene(loadingScene);
        stage.setTitle("Loading...");

        // --- BƯỚC 2: TẠO TÁC VỤ NỀN (TASK) ---
        // Task này sẽ trả về một GamePanel (là Level1Panel hoặc Level2Panel)
        Task<GamePanel> loadTask = new Task<>() {
            @Override
            protected GamePanel call() throws Exception {
                // *** DÒNG NÀY CHẠY TRÊN LUỒNG NỀN (BACKGROUND) ***
                // Nó không làm "đơ" UI

                // (Tùy chọn) Giả lập việc load nặng, bạn có thể xóa 2 dòng này
                // try { Thread.sleep(1000); } catch (InterruptedException e) {}

                // Đây là công việc chính: new Level1Panel()
                // Nó sẽ load ảnh, tạo gạch...
                return gamePanelSupplier.get();
            }
        };

        // --- BƯỚC 3: LẮNG NGHE KHI TASK LÀM XONG ---
        loadTask.setOnSucceeded(e -> {
            // *** DÒNG NÀY CHẠY TRÊN LUỒNG UI (UI THREAD) ***

            // Lấy kết quả (cái GamePanel đã load xong)
            GamePanel loadedPanel = loadTask.getValue();

            // Hiển thị game
            loadedPanel.show(stage);
        });

        // (Tùy chọn) Lắng nghe nếu Task bị lỗi
        loadTask.setOnFailed(e -> {
            loadTask.getException().printStackTrace();
            show(stage); // Quay về menu nếu lỗi
        });

        // --- BƯỚC 4: KHỞI CHẠY LUỒNG NỀN ---
        new Thread(loadTask).start();
    }

    // Màn hình Bảng Xếp Hạng (hiện đang là placeholder)
    private static void showLeaderboard(Stage stage) {
        VBox box = createMenuVBox();
        Label title = createTitle("BẢNG XẾP HẠNG");
        Label placeholder = createDisabledMenuItem("(Coming Soon...)"); // Dùng lại hàm tạo nút mờ
        Label back   = createMenuItem("BACK", () -> show(stage)); // Nút quay lại

        box.getChildren().addAll(title, placeholder, back);

        Scene scene = new Scene(new StackPane(box), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ((StackPane) scene.getRoot()).setStyle(BG_STYLE);
        stage.setScene(scene);
        stage.setTitle("Arkanoid - Leaderboard");
    }
}