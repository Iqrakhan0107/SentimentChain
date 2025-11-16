package com.sentimentchain.api;

import com.sentimentchain.dao.SentimentDAO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    private final SentimentDAO dao = new SentimentDAO();

    // 1. Summary Cards
    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return dao.getSummaryStats();
    }

    // 2. Pie Chart
    @GetMapping("/pie")
    public Map<String, Double> getPieChartData() {
        return dao.getPieChartStats();
    }

    // 3. Line Chart Trend
    @GetMapping("/trend")
    public List<Map<String, Object>> getTrendData() {
        return dao.getTrendStats();
    }

    // 4. Calendar Entries
    @GetMapping("/calendar")
    public List<Map<String, Object>> getCalendarData() {
        return dao.getCalendarEntries();
    }
}
package com.sentimentchain.api;

import com.sentimentchain.dao.SentimentDAO;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    private final SentimentDAO dao = new SentimentDAO();

    // 1. Summary Cards
    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return dao.getSummaryStats();
    }

    // 2. Pie Chart
    @GetMapping("/pie")
    public Map<String, Double> getPieChartData() {
        return dao.getPieChartStats();
    }

    // 3. Line Chart Trend
    @GetMapping("/trend")
    public List<Map<String, Object>> getTrendData() {
        return dao.getTrendStats();
    }

    // 4. Calendar Entries
    @GetMapping("/calendar")
    public List<Map<String, Object>> getCalendarData() {
        return dao.getCalendarEntries();
    }
}
