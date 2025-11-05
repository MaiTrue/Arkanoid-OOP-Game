package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** LeaderboardManager: Singleton để quản lý danh sách kỷ lục. */
public class LeaderboardManager {
    private static LeaderboardManager instance;
    private List<GameRecord> records;
    private static final String FILE_NAME = "leaderboard.ser";
    private static final int MAX_DISPLAY_RECORDS = 10; // Số lượng hiển thị tối đa

    private LeaderboardManager() {
        records = loadRecords();
    }

    public static LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    private List<GameRecord> loadRecords() {
        // Sử dụng ObjectInputStream để đọc danh sách đối tượng
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            System.out.println("Leaderboard loaded successfully.");
            return (List<GameRecord>) ois.readObject();
        } catch (FileNotFoundException e) {
            // Trường hợp file chưa tồn tại
            System.out.println("Leaderboard file not found. Creating new list.");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            // Lỗi đọc file hoặc Class không khớp (versioning)
            e.printStackTrace();
            System.err.println("Error loading leaderboard. Starting with empty list.");
            return new ArrayList<>();
        }
    }

    private void saveRecords() {
        // Sử dụng ObjectOutputStream để ghi danh sách đối tượng
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRecord(GameRecord record) {
        records.add(record);
        // Sau khi thêm, lưu ngay lập tức để đảm bảo tính kịp thời cho lịch sử gần nhất
        saveRecords();
    }

    /**
     * Lấy TOP 10 theo điểm số (sắp xếp giảm dần). Phụ: thời gian chơi ngắn hơn sẽ được ưu tiên.
     */
    public List<GameRecord> getTopScores() {
        return records.stream()
                .sorted(Comparator
                        .comparingInt(GameRecord::getScore).reversed() // Điểm cao nhất lên đầu
                        .thenComparingLong(GameRecord::getTimeElapsedSeconds)) // Nếu bằng điểm, thời gian ngắn hơn lên đầu
                .limit(MAX_DISPLAY_RECORDS)
                .collect(Collectors.toList());
    }

    /**
     * Lấy 10 lần chơi gần nhất (sắp xếp theo timestamp giảm dần).
     */
    public List<GameRecord> getRecentHistory() {
        return records.stream()
                .sorted(Comparator.comparingLong(GameRecord::getTimestampMillis).reversed()) // Mới nhất lên đầu
                .limit(MAX_DISPLAY_RECORDS)
                .collect(Collectors.toList());
    }
}