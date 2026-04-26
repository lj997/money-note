package com.example.moneynote.service.impl;

import com.example.moneynote.mapper.RecordMapper;
import com.example.moneynote.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private RecordMapper recordMapper;

    @Override
    public List<Map<String, Object>> getExpenseByCategory(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        List<Map<String, Object>> result = recordMapper.selectExpenseByCategory(userId, startDate, endDate);
        List<Map<String, Object>> filteredResult = new ArrayList<>();
        
        for (Map<String, Object> item : result) {
            Object totalAmountObj = item.get("total_amount");
            if (totalAmountObj != null) {
                double totalAmount = ((Number) totalAmountObj).doubleValue();
                if (totalAmount > 0) {
                    filteredResult.add(item);
                }
            }
        }
        
        return filteredResult;
    }

    @Override
    public List<Map<String, Object>> getMonthlyTrend(Long userId, int startYear, int startMonth, int months) {
        YearMonth startYearMonth = YearMonth.of(startYear, startMonth);
        YearMonth endYearMonth = startYearMonth.plusMonths(months - 1);
        
        LocalDate startDate = startYearMonth.atDay(1);
        LocalDate endDate = endYearMonth.atEndOfMonth();
        
        List<Map<String, Object>> dbResult = recordMapper.selectMonthlyTrend(userId, startDate, endDate);
        
        Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();
        for (Map<String, Object> item : dbResult) {
            int year = ((Number) item.get("year")).intValue();
            int month = ((Number) item.get("month")).intValue();
            String key = year + "-" + (month < 10 ? "0" + month : month);
            resultMap.put(key, item);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < months; i++) {
            YearMonth current = startYearMonth.plusMonths(i);
            String key = current.getYear() + "-" + (current.getMonthValue() < 10 ? "0" + current.getMonthValue() : current.getMonthValue());
            String monthLabel = current.getYear() + "年" + current.getMonthValue() + "月";
            
            Map<String, Object> item = resultMap.get(key);
            if (item == null) {
                item = new LinkedHashMap<>();
                item.put("year", current.getYear());
                item.put("month", current.getMonthValue());
                item.put("income", 0);
                item.put("expense", 0);
            }
            item.put("month_label", monthLabel);
            result.add(item);
        }
        
        return result;
    }
}
