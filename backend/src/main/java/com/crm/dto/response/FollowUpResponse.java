package com.crm.dto.response;

import com.crm.entity.FollowUp;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FollowUpResponse {
    
    private Long id;
    
    private Long customerId;
    
    private String content;
    
    private String type;
    
    private LocalDate nextFollowUpDate;
    
    private LocalDateTime createdAt;
    
    public static FollowUpResponse fromEntity(FollowUp followUp) {
        if (followUp == null) {
            return null;
        }
        
        FollowUpResponse response = new FollowUpResponse();
        response.setId(followUp.getId());
        response.setCustomerId(followUp.getCustomerId());
        response.setContent(followUp.getContent());
        response.setType(followUp.getType());
        response.setNextFollowUpDate(followUp.getNextFollowUpDate());
        response.setCreatedAt(followUp.getCreatedAt());
        return response;
    }
}
