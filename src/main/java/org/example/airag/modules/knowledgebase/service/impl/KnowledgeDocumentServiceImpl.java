package org.example.airag.modules.knowledgebase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.airag.modules.knowledgebase.entity.KnowledgeDocument;
import org.example.airag.modules.knowledgebase.mapper.KnowledgeDocumentMapper;
import org.example.airag.modules.knowledgebase.service.KnowledgeDocumentService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeDocumentServiceImpl extends ServiceImpl<KnowledgeDocumentMapper, KnowledgeDocument>
        implements KnowledgeDocumentService {
}
