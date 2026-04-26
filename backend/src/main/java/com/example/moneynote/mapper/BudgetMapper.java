package com.example.moneynote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.moneynote.entity.Budget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface BudgetMapper extends BaseMapper<Budget> {

    @Select("SELECT b.*, c.name as category_name, c.icon as category_icon " +
            "FROM t_budget b LEFT JOIN t_category c ON b.category_id = c.id " +
            "WHERE b.user_id = #{userId} AND b.year = #{year} AND b.month = #{month} " +
            "AND b.deleted = 0")
    List<Map<String, Object>> selectBudgetsWithCategory(@Param("userId") Long userId,
                                                          @Param("year") Integer year,
                                                          @Param("month") Integer month);

    @Select("SELECT COALESCE(SUM(r.amount), 0) FROM t_record r " +
            "WHERE r.user_id = #{userId} AND r.category_id = #{categoryId} " +
            "AND r.type = 0 AND r.record_date >= #{startDate} AND r.record_date <= #{endDate} " +
            "AND r.deleted = 0")
    BigDecimal sumCategoryExpense(@Param("userId") Long userId,
                                   @Param("categoryId") Long categoryId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);
}
