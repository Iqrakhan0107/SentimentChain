package com.sentimentchain.service;

public class SentimentAnalyzer {

    public double analyzeSentiment(String text) {
        text = text.toLowerCase();

         // Positive
        if (text.contains("increase") || text.contains("rise") || text.contains("growth") ||
            text.contains("booming") || text.contains("skyrocketing") || text.contains("surging") ||
            text.contains("uptrend") || text.contains("outperform") || text.contains("amazing")) {
            return 0.8;
        } 
        // Negative
        else if (text.contains("fall") || text.contains("drop") || text.contains("decline") ||
                 text.contains("decreasing") || text.contains("downtrend") || text.contains("crashing") ||
                 text.contains("underperform") || text.contains("loss")) {
            return -0.8;
        } 
        // Neutral
        else if (text.contains("stable") || text.contains("steady") || text.contains("unchanged") ||
                 text.contains("neutral") || text.contains("sideways")) {
            return 0.0;
        } 
        // Default fallback
        else {
            return 0.0;
        }
    }

    public String getSentimentLabel(double score) {
        if (score > 0.3) return "Positive";
        else if (score < -0.3) return "Negative";
        else return "Neutral";
    }
}
