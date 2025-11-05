package graphics;

import constants.GameConfig;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import java.util.function.Supplier;
import java.util.List; // <-- Bổ sung
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane; // <-- Bổ sung
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox; // <-- Bổ sung
import javafx.scene.layout.GridPane; // <-- Bổ sung
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import core.GameRecord;
import core.LeaderboardManager;


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
        Label newGame = createMenuItem("NEW GAME", () -> showNewGameLevels(stage));
        Label levels  = createMenuItem("BẢNG XẾP HẠNG", () -> showLeaderboard(stage));
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
        Label title = createTitle("CHỌN MÀN CHƠI");
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

    // Màn hình Bảng Xếp Hạng ĐÃ CẬP NHẬT
    private static void showLeaderboard(Stage stage) {
        // Khởi tạo Leaderboard Manager
        LeaderboardManager manager = LeaderboardManager.getInstance();
        List<GameRecord> topScores = manager.getTopScores();
        List<GameRecord> recentHistory = manager.getRecentHistory();

        // 1. Tạo giao diện chính
        VBox mainBox = createMenuVBox();
        Label title = createTitle("BẢNG XẾP HẠNG & LỊCH SỬ CHƠI");

        // 2. Tạo 2 Bảng (HBox chứa 2 ScrollPane)
        HBox contentBox = new HBox(50); // Khoảng cách 50 giữa 2 bảng
        contentBox.setAlignment(Pos.CENTER);

        // --- PHẦN 1: TOP 10 CAO ĐIỂM NHẤT ---
        VBox topScoresBox = createRecordVBox("TOP ĐIỂM CAO NHẤT", topScores, true);

        // --- PHẦN 2: 10 LẦN CHƠI GẦN NHẤT ---
        VBox recentHistoryBox = createRecordVBox("LẦN CHƠI GẦN NHẤT", recentHistory, false);

        contentBox.getChildren().addAll(topScoresBox, recentHistoryBox);

        // 3. Nút quay lại
        Label back   = createMenuItem("BACK", () -> show(stage));

        mainBox.getChildren().addAll(title, contentBox, back);

        Scene scene = new Scene(new StackPane(mainBox), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ((StackPane) scene.getRoot()).setStyle(BG_STYLE);
        stage.setScene(scene);
        stage.setTitle("Arkanoid - Leaderboard");
    }
    // Tạo Header Cột
    private static Label createColumnHeader(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        label.setTextFill(Color.web("#00FFFF")); // Xanh Cyan
        return label;
    }

    // Tạo Label Dữ liệu
    private static Label createRecordLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Monospace", FontWeight.NORMAL, 14));
        label.setTextFill(Color.WHITE);
        return label;
    }
    // Tạo VBox chứa Tiêu đề + Bảng dữ liệu
    private static VBox createRecordVBox(String title, List<GameRecord> records, boolean isTopScore) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPrefWidth(GameConfig.WINDOW_WIDTH / 2.0 - 50);

        // Tiêu đề nhỏ của bảng
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#FFFF00"));

        // Bảng dữ liệu (GridPane)
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        // ✅ QUAN TRỌNG: Thiết lập căn chỉnh cho GridPane
        grid.setAlignment(Pos.TOP_CENTER); // Căn giữa bảng
        grid.setStyle("-fx-padding: 10; -fx-background-color: rgba(0, 0, 0, 0.5); -fx-border-radius: 5;");

        // Tiêu đề cột
        // Dùng createColumnHeader đã được tạo
        grid.add(createColumnHeader(isTopScore ? "RANK" : "STT"), 0, 0);
        grid.add(createColumnHeader("PLAYER"), 1, 0);
        grid.add(createColumnHeader("TIME"), 2, 0);
        grid.add(createColumnHeader("SCORE"), 3, 0);
        if (!isTopScore) {
            grid.add(createColumnHeader("WHEN"), 4, 0);
        }

        // Dữ liệu
        int row = 1;
        for (GameRecord record : records) {
            // Cột Rank/STT
            grid.add(createRecordLabel(String.valueOf(row)), 0, row);
            // Cột Tên
            grid.add(createRecordLabel(record.getPlayerName()), 1, row);
            // Cột Thời gian chơi (Duration)
            grid.add(createRecordLabel(record.getFormattedTime()), 2, row);
            // Cột Điểm
            grid.add(createRecordLabel(String.valueOf(record.getScore())), 3, row);

            if (!isTopScore) {
                // Cột Thời điểm (Timestamp)
                grid.add(createRecordLabel(record.getFormattedTimestamp()), 4, row);
            }
            row++;
        }

        // Tạo label thông báo nếu không có dữ liệu
        if (records.isEmpty()) {
            Label emptyLabel = createDisabledMenuItem("(Chưa có dữ liệu)");
            // Căn giữa thông báo
            grid.add(emptyLabel, 0, 1, isTopScore ? 4 : 5, 1); // Trải rộng qua tất cả các cột
            GridPane.setHalignment(emptyLabel, javafx.geometry.HPos.CENTER);
        }

        // Dùng ScrollPane để cuộn nếu quá nhiều record
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        // ✅ QUAN TRỌNG: Thiết lập nền trong suốt cho ScrollPane để thấy GridPane
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: #555; -fx-border-width: 2;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        box.getChildren().addAll(titleLabel, scrollPane);
        return box;
    }
}