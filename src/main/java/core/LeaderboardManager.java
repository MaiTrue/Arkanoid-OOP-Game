package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** LeaderboardManager: Singleton để quản lý danh sách kỷ lục. */
public class LeaderboardManager {
    private static LeaderboardManager instance;
    private List<GameRecord> records;
    private static final String FILE_NAME = "leaderboard.ser";
    // Đã thay đổi để phù hợp với yêu cầu mới
    private static final int MAX_DISPLAY_RECORDS = 5;

    private LeaderboardManager() {
        records = loadRecords();
    }

    public static LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }
// ... (Các phương thức loadRecords, saveRecords, addRecord giữ nguyên)

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
        // Sau khi thêm, lưu ngay lập tức
        saveRecords();
    }


    /**
     * Lấy TOP MAX_DISPLAY_RECORDS (5 người) theo điểm số giảm dần,
     * ưu tiên thời gian chơi ngắn hơn nếu điểm bằng nhau.
     * * CHÚ Ý: Đã bỏ từ khóa 'static' để truy cập biến 'records'.
     */
    public List<GameRecord> getTopScores() { // <-- PHẢI CÓ TÊN NÀY
        // 1. Tạo bản sao danh sách để sắp xếp
        List<GameRecord> sortedRecords = new ArrayList<>(this.records);

        // 2. Định nghĩa Comparator: Score DESC, Time ASC
        Collections.sort(sortedRecords, new Comparator<GameRecord>() {
            @Override
            public int compare(GameRecord r1, GameRecord r2) {
                int scoreComparison = Integer.compare(r2.getScore(), r1.getScore());
                if (scoreComparison != 0) {
                    return scoreComparison;
                }
                return Long.compare(r1.getTimeElapsedSeconds(), r2.getTimeElapsedSeconds());
            }
        });

        // 3. Giới hạn số lượng bản ghi trả về (ví dụ MAX_DISPLAY_RECORDS = 5)
        int limit = Math.min(sortedRecords.size(), MAX_DISPLAY_RECORDS);

        return sortedRecords.subList(0, limit);
    }
    // Phương thức cũ bị lỗi sẽ được xóa hoặc đổi tên.
    // Đã thay thế logic của loadLeaderboard() tĩnh bằng getTopLeaderboard() không tĩnh.
}