package com.sentimentchain.api;

import com.sentimentchain.dao.SentimentDAO;
import com.sentimentchain.model.SentimentRecord;
import com.sentimentchain.service.SentimentAnalyzer;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin // allows frontend calls from different port
public class SentimentController {

    private final SentimentAnalyzer analyzer = new SentimentAnalyzer();
    private final SentimentDAO dao = new SentimentDAO();

    // 1. Analyze & Save
    @PostMapping("/analyze")
    public SentimentRecord analyzeSentiment(@RequestBody SentimentRequest request) {
        double score = analyzer.analyzeSentiment(request.getText());
        String label = analyzer.getSentimentLabel(score);
        SentimentRecord record = new SentimentRecord(
                request.getEntity(), request.getText(), score, label, LocalDateTime.now().toString()
        );
        dao.saveSentiment(record);
        return record;
    }

    // 2. View All Sentiments
    @GetMapping("/all")
    public List<SentimentRecord> getAll() {
        return dao.getAllSentiments();
    }

    // 3. Search by Entity
    @GetMapping("/search/{entity}")
    public List<SentimentRecord> search(@PathVariable String entity) {
        return dao.searchByEntity(entity);
    }

    // 4. Sorted by Score
    @GetMapping("/sorted")
    public List<SentimentRecord> getSorted() {
        return dao.getSortedByScore();
    }

    // 5. Filter by Label
    @GetMapping("/filter/{label}")
    public List<SentimentRecord> filterByLabel(@PathVariable String label) {
        return dao.filterByLabel(label);
    }

    // 6. Latest per Entity
    @GetMapping("/latest")
    public List<SentimentRecord> latestPerEntity() {
        return dao.getLatestRecords();
    }

    // 7. Entity History
    @GetMapping("/history/{entity}")
    public List<SentimentRecord> history(@PathVariable String entity) {
        return dao.getEntityHistory(entity);
    }
}

// Request body for analyzing sentiment
class SentimentRequest {
    private String entity;
    private String text;

    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
