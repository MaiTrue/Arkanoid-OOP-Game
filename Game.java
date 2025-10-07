import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Arkanoid Base Version");
        GamePanel panel = new GamePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.startGameLoop();
    }
}
