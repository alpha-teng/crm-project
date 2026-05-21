package com.crm.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AiQueryResponse {
    
    private String answer;
    
    private List<KnowledgeResponse> relatedDocs;
}
