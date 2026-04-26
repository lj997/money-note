package com.example.moneynote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.moneynote.entity.Budget;
import com.example.moneynote.mapper.BudgetMapper;
import com.example.moneynote.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
public class BudgetServiceImpl extends ServiceImpl<BudgetMapper, Budget> implements BudgetService {

    @Autowired
    private BudgetMapper budgetMapper;

    @Override
    public Budget setBudget(Long userId, Long categoryId, Integer year, Integer month, BigDecimal budgetAmount) {
        if (budgetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("预算金额必须大于0");
        }

        LambdaQueryWrapper<Budget> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Budget::getUserId, userId)
                .eq(Budget::getCategoryId, categoryId)
                .eq(Budget::getYear, year)
                .eq(Budget::getMonth, month);
        Budget existBudget = this.getOne(queryWrapper);

        if (existBudget != null) {
            existBudget.setBudgetAmount(budgetAmount);
            existBudget.setUpdateTime(java.time.LocalDateTime.now());
            this.updateById(existBudget);
            return existBudget;
        }

        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setCategoryId(categoryId);
        budget.setYear(year);
        budget.setMonth(month);
        budget.setBudgetAmount(budgetAmount);
        budget.setCreateTime(java.time.LocalDateTime.now());
        budget.setUpdateTime(java.time.LocalDateTime.now());
        this.save(budget);
        return budget;
    }

    @Override
    public Budget updateBudget(Long userId, Long id, BigDecimal budgetAmount) {
        Budget budget = this.getById(id);
        if (budget == null) {
            throw new RuntimeException("预算不存在");
        }
        if (!userId.equals(budget.getUserId())) {
            throw new RuntimeException("无权操作此预算");
        }
        if (budgetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("预算金额必须大于0");
        }

        budget.setBudgetAmount(budgetAmount);
        budget.setUpdateTime(java.time.LocalDateTime.now());
        this.updateById(budget);
        return budget;
    }

    @Override
    public void deleteBudget(Long userId, Long id) {
        Budget budget = this.getById(id);
        if (budget == null) {
            throw new RuntimeException("预算不存在");
        }
        if (!userId.equals(budget.getUserId())) {
            throw new RuntimeException("无权操作此预算");
        }
        this.removeById(id);
    }

    @Override
    public List<Map<String, Object>> getBudgetsWithUsage(Long userId, Integer year, Integer month) {
        List<Map<String, Object>> budgets = budgetMapper.selectBudgetsWithCategory(userId, year, month);
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        for (Map<String, Object> budget : budgets) {
            Long categoryId = ((Number) budget.get("category_id")).longValue();
            BigDecimal usedAmount = budgetMapper.sumCategoryExpense(userId, categoryId, startDate, endDate);
            budget.put("used_amount", usedAmount);
        }

        return budgets;
    }
}
