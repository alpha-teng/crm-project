package com.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.entity.KnowledgeDoc;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgeDocMapper extends BaseMapper<KnowledgeDoc> {
    
    @Select("SELECT * FROM knowledge_docs WHERE customer_id = #{customerId}")
    List<KnowledgeDoc> findByCustomerId(Long customerId);
    
    @Insert("""
        INSERT INTO knowledge_docs (content, customer_id, embedding, created_at) 
        VALUES (#{content}, #{customerId}, #{embedding}::vector, NOW())
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertWithEmbedding(KnowledgeDoc doc);
    
    @Select("""
        SELECT *, 1 - (embedding <=> #{embedding}::vector) as similarity 
        FROM knowledge_docs 
        ORDER BY embedding <=> #{embedding}::vector 
        LIMIT #{limit}
        """)
    List<KnowledgeDoc> searchByVectorSimilarity(String embedding, int limit);
}
