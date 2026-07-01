package org.example.airag.modules.knowledgebase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.airag.common.result.Result;
import org.example.airag.modules.knowledgebase.dto.KnowledgeDocumentDTO;
import org.example.airag.modules.knowledgebase.dto.KnowledgeDocumentOverviewDTO;
import org.example.airag.modules.knowledgebase.dto.KnowledgeDocumentQueryRequest;
import org.example.airag.modules.knowledgebase.dto.KnowledgeDocumentQueryResponse;
import org.example.airag.modules.knowledgebase.entity.KnowledgeDocument;
import org.example.airag.modules.knowledgebase.service.KnowledgeDocumentService;
import org.example.airag.modules.knowledgebase.service.impl.KnowledgeDocumentQueryService;
import org.example.airag.modules.knowledgebase.vo.KnowledgeDocumentListItemVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge/documents")
@RequiredArgsConstructor
public class KnowledgeDocumentController {

    private final KnowledgeDocumentService documentService;
    private final KnowledgeDocumentQueryService knowledgeDocumentQueryService;

    /**
     * 根据问题查询知识库文档。
     * @param request
     * @return
     */
    @PostMapping("/query")
    public Result<KnowledgeDocumentQueryResponse> query(@RequestBody KnowledgeDocumentQueryRequest request) {
        return Result.success(knowledgeDocumentQueryService.query(request));
    }

    /**
     * 根据id获取知识库文档概述。
     * @param id
     * @return
     */
    @GetMapping("/{id}/overview")
    public Result<KnowledgeDocumentOverviewDTO> overview(@PathVariable Long id) {
        return Result.success(documentService.overview(id));
    }

    /**
     * 获取知识库文档分页列表。
     */
    @GetMapping("/pageList")
    public Result<Page<KnowledgeDocumentListItemVO>> pageList(Page<KnowledgeDocumentListItemVO> page, KnowledgeDocumentDTO query) {
        return Result.success(documentService.findPageList(page, query));
    }

    /**
     * 废止文档。
     *
     * 废止后不再参与正式问答，但保留历史数据。
     */
    @GetMapping("/{id}/deprecated")
    public Result<Void> deprecated(@PathVariable Long id) {
        documentService.deprecatedDocument(id);
        return Result.success("文档已废止");
    }

    /**
     * 归档文档。
     *
     * 归档后不再参与正式问答，适用于历史资料留存。
     */
    @GetMapping("/{id}/archive")
    public Result<Void> archive(@PathVariable Long id) {
        documentService.archiveDocument(id);
        return Result.success("文档已归档");
    }

    /**
     * 恢复文档为已发布。
     *
     * 恢复后文档会重新参与正式问答。
     */
    @PostMapping("/{id}/restorePublished")
    public Result<Void> restorePublished(@PathVariable Long id) {
        documentService.restorePublished(id);
        return Result.success("文档已恢复发布");
    }
}
