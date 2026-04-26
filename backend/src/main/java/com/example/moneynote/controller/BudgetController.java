package com.example.moneynote.controller;

import com.example.moneynote.common.Result;
import com.example.moneynote.entity.Budget;
import com.example.moneynote.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/set")
    public Result<Budget> set(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long categoryId = Long.valueOf(params.get("categoryId").toString());
        Integer year = Integer.valueOf(params.get("year").toString());
        Integer month = Integer.valueOf(params.get("month").toString());
        BigDecimal budgetAmount = new BigDecimal(params.get("budgetAmount").toString());

        Budget budget = budgetService.setBudget(userId, categoryId, year, month, budgetAmount);
        return Result.success(budget);
    }

    @PostMapping("/update")
    public Result<Budget> update(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long id = Long.valueOf(params.get("id").toString());
        BigDecimal budgetAmount = new BigDecimal(params.get("budgetAmount").toString());

        Budget budget = budgetService.updateBudget(userId, id, budgetAmount);
        return Result.success(budget);
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long id = Long.valueOf(params.get("id").toString());

        budgetService.deleteBudget(userId, id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        List<Map<String, Object>> budgets = budgetService.getBudgetsWithUsage(userId, year, month);
        return Result.success(budgets);
    }
}
