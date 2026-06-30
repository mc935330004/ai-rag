package org.example.airag.modules.knowledgebase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.airag.modules.knowledgebase.entity.KnowledgeChunk;
import org.example.airag.modules.knowledgebase.mapper.KnowledgeChunkMapper;
import org.example.airag.modules.knowledgebase.service.KnowledgeChunkService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeChunkServiceImpl extends ServiceImpl<KnowledgeChunkMapper, KnowledgeChunk>
        implements KnowledgeChunkService {
}
