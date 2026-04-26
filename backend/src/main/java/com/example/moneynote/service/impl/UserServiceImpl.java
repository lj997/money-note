package com.example.moneynote.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.moneynote.entity.User;
import com.example.moneynote.mapper.UserMapper;
import com.example.moneynote.service.CategoryService;
import com.example.moneynote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public User register(String username, String password, String nickname) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User existUser = this.getOne(queryWrapper);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(MD5.create().digestHex(password));
        user.setNickname(nickname != null ? nickname : username);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        this.save(user);
        
        categoryService.initDefaultCategories(user.getId());
        return user;
    }

    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        String md5Password = MD5.create().digestHex(password);
        if (!md5Password.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        user.setPassword(null);
        return user;
    }
}
