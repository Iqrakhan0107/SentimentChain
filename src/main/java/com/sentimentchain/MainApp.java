package com.sentimentchain;

import com.sentimentchain.service.SentimentAnalyzer;
import com.sentimentchain.SentimentDAO.SentimentDAO;
import com.sentimentchain.model.SentimentRecord;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime;

public class MainApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SentimentAnalyzer analyzer = new SentimentAnalyzer();
        SentimentDAO dao = new SentimentDAO();
        boolean running = true;

        while (running) {
            System.out.println("\n=== SentimentChain Menu ===");
            System.out.println("1. Analyze & Save Sentiment");
            System.out.println("2. View All Sentiments");
            System.out.println("3. Search by Entity");
            System.out.println("4. View Sorted by Score");
            System.out.println("5. Filter by Label");
            System.out.println("6. View Latest Record per Entity");
            System.out.println("7. View Entity History (Timeline)");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": {
                    System.out.print("Enter entity name: ");
                    String entity = sc.nextLine();
                    System.out.print("Enter text to analyze: ");
                    String text = sc.nextLine();
                    double score = analyzer.analyzeSentiment(text);
                    String label = analyzer.getSentimentLabel(score);
                    SentimentRecord record = new SentimentRecord(entity, text, score, label, LocalDateTime.now().toString());
                    dao.saveSentiment(record);
                    System.out.println("Saved: " + record.getTimestamp() + " | " + entity + " | " + score + " (" + label + ")");
                    break;
                }
                case "2": {
                    List<SentimentRecord> all = dao.getAllSentiments();
                    if (all.isEmpty()) System.out.println("No records.");
                    else all.forEach(r -> System.out.println(r.getTimestamp() + " | " + r.getEntity() + " | " + r.getText() + " | " + r.getScore() + " | " + r.getLabel()));
                    break;
                }
                case "3": {
                    System.out.print("Enter entity to search: ");
                    String e = sc.nextLine();
                    List<SentimentRecord> results = dao.searchByEntity(e);
                    if (results.isEmpty()) System.out.println("No records for " + e);
                    else results.forEach(System.out::println);
                    break;
                }
                case "4": {
                    List<SentimentRecord> sorted = dao.getSortedByScore();
                    sorted.forEach(System.out::println);
                    break;
                }
                case "5": {
                    System.out.print("Enter label (Positive/Negative/Neutral): ");
                    String label = sc.nextLine();
                    List<SentimentRecord> filtered = dao.filterByLabel(label);
                    filtered.forEach(System.out::println);
                    break;
                }
                case "6": {
                    System.out.println("Latest per entity:");
                    dao.getLatestRecords().forEach(r -> System.out.println(r.getEntity() + " | " + r.getTimestamp() + " | " + r.getScore() + " | " + r.getLabel()));
                    break;
                }
                case "7": {
                    System.out.print("Enter entity for history: ");
                    String e = sc.nextLine();
                    List<SentimentRecord> history = dao.getEntityHistory(e);
                    if (history.isEmpty()) System.out.println("No history for " + e);
                    else history.forEach(System.out::println);
                    break;
                }
                case "8": {
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                }
                default:
                    System.out.println("Invalid option.");
            }
        }

        sc.close();
    }
}

