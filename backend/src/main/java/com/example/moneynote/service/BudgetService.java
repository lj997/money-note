package com.example.moneynote.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.moneynote.entity.Budget;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BudgetService extends IService<Budget> {

    Budget setBudget(Long userId, Long categoryId, Integer year, Integer month, BigDecimal budgetAmount);

    Budget updateBudget(Long userId, Long id, BigDecimal budgetAmount);

    void deleteBudget(Long userId, Long id);

    List<Map<String, Object>> getBudgetsWithUsage(Long userId, Integer year, Integer month);
}
