package com.example.moneynote.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.moneynote.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    void initDefaultCategories(Long userId);

    List<Category> listByUserId(Long userId, Integer type);

    Category addCategory(Long userId, String name, Integer type, String icon, Integer sort);

    Category updateCategory(Long userId, Long id, String name, String icon, Integer sort);

    void deleteCategory(Long userId, Long id);
}
