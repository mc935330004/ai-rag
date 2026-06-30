package org.example.airag.modules.knowledgebase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.airag.common.result.Result;
import org.example.airag.modules.knowledgebase.entity.KnowledgeDocument;
import org.example.airag.modules.knowledgebase.service.KnowledgeDocumentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge/documents")
@RequiredArgsConstructor
public class KnowledgeDocumentController {

    private final KnowledgeDocumentService documentService;

    @GetMapping("/page")
    public Result<Page<KnowledgeDocument>> page(Page<KnowledgeDocument> page) {
        return Result.success(documentService.page(page));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeDocument> detail(@PathVariable Long id) {
        return Result.success(documentService.getById(id));
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody KnowledgeDocument document) {
        return Result.success(documentService.save(document));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody KnowledgeDocument document) {
        return Result.success(documentService.updateById(document));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(documentService.removeById(id));
    }
}
