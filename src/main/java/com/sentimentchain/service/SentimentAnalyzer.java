package com.sentimentchain.service;

import com.sentimentchain.model.SentimentRecord;
import com.sentimentchain.node.NodeManager;

import java.util.List;

public class SentimentAnalyzer {

    private NodeManager nodeManager;
    private List<String> availableNodes;

    public SentimentAnalyzer() {
        nodeManager = new NodeManager();
        refreshNodes();
    }

    /** Refresh available blockchain nodes */
    public void refreshNodes() {
        availableNodes = nodeManager.getAvailableNodes();
        if (availableNodes.isEmpty()) {
            System.out.println(" No blockchain nodes available, using local analysis.");
        } else {
            System.out.println(" Available nodes: " + availableNodes);
        }
    }

    /** Main method for sentiment analysis */
    public double analyzeSentiment(String text) {
        text = text.toLowerCase();

        // Use blockchain score if nodes are available
        if (!availableNodes.isEmpty()) {
            String node = availableNodes.get(0); // pick first available node
            System.out.println("Using blockchain node: " + node);

            // Placeholder for future blockchain integration
            // double blockchainScore = fetchScoreFromBlockchain(node, text);
            // return blockchainScore;
        }

        // Local regex-based scoring fallback
        if (text.matches(".*(increase|rise|growth|booming|skyrocketing|surging|uptrend|outperform|amazing).*")) return 0.8;
        if (text.matches(".*(fall|drop|decline|decreasing|downtrend|crashing|underperform|loss).*")) return -0.8;
        if (text.matches(".*(stable|steady|unchanged|neutral|sideways).*")) return 0.0;

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
