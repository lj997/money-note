package com.example.moneynote.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.moneynote.entity.BillRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RecordService extends IService<BillRecord> {

    BillRecord addRecord(Long userId, Integer type, BigDecimal amount, Long categoryId,
                         LocalDate recordDate, String remark);

    BillRecord updateRecord(Long userId, Long id, BigDecimal amount, Long categoryId,
                            LocalDate recordDate, String remark);

    void deleteRecord(Long userId, Long id);

    List<Map<String, Object>> getRecordsByMonth(Long userId, int year, int month, Long categoryId);

    Map<String, BigDecimal> getMonthlySummary(Long userId, int year, int month);
}
