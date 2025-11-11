package com.sentimentchain.SentimentDAO;

import com.sentimentchain.model.SentimentRecord;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SentimentDAO {

    private final String FILE_NAME = "sentiments.txt";
    private final String CSV_FILE = "sentiments.csv";

    // Save a new sentiment (allow duplicates as updates over time)
    public void saveSentiment(SentimentRecord record) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            bw.write(record.toString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Also save to CSV
        saveToCSV(record);
    }

    // Save to CSV file
    private void saveToCSV(SentimentRecord record) {
        boolean fileExists = new File(CSV_FILE).exists();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            // Write header if file didn’t exist
            if (!fileExists) {
                bw.write("Entity,Text,Score,Label,Timestamp");
                bw.newLine();
            }
            // Escape commas in text
            String textEscaped = record.getText().replace(",", ";");
            bw.write(record.getEntity() + "," + textEscaped + "," +
                    record.getScore() + "," + record.getLabel() + "," +
                    record.getTimestamp());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get sorted list by score descending
    public List<SentimentRecord> getSortedByScore() {
        List<SentimentRecord> all = getAllSentiments();
        all.sort((a, b) -> Double.compare(b.getScore(), a.getScore())); // descending
        return all;
    }

    // Get filtered list by label (Positive, Negative, Neutral)
    public List<SentimentRecord> filterByLabel(String label) {
        return getAllSentiments()
                .stream()
                .filter(r -> r.getLabel().equalsIgnoreCase(label))
                .collect(Collectors.toList());
    }

       // Get the latest sentiment per entity (most recent timestamp)
    public List<SentimentRecord> getLatestRecords() {
        List<SentimentRecord> all = getAllSentiments();
        List<SentimentRecord> latest = new ArrayList<>();

        // Track the latest record for each entity
        java.util.Map<String, SentimentRecord> latestMap = new java.util.HashMap<>();
        for (SentimentRecord record : all) {
            SentimentRecord existing = latestMap.get(record.getEntity());
            if (existing == null || record.getTimestamp().isAfter(existing.getTimestamp())) {
                latestMap.put(record.getEntity(), record);
            }
        }

        latest.addAll(latestMap.values());
        // Sort latest list alphabetically by entity name
        latest.sort((a, b) -> a.getEntity().compareToIgnoreCase(b.getEntity()));
        return latest;
    }

    // Get full history of a specific entity (sorted oldest → newest)
    public List<SentimentRecord> getEntityHistory(String entity) {
        List<SentimentRecord> all = getAllSentiments();
        List<SentimentRecord> history = new ArrayList<>();
        for (SentimentRecord record : all) {
            if (record.getEntity().equalsIgnoreCase(entity)) {
                history.add(record);
            }
        }

        // Sort by timestamp ascending (oldest first)
        history.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
        return history;
    }


    // Read all sentiments
    public List<SentimentRecord> getAllSentiments() {
        List<SentimentRecord> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                SentimentRecord record = SentimentRecord.fromString(line);
                if (record != null) list.add(record);
            }
        } catch (FileNotFoundException e) {
            // File not yet created, return empty list
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Search by entity (case-insensitive)
    public List<SentimentRecord> searchByEntity(String entity) {
        return getAllSentiments()
                .stream()
                .filter(record -> record.getEntity().equalsIgnoreCase(entity))
                .collect(Collectors.toList());
    }
}
