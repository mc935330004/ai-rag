package org.example.airag.modules.knowledgebase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.airag.modules.knowledgebase.entity.KnowledgeDocumentVersion;
import org.example.airag.modules.knowledgebase.mapper.KnowledgeDocumentVersionMapper;
import org.example.airag.modules.knowledgebase.service.KnowledgeDocumentVersionService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeDocumentVersionServiceImpl extends ServiceImpl<KnowledgeDocumentVersionMapper, KnowledgeDocumentVersion>
        implements KnowledgeDocumentVersionService {
}
