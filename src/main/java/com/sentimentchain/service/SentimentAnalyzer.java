package com.sentimentchain.service;

import com.sentimentchain.model.SentimentRecord;
import java.util.List;

public class SentimentAnalyzer {

    public SentimentAnalyzer() {
        System.out.println("✔ Sentiment Analyzer initialized (local mode)");
    }

    /** Main method for sentiment analysis */
    public double analyzeSentiment(String text) {
        if (text == null) return 0.0;

        text = text.toLowerCase();

        // Local regex-based scoring
        if (text.matches(".*(increase|rise|growth|booming|skyrocketing|surging|uptrend|outperform|amazing).*"))
            return 0.8;

        if (text.matches(".*(fall|drop|decline|decreasing|downtrend|crashing|underperform|loss).*"))
            return -0.8;

        if (text.matches(".*(stable|steady|unchanged|neutral|sideways).*"))
            return 0.0;

        return 0.0;
    }

    /** Assign label based on score */
    public String getSentimentLabel(double score) {
        if (score > 0.3) return "Positive";
        if (score < -0.3) return "Negative";
        return "Neutral";
    }

    /** Utility: sum of all scores */
    public double calculateTotal(List<SentimentRecord> records) {
        return records.stream().mapToDouble(SentimentRecord::getScore).sum();
    }

    /** Utility: percentage growth between totals */
    public double calculateGrowth(double previousTotal, double recentTotal) {
        if (previousTotal == 0) return 0;
        return ((recentTotal - previousTotal) / previousTotal) * 100;
    }
}

