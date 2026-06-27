package org.example.airag.modules.knowledgebase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.airag.modules.knowledgebase.entity.KnowledgeBase;


/**
 * 知识库文件 Mapper
 */
@Mapper
public interface KnowledgeBaseMapper extends BaseMapper<KnowledgeBase> {

}