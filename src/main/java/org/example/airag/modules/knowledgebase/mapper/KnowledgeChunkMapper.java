package org.example.airag.modules.knowledgebase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.airag.modules.knowledgebase.entity.KnowledgeChunk;

@Mapper
public interface KnowledgeChunkMapper extends BaseMapper<KnowledgeChunk> {
}
