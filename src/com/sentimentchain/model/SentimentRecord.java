package com.sentimentchain.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SentimentRecord {
    private String entity;
    private String text;
    private double score;
    private String label;
    private LocalDateTime timestamp;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public SentimentRecord(String entity, String text, double score, String label) {
        this.entity = entity;
        this.text = text;
        this.score = score;
        this.label = label;
        this.timestamp = LocalDateTime.now();
    }

    public String getEntity() {
        return entity;
    }

    public String getText() {
        return text;
    }

    public double getScore() {
        return score;
    }

    public String getLabel() {
        return label;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // ✅ Converts to text for saving in file
    @Override
    public String toString() {
        return entity + "|" + text + "|" + score + "|" + label + "|" + timestamp.format(FORMATTER);
    }

    // ✅ Converts back from saved file line → SentimentRecord object
    public static SentimentRecord fromString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 5) return null;

            String entity = parts[0];
            String text = parts[1];
            double score = Double.parseDouble(parts[2]);
            String label = parts[3];
            LocalDateTime timestamp = LocalDateTime.parse(parts[4], FORMATTER);

            SentimentRecord record = new SentimentRecord(entity, text, score, label);
            record.timestamp = timestamp;
            return record;
        } catch (Exception e) {
            return null;
        }
    }
}
