import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        // Vẽ hình chữ nhật
        Rectangle rect = new Rectangle(50, 50, 100, 60);
        rect.setFill(Color.BLUE);

        // Vẽ hình tròn
        Circle circle = new Circle(250, 80, 40);
        circle.setFill(Color.RED);

        // Vẽ đường thẳng
        Line line = new Line(50, 150, 250, 150);
        line.setStroke(Color.GREEN);

        // Vẽ chữ
        Text text = new Text(50, 200, "Xin chào, đây là test JavaFX!");
        text.setFill(Color.BLACK);

        // Thêm các đối tượng vào Pane
        root.getChildren().addAll(rect, circle, line, text);

        // Tạo Scene
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("Test Đồ Họa JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
        // tesst
    }
}