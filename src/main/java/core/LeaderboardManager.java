package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardManager {
    private static LeaderboardManager instance;
    private List<GameRecord> records;
    private static final String FILE_NAME = "leaderboard.ser";
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

    @SuppressWarnings("unchecked")
    private List<GameRecord> loadRecords() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            System.out.println("Leaderboard loaded successfully.");
            return (List<GameRecord>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Leaderboard file not found. Creating new list.");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error loading leaderboard. Starting with empty list.");
            return new ArrayList<>();
        }
    }

    private void saveRecords() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRecord(GameRecord record) {
        records.add(record);
        saveRecords();
    }

    public List<GameRecord> getTopScores() {
        List<GameRecord> sortedRecords = new ArrayList<>(this.records);

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

        int limit = Math.min(sortedRecords.size(), MAX_DISPLAY_RECORDS);
        return sortedRecords.subList(0, limit);
    }
}