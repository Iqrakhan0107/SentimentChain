package com.sentimentchain.api;

import com.sentimentchain.SentimentDAO.SentimentDAO;
import com.sentimentchain.model.SentimentRecord;
import com.sentimentchain.service.SentimentAnalyzer;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SentimentController {

    private final SentimentAnalyzer analyzer;
    private final SentimentDAO dao;

    public SentimentController(SentimentDAO dao) {
        this.analyzer = new SentimentAnalyzer();
        this.dao = dao;
    }

    @PostMapping("/analyze")
    public SentimentRecord analyze(@RequestBody Map<String, String> body) {
        String entity = body.get("entity");
        String text = body.get("text");

        double score = analyzer.analyzeSentiment(text);
        String label = analyzer.getSentimentLabel(score);

        SentimentRecord record = new SentimentRecord(
                entity,
                text,
                score,
                label,
                java.time.LocalDateTime.now().toString()
        );

        dao.saveSentiment(record);
        return record;
    }

    @GetMapping("/all-sentiments")
    public List<SentimentRecord> getAllSentiments() {
        return dao.getAllSentiments();
    }

    @GetMapping("/search/{entity}")
    public List<SentimentRecord> search(@PathVariable String entity) {
        return dao.searchByEntity(entity);
    }

    @GetMapping("/sorted-sentiments")
    public List<SentimentRecord> sorted() {
        return dao.getSortedByScore();
    }

    @GetMapping("/filter/{label}")
    public List<SentimentRecord> filter(@PathVariable String label) {
        return dao.filterByLabel(label);
    }

    @GetMapping("/latest-all")
    public List<SentimentRecord> latestAll() {
        return dao.getLatestRecords();
    }

    @GetMapping("/history/{entity}")
    public List<SentimentRecord> fullHistory(@PathVariable String entity) {
        return dao.getEntityHistory(entity);
    }
}
