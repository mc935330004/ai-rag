package org.example.airag.modules.knowledgebase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.airag.common.result.Result;
import org.example.airag.modules.knowledgebase.entity.KnowledgeDocumentVersion;
import org.example.airag.modules.knowledgebase.service.KnowledgeDocumentVersionService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge/document-versions")
@RequiredArgsConstructor
public class KnowledgeDocumentVersionController {

    private final KnowledgeDocumentVersionService versionService;

    @GetMapping("/page")
    public Result<Page<KnowledgeDocumentVersion>> page(Page<KnowledgeDocumentVersion> page) {
        return Result.success(versionService.page(page));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeDocumentVersion> detail(@PathVariable Long id) {
        return Result.success(versionService.getById(id));
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody KnowledgeDocumentVersion version) {
        return Result.success(versionService.save(version));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody KnowledgeDocumentVersion version) {
        return Result.success(versionService.updateById(version));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(versionService.removeById(id));
    }
}
