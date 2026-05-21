package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("knowledge_docs")
public class KnowledgeDoc {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("content")
    private String content;
    
    @TableField("customer_id")
    private Long customerId;
    
    @TableField("embedding")
    private String embedding; // Store as string for pgvector
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}
