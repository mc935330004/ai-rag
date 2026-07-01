package org.example.airag.modules.knowledgebase.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.airag.common.exception.BusinessException;
import org.example.airag.common.exception.ErrorCode;
import org.example.airag.modules.knowledgebase.dto.KnowledgeDocumentQueryRequest;
import org.example.airag.modules.knowledgebase.dto.KnowledgeDocumentQueryResponse;
import org.example.airag.modules.knowledgebase.entity.KnowledgeChunk;
import org.example.airag.modules.knowledgebase.entity.KnowledgeDocument;
import org.example.airag.modules.KnowledgeLog.entity.KnowledgeQueryLog;
import org.example.airag.modules.KnowledgeLog.entity.KnowledgeQueryReference;
import org.example.airag.modules.knowledgebase.service.KnowledgeChunkService;
import org.example.airag.modules.knowledgebase.service.KnowledgeDocumentService;
import org.example.airag.modules.KnowledgeLog.service.KnowledgeQueryLogService;
import org.example.airag.modules.KnowledgeLog.service.KnowledgeQueryReferenceService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static org.springframework.util.StringUtils.truncate;

/**
 * 企业知识文档问答服务。
 *
 * 只面向 knowledge_document 主线，不复用旧 knowledge_base 查询逻辑。
 */
@Service
@RequiredArgsConstructor
public class KnowledgeDocumentQueryService {
    private static final int DEFAULT_TOP_K = 5;
    // 降低相似度阈值，先保证本地联调能召回知识库片段。
    private static final double DEFAULT_MIN_SCORE = 0.2;
    private static final String NO_RESULT_RESPONSE = "抱歉，在选定的知识库中未检索到相关信息。";

    private final KnowledgeDocumentService documentService;
    private final ObjectProvider<KnowledgeBaseVectorService> vectorServiceProvider;
    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
    private final KnowledgeQueryLogService queryLogService;
    private final KnowledgeQueryReferenceService queryReferenceService;
    private final KnowledgeChunkService chunkService;
    /**
     * 根据问题查询知识库文档。
     * @param request
     * @return
     */
    public KnowledgeDocumentQueryResponse query(KnowledgeDocumentQueryRequest request) {
        long start = System.currentTimeMillis();
        String question = request.question() == null ? "" : request.question().trim();
        int topK = request.topK() == null ? DEFAULT_TOP_K : request.topK();
        double minScore = request.minScore() == null ? DEFAULT_MIN_SCORE : request.minScore();
        try {

        if (!StringUtils.hasText(question)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "问题不能为空");
        }
        List<KnowledgeDocument> documents = findPublishedDocuments(request);
        List<Long> versionIds = documents.stream()
                .map(KnowledgeDocument::getCurrentVersionId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (versionIds.isEmpty()) {
            return new KnowledgeDocumentQueryResponse(NO_RESULT_RESPONSE, List.of());
        }
        // 向量检索
        List<Document> hits = requireVectorService().similaritySearchByVersionIds(
                question,
                versionIds,
                topK,
                minScore
        );
            // 过滤已禁用的切片。
            // PGVector 里可能还存在旧向量，但只要 MySQL 中 chunk.enabled = 0，正式问答就不使用。
            hits = filterEnabledChunks(hits);
        if (hits.isEmpty()) {
            saveQueryLog(question,NO_RESULT_RESPONSE,topK, minScore,"NO_RESULT",null,start);
            return new KnowledgeDocumentQueryResponse(NO_RESULT_RESPONSE, List.of());
        }
        String answer = requireChatClient()
                .prompt()
                .system(buildSystemPrompt())
                .user(buildUserPrompt(buildContext(hits), question))
                .call()
                .content();
            // 问答成功后，先保存主日志，再保存本次命中的引用切片。
            KnowledgeQueryLog queryLog = saveQueryLog(question,answer,topK,minScore,
                    "SUCCESS", null, start);
            saveQueryReferences(queryLog.getId(), hits);
        return new KnowledgeDocumentQueryResponse(
                StringUtils.hasText(answer) ? answer.trim() : NO_RESULT_RESPONSE,
                buildReferences(hits)
        );
        }catch (Exception e){
            // 失败也要落库，方便后续排查模型异常、向量异常、参数异常。
            saveQueryLog(question,null,topK, minScore,"FAILED", e.getMessage(),start);
            throw e;
        }
    }

    /**
     * 查询已发布文档，并只使用 currentVersionId 参与正式问答。
     */
    private List<KnowledgeDocument> findPublishedDocuments(KnowledgeDocumentQueryRequest request) {
        return documentService.lambdaQuery()
                .eq(KnowledgeDocument::getDelFlag, 0)
                .eq(KnowledgeDocument::getStatus, "PUBLISHED")
                .isNotNull(KnowledgeDocument::getCurrentVersionId)
                .in(request.categoryIds() != null && !request.categoryIds().isEmpty(),
                        KnowledgeDocument::getCategoryId, request.categoryIds())
                .in(request.documentIds() != null && !request.documentIds().isEmpty(),
                        KnowledgeDocument::getId,request.documentIds())
                .list();
    }

    /**
     * 获取向量检索服务，如果未启用则抛出异常。
     * @return
     */
    private KnowledgeBaseVectorService requireVectorService() {
        KnowledgeBaseVectorService vectorService = vectorServiceProvider.getIfAvailable();
        if (vectorService == null) {
            throw new BusinessException(ErrorCode.KNOWLEDGE_BASE_QUERY_FAILED, "向量检索服务未启用");
        }
        return vectorService;
    }

    /**
     * 获取 AI 对话客户端，如果未启用则抛出异常。
     * @return
     */
    private ChatClient requireChatClient() {
        ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
        if (builder == null) {
            throw new BusinessException(ErrorCode.AI_SERVICE_UNAVAILABLE, "AI 对话服务未启用");
        }
        return builder.build();
    }

    /**
     * 构建上下文内容。
     * @param documents
     * @return
     */
    private String buildContext(List<Document> documents) {
        return documents.stream()
                .map(Document::getText)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining("\n\n---\n\n"));
    }

    /**
     * 构建系统提示。
     * @return
     */
    private String buildSystemPrompt() {
        return """
                你是企业知识库问答助手。
                你必须优先根据检索到的企业知识文档回答。
                如果文档内容不足以回答，请明确说明未检索到足够信息。
                不要编造企业知识文档中没有出现的内容。
                """;
    }

    /**
     * 构建用户提示。
     * @param context
     * @param question
     * @return
     */
    private String buildUserPrompt(String context, String question) {
        return """
                【企业知识文档内容】
                %s

                【用户问题】
                %s
                """.formatted(context, question);
    }

    /**
     * 构建引用信息。
     * @param documents
     * @return
     */
    private List<KnowledgeDocumentQueryResponse.Reference> buildReferences(List<Document> documents) {
        return documents.stream()
                .map(document -> new KnowledgeDocumentQueryResponse.Reference(
                        toLong(metadata(document, "document_id")),
                        toLong(metadata(document, "version_id")),
                        toLong(metadata(document, "chunk_id")),
                        metadata(document, "chunk_index"),
                        metadata(document, "document_title"),
                        metadata(document, "source")
                ))
                .distinct()
                .toList();
    }

    /**
     * 获取文档元数据。
     * @param document
     * @param key
     * @return
     */
    private String metadata(Document document, String key) {
        Object value = document.getMetadata().get(key);
        return value == null ? "" : value.toString();
    }

    /**
     * 将字符串转换为 Long，如果为空则返回 null。
     * @param value
     * @return
     */
    private Long toLong(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return Long.valueOf(value);
    }
    /**
     * 保存问答主日志。
     *
     * 日志主表记录问题、回答、状态和耗时。
     * 引用明细单独保存到 knowledge_query_reference。
     */
    private KnowledgeQueryLog saveQueryLog(String question, String answer,int topK,double minScore,
            String status, String errorMessage,long start ) {
        KnowledgeQueryLog queryLog = new KnowledgeQueryLog();
        queryLog.setQuestion(question);
        queryLog.setAnswer(answer);
        queryLog.setTopK(topK);
        queryLog.setMinScore(BigDecimal.valueOf(minScore));
        queryLog.setStatus(status);
        queryLog.setErrorMessage(errorMessage!=null?truncate(errorMessage):null);
        queryLog.setDurationMs(System.currentTimeMillis() - start);
        queryLog.setCreatedAt(LocalDateTime.now());
        queryLogService.save(queryLog);
        return queryLog;
    }

    /**
     * 保存回答引用来源。
     *
     * hits 是 PGVector 返回的命中文档片段。
     * 这里从 metadata 里取 document_id、version_id、chunk_id 等字段。
     */
    private void saveQueryReferences(Long queryLogId, List<Document> hits) {
        if (queryLogId == null || hits == null || hits.isEmpty()) {
            return;
        }
        List<KnowledgeQueryReference> references = hits.stream()
                .map(hit -> {
                    KnowledgeQueryReference reference = new KnowledgeQueryReference();
                    reference.setQueryLogId(queryLogId);
                    reference.setDocumentId(toLong(metadata(hit, "document_id")));
                    reference.setVersionId(toLong(metadata(hit, "version_id")));
                    reference.setChunkId(toLong(metadata(hit, "chunk_id")));
                    reference.setChunkIndex(Integer.valueOf(metadata(hit, "chunk_index")));
                    reference.setSource(metadata(hit, "source"));
                    reference.setCreatedAt(LocalDateTime.now());
                    return reference;
                })
                .toList();
        queryReferenceService.saveBatch(references);
    }

    /**
     * 过滤已禁用的切片。
     *
     * 向量检索先从 PGVector 召回，再根据 MySQL 的 knowledge_chunk.enabled 做最终过滤。
     * 这样禁用切片不需要立刻删除 PGVector 向量，操作更简单，也方便后续重新启用。
     */
    private List<Document> filterEnabledChunks(List<Document> hits) {
        if (hits == null || hits.isEmpty()) {
            return List.of();
        }
        List<Long> chunkIds = hits.stream()
                .map(hit -> toLong(metadata(hit, "chunk_id")))
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (chunkIds.isEmpty()) {
            return List.of();
        }
        List<Long> enabledChunkIds = chunkService.lambdaQuery()
                .select(KnowledgeChunk::getId)
                .in(KnowledgeChunk::getId, chunkIds)
                .eq(KnowledgeChunk::getEnabled, 1)
                .eq(KnowledgeChunk::getDelFlag, 0)
                .list()
                .stream()
                .map(KnowledgeChunk::getId)
                .toList();
        return hits.stream()
                .filter(hit -> enabledChunkIds.contains(toLong(metadata(hit, "chunk_id"))))
                .toList();
    }
}
