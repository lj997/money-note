package com.example.moneynote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.moneynote.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
