package Patterns;

public class LevelPatterns {

    public static int[][] getPattern(int level) {
        return switch (level) {
            case 1 -> PikachuPattern.DATA;
            // thêm nhiều hình khác ở đây
            default -> PikachuPattern.DATA;
        };
    }
}