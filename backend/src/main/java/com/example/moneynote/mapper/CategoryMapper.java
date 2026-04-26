package com.example.moneynote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.moneynote.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
