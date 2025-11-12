package graphics;

import constants.GameConfig;
import core.GameRecord;
import core.LeaderboardManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.List;

public class Leaderboard extends StackPane {
    private static final String BG_STYLE = """
        -fx-background-image: url('/image/menu1.png');
        -fx-background-size: cover;
        -fx-background-position: center center;
    """;
    private static final Font TITLE_FONT = Font.font("Verdana", FontWeight.EXTRA_BOLD, 48);
    private static final Font MENU_FONT = Font.font("Arial", FontWeight.BOLD, 36);
    public Leaderboard() {
        this.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        this.setStyle(BG_STYLE);

        initializeUI();
    }

    private void initializeUI() {
        LeaderboardManager manager = LeaderboardManager.getInstance();
        List<GameRecord> topScores = manager.getTopScores();
        VBox mainBox = createMenuVBox();
        Label title = createTitle("BẢNG XẾP HẠNG TOP 5");
        VBox topScoresBox = createRecordVBox("TOP 5 ĐIỂM CAO NHẤT", topScores, true);
        Label back = createMenuItem("BACK", () -> Menu.show((Stage) this.getScene().getWindow()));
        mainBox.getChildren().addAll(title, topScoresBox, back);

        this.getChildren().add(mainBox);
    }

    public void show(Stage stage) {
        Scene scene = new Scene(this, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Arkanoid - Leaderboard");
        stage.show();
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

    private static Label createColumnHeader(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        label.setTextFill(Color.web("#00FFFF"));
        return label;
    }

    private static Label createRecordLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Monospace", FontWeight.NORMAL, 14));
        label.setTextFill(Color.WHITE);
        return label;
    }

    private static VBox createRecordVBox(String title, List<GameRecord> records, boolean isTopScore) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPrefWidth(GameConfig.WINDOW_WIDTH * 0.75);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#FFFF00"));

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(30);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setStyle("-fx-padding: 10; -fx-background-color: rgba(0, 0, 0, 0.5); -fx-border-color: #555; -fx-border-width: 2;");
        grid.add(createColumnHeader("RANK"), 0, 0);
        grid.add(createColumnHeader("PLAYER"), 1, 0);
        grid.add(createColumnHeader("TIME"), 2, 0);
        grid.add(createColumnHeader("SCORE"), 3, 0);

        int row = 1;
        for (GameRecord record : records) {
            grid.add(createRecordLabel(String.valueOf(row)), 0, row);
            grid.add(createRecordLabel(record.getPlayerName()), 1, row);
            grid.add(createRecordLabel(record.getFormattedTime()), 2, row);
            grid.add(createRecordLabel(String.valueOf(record.getScore())), 3, row);
            row++;
        }

        if (records.isEmpty()) {
            Label emptyLabel = new Label("(Chưa có dữ liệu)");
            emptyLabel.setTextFill(Color.GRAY);
            grid.add(emptyLabel, 0, 1, 4, 1);
            GridPane.setHalignment(emptyLabel, javafx.geometry.HPos.CENTER);
        }
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: #555;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        box.getChildren().addAll(titleLabel, scrollPane);
        return box;
    }
}