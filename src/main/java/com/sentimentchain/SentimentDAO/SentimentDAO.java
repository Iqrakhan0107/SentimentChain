package com.sentimentchain.SentimentDAO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentimentchain.model.SentimentRecord;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SentimentDAO {

    private final List<SentimentRecord> sentimentList = new ArrayList<>();
    private static final String DATA_FILE = "sentiments.csv";

    public SentimentDAO() {
        loadFromDisk();
    }

    private LocalDateTime safeParse(String ts) {
        try {
            return LocalDateTime.parse(ts);
        } catch (Exception e) {
            return LocalDateTime.MIN;
        }
    }

    private void loadFromDisk() {
        try {
            Path path = Paths.get(DATA_FILE);
            if (!Files.exists(path)) return;

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

                    sentimentList.add(new SentimentRecord(
                            p[0], p[1], Double.parseDouble(p[2]), p[3], p[4]
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading CSV: " + e.getMessage());
        }
    }

    // ---------------- SAVE SENTIMENT -----------------
    public void saveSentiment(SentimentRecord r) {
        if (r == null) return;

        sentimentList.add(r);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            bw.write(r.getEntity() + "," +
                     r.getText().replace(",", ";").replace("\"", "\\\"") + "," +
                     r.getScore() + "," +
                     r.getLabel() + "," +
                     r.getTimestamp());
            bw.newLine();
        } catch (Exception e) {
            System.out.println("Error saving CSV: " + e.getMessage());
        }

        // Push asynchronously to blockchain
        pushToChain(r);
    }

    private void pushToChain(SentimentRecord record) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String json = String.format(
                "{\"entity\":\"%s\",\"text\":\"%s\",\"score\":%f,\"label\":\"%s\",\"timestamp\":\"%s\"}",
                record.getEntity(),
                record.getText().replace("\"", "\\\""),
                record.getScore(),
                record.getLabel().toLowerCase(),  // blockchain expects lowercase
                record.getTimestamp()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3000/api/pushToChain"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                  .thenAccept(resp -> System.out.println("Blockchain push response: " + resp.body()))
                  .exceptionally(e -> { System.out.println("Blockchain error: " + e.getMessage()); return null; });

        } catch (Exception e) {
            System.out.println("Error pushing to chain: " + e.getMessage());
        }
    }

    // ---------------- FETCH BLOCKCHAIN -----------------
    public List<Map<String, Object>> getBlockchain() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3000/api/chain"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> chainData = mapper.readValue(response.body(), Map.class);

            return (List<Map<String, Object>>) chainData.getOrDefault("chain", Collections.emptyList());
        } catch (Exception e) {
            System.out.println("Error fetching blockchain: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ---------------- QUERY METHODS -----------------
    public List<SentimentRecord> getAllSentiments() {
        return new ArrayList<>(sentimentList);
    }

    public List<SentimentRecord> searchByEntity(String entity) {
        if (entity == null) return Collections.emptyList();
        return sentimentList.stream()
                .filter(r -> r.getEntity().equalsIgnoreCase(entity))
                .collect(Collectors.toList());
    }

    public List<SentimentRecord> filterByLabel(String label) {
        if (label == null) return Collections.emptyList();
        return sentimentList.stream()
                .filter(r -> r.getLabel().equalsIgnoreCase(label))
                .collect(Collectors.toList());
    }

    public List<SentimentRecord> getSortedByScore() {
        return sentimentList.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .collect(Collectors.toList());
    }

    public List<SentimentRecord> getLatestRecords() {
        Map<String, SentimentRecord> map = new HashMap<>();
        for (SentimentRecord r : sentimentList) {
            map.merge(r.getEntity(), r,
                    (oldR, newR) -> safeParse(newR.getTimestamp()).isAfter(safeParse(oldR.getTimestamp())) ? newR : oldR);
        }
        return new ArrayList<>(map.values());
    }

    public List<SentimentRecord> getEntityHistory(String entity) {
        List<SentimentRecord> list = searchByEntity(entity);
        list.sort(Comparator.comparing(r -> safeParse(r.getTimestamp())));
        return list;
    }

    // ---------------- DASHBOARD -----------------
    public Map<String, Object> getSummaryStats() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", sentimentList.size());
        summary.put("positive", filterByLabel("positive").size());
        summary.put("negative", filterByLabel("negative").size());
        summary.put("neutral", filterByLabel("neutral").size());
        return summary;
    }

    public Map<String, Double> getPieChartStats() {
        Map<String, Double> pie = new HashMap<>();
        int total = sentimentList.size() == 0 ? 1 : sentimentList.size();
        pie.put("positive", filterByLabel("positive").size() * 100.0 / total);
        pie.put("negative", filterByLabel("negative").size() * 100.0 / total);
        pie.put("neutral", filterByLabel("neutral").size() * 100.0 / total);
        return pie;
    }

    public List<Map<String, Object>> getTrendStats() {
        Map<String, List<SentimentRecord>> byDate = new HashMap<>();
        for (SentimentRecord r : sentimentList) {
            String date = r.getTimestamp().substring(0, 10);
            byDate.computeIfAbsent(date, k -> new ArrayList<>()).add(r);
        }

        List<Map<String, Object>> trend = new ArrayList<>();
        for (String date : byDate.keySet()) {
            List<SentimentRecord> records = byDate.get(date);
            double avg = records.stream().mapToDouble(SentimentRecord::getScore).average().orElse(0);
            trend.add(Map.of("date", date, "avgScore", avg));
        }

        trend.sort(Comparator.comparing(m -> (String) m.get("date")));
        return trend;
    }

    public List<Map<String, Object>> getCalendarEntries() {
        List<Map<String, Object>> calendar = new ArrayList<>();
        calendar.add(Map.of("date", "2025-11-16", "events", List.of("Nova news", "Stock rise")));
        calendar.add(Map.of("date", "2025-11-17", "events", List.of("Nova minor drop")));
        return calendar;
    }
}
