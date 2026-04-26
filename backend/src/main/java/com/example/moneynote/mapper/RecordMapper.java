package com.example.moneynote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.moneynote.entity.BillRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface RecordMapper extends BaseMapper<BillRecord> {

    @Select("SELECT r.*, c.name as category_name, c.icon as category_icon " +
            "FROM t_record r LEFT JOIN t_category c ON r.category_id = c.id " +
            "WHERE r.user_id = #{userId} AND r.deleted = 0 " +
            "ORDER BY r.record_date DESC, r.create_time DESC")
    List<Map<String, Object>> selectRecordsWithCategory(@Param("userId") Long userId);

    @Select("SELECT r.*, c.name as category_name, c.icon as category_icon " +
            "FROM t_record r LEFT JOIN t_category c ON r.category_id = c.id " +
            "WHERE r.user_id = #{userId} " +
            "AND r.record_date >= #{startDate} AND r.record_date <= #{endDate} " +
            "AND r.deleted = 0 " +
            "ORDER BY r.record_date DESC, r.create_time DESC")
    List<Map<String, Object>> selectRecordsByDateRange(@Param("userId") Long userId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    @Select("SELECT r.*, c.name as category_name, c.icon as category_icon " +
            "FROM t_record r LEFT JOIN t_category c ON r.category_id = c.id " +
            "WHERE r.user_id = #{userId} " +
            "AND r.record_date >= #{startDate} AND r.record_date <= #{endDate} " +
            "AND r.category_id = #{categoryId} " +
            "AND r.deleted = 0 " +
            "ORDER BY r.record_date DESC, r.create_time DESC")
    List<Map<String, Object>> selectRecordsByDateRangeAndCategory(@Param("userId") Long userId,
                                                                    @Param("startDate") LocalDate startDate,
                                                                    @Param("endDate") LocalDate endDate,
                                                                    @Param("categoryId") Long categoryId);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM t_record " +
            "WHERE user_id = #{userId} AND type = #{type} " +
            "AND record_date >= #{startDate} AND record_date <= #{endDate} " +
            "AND deleted = 0")
    BigDecimal sumAmountByTypeAndDate(@Param("userId") Long userId,
                                       @Param("type") Integer type,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    @Select("SELECT c.id, c.name, c.icon, COALESCE(SUM(r.amount), 0) as total_amount " +
            "FROM t_category c LEFT JOIN t_record r ON c.id = r.category_id " +
            "AND r.user_id = #{userId} AND r.type = 0 " +
            "AND r.record_date >= #{startDate} AND r.record_date <= #{endDate} " +
            "AND r.deleted = 0 " +
            "WHERE c.user_id = #{userId} AND c.type = 0 AND c.deleted = 0 " +
            "GROUP BY c.id, c.name, c.icon " +
            "ORDER BY total_amount DESC")
    List<Map<String, Object>> selectExpenseByCategory(@Param("userId") Long userId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    @Select("SELECT YEAR(r.record_date) as year, MONTH(r.record_date) as month, " +
            "COALESCE(SUM(CASE WHEN r.type = 1 THEN r.amount ELSE 0 END), 0) as income, " +
            "COALESCE(SUM(CASE WHEN r.type = 0 THEN r.amount ELSE 0 END), 0) as expense " +
            "FROM t_record r " +
            "WHERE r.user_id = #{userId} " +
            "AND r.record_date >= #{startDate} AND r.record_date <= #{endDate} " +
            "AND r.deleted = 0 " +
            "GROUP BY YEAR(r.record_date), MONTH(r.record_date) " +
            "ORDER BY YEAR(r.record_date), MONTH(r.record_date)")
    List<Map<String, Object>> selectMonthlyTrend(@Param("userId") Long userId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
}
