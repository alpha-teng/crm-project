package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.dto.request.OpportunityRequest;
import com.crm.dto.response.OpportunityResponse;
import com.crm.entity.Opportunity;

import java.util.List;

public interface IOpportunityService extends IService<Opportunity> {
    
    List<OpportunityResponse> getAllOpportunities();
    
    OpportunityResponse getOpportunityById(Long id);
    
    OpportunityResponse createOpportunity(OpportunityRequest request);
    
    OpportunityResponse updateOpportunity(Long id, OpportunityRequest request);
    
    void deleteOpportunity(Long id);
    
    List<OpportunityResponse> getOpportunitiesByCustomerId(Long customerId);
}
