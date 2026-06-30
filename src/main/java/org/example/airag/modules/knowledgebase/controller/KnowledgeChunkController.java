package org.example.airag.modules.knowledgebase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.airag.common.result.Result;
import org.example.airag.modules.knowledgebase.entity.KnowledgeChunk;
import org.example.airag.modules.knowledgebase.service.KnowledgeChunkService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge/chunks")
@RequiredArgsConstructor
public class KnowledgeChunkController {

    private final KnowledgeChunkService chunkService;

    @GetMapping("/page")
    public Result<Page<KnowledgeChunk>> page(Page<KnowledgeChunk> page) {
        return Result.success(chunkService.page(page));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeChunk> detail(@PathVariable Long id) {
        return Result.success(chunkService.getById(id));
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody KnowledgeChunk chunk) {
        return Result.success(chunkService.save(chunk));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody KnowledgeChunk chunk) {
        return Result.success(chunkService.updateById(chunk));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(chunkService.removeById(id));
    }
}
