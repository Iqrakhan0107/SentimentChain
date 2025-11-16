package com.sentimentchain.SentimentDAO;

import com.sentimentchain.model.SentimentRecord;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SentimentDAO {

    private final List<SentimentRecord> sentimentList = new ArrayList<>();
    private static final String DATA_FILE = "sentiments.csv";

    public SentimentDAO() {
        loadFromDisk();
    }

    public Map<String, Object> getSummaryStats() {
    List<SentimentRecord> records = getAllSentiments();

    long positive = records.stream().filter(r -> r.getLabel().equalsIgnoreCase("Positive")).count();
    long negative = records.stream().filter(r -> r.getLabel().equalsIgnoreCase("Negative")).count();
    long neutral = records.stream().filter(r -> r.getLabel().equalsIgnoreCase("Neutral")).count();

    Map<String, Object> map = new HashMap<>();
    map.put("positive", positive);
    map.put("negative", negative);
    map.put("neutral", neutral);
    map.put("total", records.size());

    return map;
}

public Map<String, Double> getPieChartStats() {
    Map<String, Object> summary = getSummaryStats();
    double total = (int) summary.get("total");

    if (total == 0) {
        return Map.of("Positive", 0.0, "Neutral", 0.0, "Negative", 0.0);
    }

    Map<String, Double> map = new HashMap<>();
    map.put("Positive", ((int) summary.get("positive") * 100.0) / total);
    map.put("Neutral", ((int) summary.get("neutral") * 100.0) / total);
    map.put("Negative", ((int) summary.get("negative") * 100.0) / total);

    return map;
}

public List<Map<String, Object>> getTrendStats() {
    List<SentimentRecord> records = getAllSentiments();

    return records.stream()
            .collect(Collectors.groupingBy(
                    r -> r.getTimestamp().substring(0, 10),   // extract yyyy-MM-dd
                    TreeMap::new,
                    Collectors.averagingDouble(SentimentRecord::getScore)
            ))
            .entrySet().stream()
            .map(e -> Map.of("date", e.getKey(), "score", e.getValue()))
            .collect(Collectors.toList());
}

public List<Map<String, Object>> getCalendarEntries() {
    List<SentimentRecord> records = getAllSentiments();

    return records.stream()
            .map(r -> Map.of(
                    "date", r.getTimestamp().substring(0, 10),
                    "label", r.getLabel(),
                    "entity", r.getEntity(),
                    "text", r.getText()
            ))
            .collect(Collectors.toList());
}



    private void loadFromDisk() {
        try {
            Path path = Paths.get(DATA_FILE);
            if (!Files.exists(path)) {
                System.out.println("⚠ No existing CSV. Starting fresh.");
                return;
            }

            try (BufferedReader br = Files.newBufferedReader(path)) {
                String line;
                boolean headerSkipped = false;

                while ((line = br.readLine()) != null) {
                    if (!headerSkipped || line.startsWith("Entity")) {
                        headerSkipped = true;
                        continue;
                    }
                    String[] p = line.split(",", 5);
                    if (p.length < 5) continue;

                    sentimentList.add(new SentimentRecord(p[0], p[1], Double.parseDouble(p[2]), p[3], p[4]));
                }
            }

            System.out.println("✔ Loaded " + sentimentList.size() + " records.");

        } catch (Exception e) {
            System.out.println("Error loading CSV: " + e.getMessage());
        }
    }

    public void saveSentiment(SentimentRecord record) {
        sentimentList.add(record);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            bw.write(record.getEntity() + "," +
                     record.getText().replace(",", ";") + "," +
                     record.getScore() + "," +
                     record.getLabel() + "," +
                     record.getTimestamp());
            bw.newLine();
        } catch (Exception e) {
            System.out.println("Error saving sentiment: " + e.getMessage());
        }
    }

    public List<SentimentRecord> getAllSentiments() {
        return new ArrayList<>(sentimentList);
    }

    public List<SentimentRecord> searchByEntity(String entity) {
        return sentimentList.stream()
                .filter(r -> r.getEntity().equalsIgnoreCase(entity))
                .collect(Collectors.toList());
    }

    public List<SentimentRecord> getSortedByScore() {
        return sentimentList.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .collect(Collectors.toList());
    }

    public List<SentimentRecord> filterByLabel(String label) {
        return sentimentList.stream()
                .filter(r -> r.getLabel().equalsIgnoreCase(label))
                .collect(Collectors.toList());
    }

    public List<SentimentRecord> getEntityHistory(String entity) {
        List<SentimentRecord> list = searchByEntity(entity);
        list.sort(Comparator.comparing(r -> LocalDateTime.parse(r.getTimestamp())));
        return list;
    }

    public SentimentRecord getLatestRecord(String entity) {
        return searchByEntity(entity).stream()
                .max(Comparator.comparing(r -> LocalDateTime.parse(r.getTimestamp())))
                .orElse(null);
    }

    public List<SentimentRecord> getLatestRecords() {
        Map<String, SentimentRecord> map = new HashMap<>();
        for (SentimentRecord r : sentimentList) {
            map.merge(r.getEntity(), r, (prev, now) ->
                    LocalDateTime.parse(now.getTimestamp()).isAfter(LocalDateTime.parse(prev.getTimestamp())) ? now : prev
            );
        }
        return new ArrayList<>(map.values());
    }
}


