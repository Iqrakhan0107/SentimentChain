package com.sentimentchain.model;

public class SentimentRecord {
    private String entity;
    private String text;
    private double score;
    private String label;
    private String timestamp;

    public SentimentRecord(String entity, String text, double score, String label, String timestamp) {
        this.entity = entity;
        this.text = text;
        this.score = score;
        this.label = label;
        this.timestamp = timestamp;
    }

    public SentimentRecord(String entity, String text, double score, String label) {
        this(entity, text, score, label, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
);
    }

    public String getEntity() { return entity; }
    public String getText() { return text; }
    public double getScore() { return score; }
    public String getLabel() { return label; }
    public String getTimestamp() { return timestamp; }

    public void setEntity(String entity) { this.entity = entity; }
    public void setText(String text) { this.text = text; }
    public void setScore(double score) { this.score = score; }
    public void setLabel(String label) { this.label = label; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return entity + "|" + text + "|" + score + "|" + label + "|" + timestamp;
    }

    public static SentimentRecord fromString(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 5) return null;
            return new SentimentRecord(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3], parts[4]);
        } catch (Exception e) {
            return null;
        }
    }
}

