package com.example.moneynote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_budget")
public class Budget {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long categoryId;

    private Integer year;

    private Integer month;

    private BigDecimal budgetAmount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private String categoryIcon;

    @TableField(exist = false)
    private BigDecimal usedAmount;

    @TableField(exist = false)
    private Integer deleted;
}
