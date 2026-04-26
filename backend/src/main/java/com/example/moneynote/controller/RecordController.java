package com.example.moneynote.controller;

import com.example.moneynote.common.Result;
import com.example.moneynote.entity.BillRecord;
import com.example.moneynote.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @PostMapping("/add")
    public Result<BillRecord> add(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Integer type = Integer.valueOf(params.get("type").toString());
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        Long categoryId = Long.valueOf(params.get("categoryId").toString());
        LocalDate recordDate = params.containsKey("recordDate") ? LocalDate.parse(params.get("recordDate").toString()) : null;
        String remark = params.containsKey("remark") ? params.get("remark").toString() : null;

        BillRecord record = recordService.addRecord(userId, type, amount, categoryId, recordDate, remark);
        return Result.success(record);
    }

    @PostMapping("/update")
    public Result<BillRecord> update(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long id = Long.valueOf(params.get("id").toString());
        BigDecimal amount = params.containsKey("amount") ? new BigDecimal(params.get("amount").toString()) : null;
        Long categoryId = params.containsKey("categoryId") ? Long.valueOf(params.get("categoryId").toString()) : null;
        LocalDate recordDate = params.containsKey("recordDate") ? LocalDate.parse(params.get("recordDate").toString()) : null;
        String remark = params.containsKey("remark") ? params.get("remark").toString() : null;

        BillRecord record = recordService.updateRecord(userId, id, amount, categoryId, recordDate, remark);
        return Result.success(record);
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long id = Long.valueOf(params.get("id").toString());

        recordService.deleteRecord(userId, id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(required = false) Long categoryId) {
        List<Map<String, Object>> records = recordService.getRecordsByMonth(userId, year, month, categoryId);
        return Result.success(records);
    }

    @GetMapping("/summary")
    public Result<Map<String, BigDecimal>> summary(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Map<String, BigDecimal> summary = recordService.getMonthlySummary(userId, year, month);
        return Result.success(summary);
    }
}
