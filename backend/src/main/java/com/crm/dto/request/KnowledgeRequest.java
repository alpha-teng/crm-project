package com.crm.dto.request;

import lombok.Data;

@Data
public class KnowledgeRequest {
    
    private String title;
    
    private String content;
    
    private String category;
    
    private Long customerId;
}
