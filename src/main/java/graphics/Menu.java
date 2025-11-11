package graphics;

import constants.GameConfig;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import java.util.function.Supplier;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
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
        Label levels = createMenuItem("BẢNG XẾP HẠNG", () -> showLeaderboard(stage));
        Label exit = createMenuItem("EXIT", stage::close);

        menuBox.getChildren().addAll(title, newGame, levels, exit);

        Scene scene = new Scene(new StackPane(menuBox), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ((StackPane) scene.getRoot()).setStyle(BG_STYLE);

        stage.setScene(scene);
        stage.setTitle("Arkanoid - Main Menu");
        stage.show();
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

    private static void showLeaderboard(Stage stage) {
        LeaderboardManager manager = LeaderboardManager.getInstance();
        List<GameRecord> topScores = manager.getTopScores();

        VBox mainBox = createMenuVBox();
        Label title = createTitle("BẢNG XẾP HẠNG");

        HBox contentBox = new HBox(50);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPrefWidth(GameConfig.WINDOW_WIDTH);
        VBox topScoresBox = createSimpleRecordDisplay("TOP 10 ĐIỂM CAO", topScores, true);

        contentBox.getChildren().addAll(topScoresBox);
        Label back = createMenuItem("QUAY LẠI", () -> show(stage));

        mainBox.setSpacing(15);
        mainBox.getChildren().addAll(title, contentBox, back);

        Scene scene = new Scene(new StackPane(mainBox), GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        ((StackPane) scene.getRoot()).setStyle(BG_STYLE);
        stage.setScene(scene);
        stage.setTitle("Arkanoid - Leaderboard");
    }

    private static Label createSimpleColumnHeader(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Monospace", FontWeight.EXTRA_BOLD, 20));
        label.setTextFill(Color.web("#FFFF00"));
        return label;
    }

    private static Label createSimpleRecordLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Monospace", FontWeight.NORMAL, 16));
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(3, 1, 1, Color.BLACK));
        return label;
    }

    private static double getMinWidthForColumn(String columnName) {
        return switch (columnName.toUpperCase()) {
            case "HẠNG", "STT" -> 60.0;
            case "TÊN" -> 200.0;
            case "THỜI GIAN" -> 120.0;
            case "ĐIỂM" -> 100.0;
            default -> 50.0;
        };
    }

    private static Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        label.setTextFill(Color.web("#FFFF00"));
        label.setMinWidth(getMinWidthForColumn(text));
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
    }

    private static Label createRecordLabel(String text, String columnName) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(3, 1, 1, Color.BLACK));
        label.setMinWidth(getMinWidthForColumn(columnName));
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
    }

    private static VBox createSimpleRecordDisplay(String title, List<GameRecord> records, boolean isTopScore) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.TOP_CENTER);
        box.setMinWidth(500);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 28));
        titleLabel.setTextFill(Color.web("#00FFFF"));
        titleLabel.setEffect(new DropShadow(5, 2, 2, Color.BLACK));
        box.getChildren().add(titleLabel);

        HBox headerRow = new HBox(5); // Khoảng cách nhỏ giữa các cột
        headerRow.setAlignment(Pos.CENTER_LEFT);

        headerRow.getChildren().addAll(
                createHeaderLabel(isTopScore ? "Hạng" : "STT"),
                createHeaderLabel("Tên"),
                createHeaderLabel("Thời Gian"),
                createHeaderLabel("Điểm")
        );
        box.getChildren().add(headerRow);

        if (records.isEmpty()) {
            box.getChildren().add(createDisabledMenuItem("(Chưa có dữ liệu)"));
            return box;
        }

        int limit = Math.min(records.size(), 10);
        for (int i = 0; i < limit; i++) {
            GameRecord record = records.get(i);
            HBox dataRow = new HBox(5); // Khoảng cách nhỏ giữa các cột
            dataRow.setAlignment(Pos.CENTER_LEFT);

            String playerName = record.getPlayerName().length() > 20
                    ? record.getPlayerName().substring(0, 19) + "..."
                    : record.getPlayerName();

            dataRow.getChildren().addAll(
                    createRecordLabel(String.valueOf(i + 1), isTopScore ? "Hạng" : "STT"),
                    createRecordLabel(playerName, "Tên"),
                    createRecordLabel(record.getFormattedTime(), "Thời Gian"),
                    createRecordLabel(String.valueOf(record.getScore()), "Điểm")
            );

            box.getChildren().add(dataRow);
        }

        return box;
    }
}