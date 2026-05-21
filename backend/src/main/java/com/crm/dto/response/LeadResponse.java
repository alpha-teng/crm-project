package com.crm.dto.response;

import com.crm.entity.Lead;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeadResponse {
    
    private Long id;
    
    private Long customerId;
    
    private String name;
    
    private String company;
    
    private String phone;
    
    private String email;
    
    private String source;
    
    private String status;
    
    private Integer score;
    
    private String remark;
    
    private LocalDateTime createdAt;
    
    public static LeadResponse fromEntity(Lead lead) {
        if (lead == null) {
            return null;
        }
        
        LeadResponse response = new LeadResponse();
        response.setId(lead.getId());
        response.setCustomerId(lead.getCustomerId());
        response.setName(lead.getName());
        response.setCompany(lead.getCompany());
        response.setPhone(lead.getPhone());
        response.setEmail(lead.getEmail());
        response.setSource(lead.getSource());
        response.setStatus(lead.getStatus());
        response.setScore(lead.getScore());
        response.setRemark(lead.getRemark());
        response.setCreatedAt(lead.getCreatedAt());
        return response;
    }
}
