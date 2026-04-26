package com.example.moneynote.controller;

import com.example.moneynote.common.Result;
import com.example.moneynote.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/expense-by-category")
    public Result<List<Map<String, Object>>> expenseByCategory(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        LocalDate now = LocalDate.now();
        int targetYear = year != null ? year : now.getYear();
        int targetMonth = month != null ? month : now.getMonthValue();

        List<Map<String, Object>> data = statisticsService.getExpenseByCategory(userId, targetYear, targetMonth);
        return Result.success(data);
    }

    @GetMapping("/monthly-trend")
    public Result<List<Map<String, Object>>> monthlyTrend(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer months) {
        int targetMonths = months != null ? months : 6;
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusMonths(targetMonths - 1);

        List<Map<String, Object>> data = statisticsService.getMonthlyTrend(
                userId, startDate.getYear(), startDate.getMonthValue(), targetMonths);
        return Result.success(data);
    }
}
