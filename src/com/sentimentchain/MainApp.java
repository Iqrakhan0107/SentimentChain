package com.sentimentchain;

import com.sentimentchain.service.SentimentAnalyzer;
import com.sentimentchain.SentimentDAO.SentimentDAO;
import com.sentimentchain.model.SentimentRecord;

import java.util.List;
import java.util.Scanner;

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

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter entity name (e.g. Tesla): ");
                    String entity = sc.nextLine();

                    System.out.print("Enter text to analyze: ");
                    String text = sc.nextLine();

                    double score = analyzer.analyzeSentiment(text);
                    String label = analyzer.getSentimentLabel(score);

                    SentimentRecord record = new SentimentRecord(entity, text, score, label);
                    dao.saveSentiment(record);

                    System.out.println("\nResult:");
                    System.out.println("[" + java.time.LocalDateTime.now() + "] " + entity + ": " + score + " (" + label + ")");
                    break;

                case "2":
                    List<SentimentRecord> all = dao.getAllSentiments();
                    if (all.isEmpty()) {
                        System.out.println("No sentiment records found.");
                    } else {
                        System.out.println("\nAll Sentiments:");
                        for (SentimentRecord r : all) {
                            System.out.println(r.getTimestamp() + " | " + r.getEntity() + " | " + r.getText() + " | " + r.getScore() + " | " + r.getLabel());
                        }
                    }
                    break;

                case "3":
                    System.out.print("Enter entity name to search: ");
                    String searchEntity = sc.nextLine();
                    List<SentimentRecord> results = dao.searchByEntity(searchEntity);

                    if (results.isEmpty()) {
                        System.out.println("No records found for " + searchEntity);
                    } else {
                        System.out.println("\nSearch Results:");
                        for (SentimentRecord r : results) {
                            System.out.println(r.getTimestamp() + " | " + r.getEntity() + " | " + r.getText() + " | " + r.getScore() + " | " + r.getLabel());
                        }
                    }
                    break;

                    case "4": // Sorted by Score
    List<SentimentRecord> sorted = dao.getSortedByScore();
    if(sorted.isEmpty()) System.out.println("No records found.");
    else {
        System.out.println("\nSorted Sentiments by Score:");
        for(SentimentRecord r : sorted)
            System.out.println(r.getTimestamp() + " | " + r.getEntity() + " | " + r.getText() + " | " + r.getScore() + " | " + r.getLabel());
    }
    break;

case "5": // Filter by Label
    System.out.print("Enter label to filter (Positive/Negative/Neutral): ");
    String labelFilter = sc.nextLine();
    List<SentimentRecord> filtered = dao.filterByLabel(labelFilter);
    if(filtered.isEmpty()) System.out.println("No records with label " + labelFilter);
    else {
        System.out.println("\nFiltered Sentiments by Label: " + labelFilter);
        for(SentimentRecord r : filtered)
            System.out.println(r.getTimestamp() + " | " + r.getEntity() + " | " + r.getText() + " | " + r.getScore() + " | " + r.getLabel());
    }
    break;

         case "6":
                    System.out.println("\n--- Latest Records per Entity ---");
                    List<SentimentRecord> latest = dao.getLatestRecords();
                    latest.forEach(System.out::println);
                    break;

                case "7":
                    System.out.print("Enter entity name to view history: ");
                    String historyEntity = sc.nextLine();
                    List<SentimentRecord> history = dao.getEntityHistory(historyEntity);
                    System.out.println("\n--- History for " + historyEntity + " ---");
                    history.forEach(System.out::println);
                    break;


                case "8":
                    running = false;
                    System.out.println("Exiting SentimentChain. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        sc.close();
    }
}
