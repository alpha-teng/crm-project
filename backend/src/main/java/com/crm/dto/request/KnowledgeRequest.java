package com.crm.dto.request;

import lombok.Data;

@Data
public class KnowledgeRequest {
    
    private String content;
    
    private Long customerId;
}
