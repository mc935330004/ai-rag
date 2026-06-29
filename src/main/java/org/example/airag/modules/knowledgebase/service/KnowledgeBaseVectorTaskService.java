package org.example.airag.modules.knowledgebase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.airag.modules.knowledgebase.dto.VectorTaskDTO;
import org.example.airag.modules.knowledgebase.entity.KnowledgeBaseVectorTask;

import java.util.List;

/**
 * 知识库向量化任务 Service
 */
public interface KnowledgeBaseVectorTaskService extends IService<KnowledgeBaseVectorTask> {

    /**
     * 创建向量化任务
     * @param knowledgeBaseId
     */
    void createVectorizeTask(Long knowledgeBaseId);

    /**
     * 列出向量化任务
     * @param status
     * @param knowledgeBaseId
     * @return
     */
     List<VectorTaskDTO> listTasks(String status, Long knowledgeBaseId);

     /**
     * 重试向量化任务
     * @param taskId
     */
     void retryTask(Long taskId);
}