package com.crm.dto.response;

import com.crm.entity.KnowledgeDoc;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeResponse {
    
    private Long id;
    
    private String title;
    
    private String content;
    
    private String category;
    
    private Long customerId;
    
    private LocalDateTime createdAt;
    
    public static KnowledgeResponse fromEntity(KnowledgeDoc doc) {
        if (doc == null) {
            return null;
        }
        
        KnowledgeResponse response = new KnowledgeResponse();
        response.setId(doc.getId());
        response.setTitle(doc.getTitle());
        response.setContent(doc.getContent());
        response.setCategory(doc.getCategory());
        response.setCustomerId(doc.getCustomerId());
        response.setCreatedAt(doc.getCreatedAt());
        return response;
    }
}
