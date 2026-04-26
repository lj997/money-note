package com.example.moneynote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.moneynote.entity.Category;
import com.example.moneynote.mapper.CategoryMapper;
import com.example.moneynote.service.CategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private static final int TYPE_EXPENSE = 0;
    private static final int TYPE_INCOME = 1;

    @Override
    public void initDefaultCategories(Long userId) {
        List<Category> categories = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        categories.add(createCategory(userId, "餐饮", TYPE_EXPENSE, "🍽️", 1, 1, now));
        categories.add(createCategory(userId, "交通", TYPE_EXPENSE, "🚗", 2, 1, now));
        categories.add(createCategory(userId, "购物", TYPE_EXPENSE, "🛒", 3, 1, now));
        categories.add(createCategory(userId, "娱乐", TYPE_EXPENSE, "🎮", 4, 1, now));
        categories.add(createCategory(userId, "医疗", TYPE_EXPENSE, "💊", 5, 1, now));
        categories.add(createCategory(userId, "教育", TYPE_EXPENSE, "📚", 6, 1, now));
        categories.add(createCategory(userId, "住房", TYPE_EXPENSE, "🏠", 7, 1, now));
        categories.add(createCategory(userId, "其他支出", TYPE_EXPENSE, "📝", 8, 1, now));

        categories.add(createCategory(userId, "工资", TYPE_INCOME, "💰", 1, 1, now));
        categories.add(createCategory(userId, "奖金", TYPE_INCOME, "🎁", 2, 1, now));
        categories.add(createCategory(userId, "投资", TYPE_INCOME, "📈", 3, 1, now));
        categories.add(createCategory(userId, "兼职", TYPE_INCOME, "💼", 4, 1, now));
        categories.add(createCategory(userId, "其他收入", TYPE_INCOME, "📝", 5, 1, now));

        this.saveBatch(categories);
    }

    private Category createCategory(Long userId, String name, Integer type, String icon, Integer sort, Integer isDefault, LocalDateTime now) {
        Category category = new Category();
        category.setUserId(userId);
        category.setName(name);
        category.setType(type);
        category.setIcon(icon);
        category.setSort(sort);
        category.setIsDefault(isDefault);
        category.setCreateTime(now);
        category.setUpdateTime(now);
        return category;
    }

    @Override
    public List<Category> listByUserId(Long userId, Integer type) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getUserId, userId);
        if (type != null) {
            queryWrapper.eq(Category::getType, type);
        }
        queryWrapper.orderByAsc(Category::getSort);
        return this.list(queryWrapper);
    }

    @Override
    public Category addCategory(Long userId, String name, Integer type, String icon, Integer sort) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getUserId, userId)
                .eq(Category::getType, type)
                .eq(Category::getName, name);
        Category existCategory = this.getOne(queryWrapper);
        if (existCategory != null) {
            throw new RuntimeException("该分类已存在");
        }

        Category category = new Category();
        category.setUserId(userId);
        category.setName(name);
        category.setType(type);
        category.setIcon(icon != null ? icon : "📝");
        category.setSort(sort != null ? sort : 99);
        category.setIsDefault(0);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        this.save(category);
        return category;
    }

    @Override
    public Category updateCategory(Long userId, Long id, String name, String icon, Integer sort) {
        Category category = this.getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        if (!userId.equals(category.getUserId())) {
            throw new RuntimeException("无权操作此分类");
        }
        if (category.getIsDefault() == 1) {
            throw new RuntimeException("默认分类不可修改");
        }

        category.setName(name != null ? name : category.getName());
        category.setIcon(icon != null ? icon : category.getIcon());
        category.setSort(sort != null ? sort : category.getSort());
        category.setUpdateTime(LocalDateTime.now());
        this.updateById(category);
        return category;
    }

    @Override
    public void deleteCategory(Long userId, Long id) {
        Category category = this.getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        if (!userId.equals(category.getUserId())) {
            throw new RuntimeException("无权操作此分类");
        }
        if (category.getIsDefault() == 1) {
            throw new RuntimeException("默认分类不可删除");
        }

        this.removeById(id);
    }
}
