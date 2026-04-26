package com.example.moneynote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_record")
public class BillRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer type;

    private BigDecimal amount;

    private Long categoryId;

    private LocalDate recordDate;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private String categoryIcon;

    @TableField(exist = false)
    private Integer deleted;
}
