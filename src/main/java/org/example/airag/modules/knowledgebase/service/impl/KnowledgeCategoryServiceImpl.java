package org.example.airag.modules.knowledgebase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.airag.modules.knowledgebase.entity.KnowledgeCategory;
import org.example.airag.modules.knowledgebase.mapper.KnowledgeCategoryMapper;
import org.example.airag.modules.knowledgebase.service.KnowledgeCategoryService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeCategoryServiceImpl  extends ServiceImpl<KnowledgeCategoryMapper, KnowledgeCategory>
        implements KnowledgeCategoryService {
}
