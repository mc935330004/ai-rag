package org.example.airag.modules.knowledgebase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import org.example.airag.modules.knowledgebase.entity.KnowledgeBase;
import org.example.airag.modules.knowledgebase.mapper.KnowledgeBaseMapper;
import org.example.airag.modules.knowledgebase.service.KnowledgeBaseService;
import org.springframework.stereotype.Service;

/**
 * 知识库文件 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class KnowledgeBaseServiceImpl extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBase>
        implements KnowledgeBaseService {
}