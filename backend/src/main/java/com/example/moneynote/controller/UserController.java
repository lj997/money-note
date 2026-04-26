package com.example.moneynote.controller;

import com.example.moneynote.common.Result;
import com.example.moneynote.entity.User;
import com.example.moneynote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String nickname = params.get("nickname");
        User user = userService.register(username, password, nickname);
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        User user = userService.login(username, password);
        return Result.success(user);
    }
}
