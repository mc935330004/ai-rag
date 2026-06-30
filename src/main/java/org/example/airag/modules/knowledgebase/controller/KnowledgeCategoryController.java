package org.example.airag.modules.knowledgebase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.airag.common.result.Result;
import org.example.airag.modules.knowledgebase.entity.KnowledgeCategory;
import org.example.airag.modules.knowledgebase.service.KnowledgeCategoryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge/categories")
@RequiredArgsConstructor
public class KnowledgeCategoryController {

    private final KnowledgeCategoryService categoryService;

    @GetMapping("/page")
    public Result<Page<KnowledgeCategory>> page(Page<KnowledgeCategory> page) {
        return Result.success(categoryService.page(page));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeCategory> detail(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody KnowledgeCategory category) {
        return Result.success(categoryService.save(category));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody KnowledgeCategory category) {
        return Result.success(categoryService.updateById(category));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(categoryService.removeById(id));
    }
}
