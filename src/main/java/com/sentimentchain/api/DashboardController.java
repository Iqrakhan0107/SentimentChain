package com.sentimentchain.api;

import com.sentimentchain.SentimentDAO.SentimentDAO;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final SentimentDAO dao;

    public DashboardController(SentimentDAO dao) {
        this.dao = dao;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return dao.getSummaryStats();
    }

    @GetMapping("/pie")
    public Map<String, Double> getPieChartData() {
        return dao.getPieChartStats();
    }

    @GetMapping("/trend")
    public List<Map<String, Object>> getTrendData() {
        return dao.getTrendStats();
    }

    @GetMapping("/calendar")
    public List<Map<String, Object>> getCalendarData() {
        return dao.getCalendarEntries();
    }
}
