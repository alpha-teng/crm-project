package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.dto.request.LeadRequest;
import com.crm.dto.response.LeadResponse;
import com.crm.entity.Lead;

import java.util.List;

public interface ILeadService extends IService<Lead> {
    
    List<LeadResponse> getAllLeads();
    
    LeadResponse getLeadById(Long id);
    
    LeadResponse createLead(LeadRequest request);
    
    LeadResponse updateLead(Long id, LeadRequest request);
    
    void deleteLead(Long id);
    
    List<LeadResponse> getLeadsByCustomerId(Long customerId);
    
    LeadResponse convertLead(Long id);
}
