package com.example.moneynote.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.moneynote.entity.User;

public interface UserService extends IService<User> {

    User register(String username, String password, String nickname);

    User login(String username, String password);
}
