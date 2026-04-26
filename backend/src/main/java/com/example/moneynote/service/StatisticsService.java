package com.example.moneynote.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    List<Map<String, Object>> getExpenseByCategory(Long userId, int year, int month);

    List<Map<String, Object>> getMonthlyTrend(Long userId, int startYear, int startMonth, int months);
}
