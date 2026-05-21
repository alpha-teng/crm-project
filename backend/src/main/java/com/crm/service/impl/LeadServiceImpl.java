package com.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.dto.request.LeadRequest;
import com.crm.dto.response.LeadResponse;
import com.crm.entity.Lead;
import com.crm.mapper.LeadMapper;
import com.crm.service.ILeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadServiceImpl extends ServiceImpl<LeadMapper, Lead> implements ILeadService {
    
    @Override
    public List<LeadResponse> getAllLeads() {
        return list().stream()
                .map(LeadResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public LeadResponse getLeadById(Long id) {
        Lead lead = getById(id);
        if (lead == null) {
            throw new RuntimeException("Lead not found with id: " + id);
        }
        return LeadResponse.fromEntity(lead);
    }
    
    @Override
    public LeadResponse createLead(LeadRequest request) {
        Lead lead = new Lead();
        mapRequestToEntity(request, lead);
        
        save(lead);
        log.info("Created lead with id: {}", lead.getId());
        return LeadResponse.fromEntity(lead);
    }
    
    @Override
    public LeadResponse updateLead(Long id, LeadRequest request) {
        Lead lead = getById(id);
        if (lead == null) {
            throw new RuntimeException("Lead not found with id: " + id);
        }
        
        mapRequestToEntity(request, lead);
        
        updateById(lead);
        log.info("Updated lead with id: {}", lead.getId());
        return LeadResponse.fromEntity(lead);
    }
    
    @Override
    public void deleteLead(Long id) {
        if (!removeById(id)) {
            throw new RuntimeException("Lead not found with id: " + id);
        }
        log.info("Deleted lead with id: {}", id);
    }
    
    @Override
    public List<LeadResponse> getLeadsByCustomerId(Long customerId) {
        return baseMapper.findByCustomerId(customerId).stream()
                .map(LeadResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    private void mapRequestToEntity(LeadRequest request, Lead entity) {
        entity.setCustomerId(request.getCustomerId());
        entity.setName(request.getName());
        entity.setCompany(request.getCompany());
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setSource(request.getSource());
        entity.setStatus(request.getStatus());
        entity.setScore(request.getScore());
        entity.setRemark(request.getRemark());
    }
    
    @Override
    public LeadResponse convertLead(Long id) {
        Lead lead = getById(id);
        if (lead == null) {
            throw new RuntimeException("Lead not found with id: " + id);
        }
        lead.setStatus("converted");
        updateById(lead);
        log.info("Converted lead {} to customer", id);
        return LeadResponse.fromEntity(lead);
    }
}
