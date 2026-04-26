package com.example.moneynote.controller;

import com.example.moneynote.common.Result;
import com.example.moneynote.entity.Category;
import com.example.moneynote.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> list(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer type) {
        List<Category> categories = categoryService.listByUserId(userId, type);
        return Result.success(categories);
    }

    @PostMapping("/add")
    public Result<Category> add(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String name = params.get("name").toString();
        Integer type = Integer.valueOf(params.get("type").toString());
        String icon = params.containsKey("icon") ? params.get("icon").toString() : null;
        Integer sort = params.containsKey("sort") ? Integer.valueOf(params.get("sort").toString()) : null;

        Category category = categoryService.addCategory(userId, name, type, icon, sort);
        return Result.success(category);
    }

    @PostMapping("/update")
    public Result<Category> update(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long id = Long.valueOf(params.get("id").toString());
        String name = params.containsKey("name") ? params.get("name").toString() : null;
        String icon = params.containsKey("icon") ? params.get("icon").toString() : null;
        Integer sort = params.containsKey("sort") ? Integer.valueOf(params.get("sort").toString()) : null;

        Category category = categoryService.updateCategory(userId, id, name, icon, sort);
        return Result.success(category);
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long id = Long.valueOf(params.get("id").toString());

        categoryService.deleteCategory(userId, id);
        return Result.success();
    }
}
