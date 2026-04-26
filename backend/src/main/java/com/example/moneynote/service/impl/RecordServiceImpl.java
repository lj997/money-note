package com.example.moneynote.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.moneynote.entity.BillRecord;
import com.example.moneynote.mapper.RecordMapper;
import com.example.moneynote.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, BillRecord> implements RecordService {

    @Autowired
    private RecordMapper recordMapper;

    private static final int TYPE_EXPENSE = 0;
    private static final int TYPE_INCOME = 1;

    @Override
    public BillRecord addRecord(Long userId, Integer type, BigDecimal amount, Long categoryId,
                                 LocalDate recordDate, String remark) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("金额必须大于0");
        }

        BillRecord record = new BillRecord();
        record.setUserId(userId);
        record.setType(type);
        record.setAmount(amount);
        record.setCategoryId(categoryId);
        record.setRecordDate(recordDate != null ? recordDate : LocalDate.now());
        record.setRemark(remark);
        record.setCreateTime(java.time.LocalDateTime.now());
        record.setUpdateTime(java.time.LocalDateTime.now());
        this.save(record);
        return record;
    }

    @Override
    public BillRecord updateRecord(Long userId, Long id, BigDecimal amount, Long categoryId,
                                    LocalDate recordDate, String remark) {
        BillRecord record = this.getById(id);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }
        if (!userId.equals(record.getUserId())) {
            throw new RuntimeException("无权操作此记录");
        }

        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("金额必须大于0");
        }

        record.setAmount(amount != null ? amount : record.getAmount());
        record.setCategoryId(categoryId != null ? categoryId : record.getCategoryId());
        record.setRecordDate(recordDate != null ? recordDate : record.getRecordDate());
        record.setRemark(remark != null ? remark : record.getRemark());
        record.setUpdateTime(java.time.LocalDateTime.now());
        this.updateById(record);
        return record;
    }

    @Override
    public void deleteRecord(Long userId, Long id) {
        BillRecord record = this.getById(id);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }
        if (!userId.equals(record.getUserId())) {
            throw new RuntimeException("无权操作此记录");
        }
        this.removeById(id);
    }

    @Override
    public List<Map<String, Object>> getRecordsByMonth(Long userId, int year, int month, Long categoryId) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        if (categoryId != null) {
            return recordMapper.selectRecordsByDateRangeAndCategory(userId, startDate, endDate, categoryId);
        }
        return recordMapper.selectRecordsByDateRange(userId, startDate, endDate);
    }

    @Override
    public Map<String, BigDecimal> getMonthlySummary(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("income", recordMapper.sumAmountByTypeAndDate(userId, TYPE_INCOME, startDate, endDate));
        summary.put("expense", recordMapper.sumAmountByTypeAndDate(userId, TYPE_EXPENSE, startDate, endDate));
        return summary;
    }
}
