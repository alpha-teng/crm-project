package com.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.dto.request.OpportunityRequest;
import com.crm.dto.response.OpportunityResponse;
import com.crm.entity.Opportunity;
import com.crm.mapper.OpportunityMapper;
import com.crm.service.IOpportunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpportunityServiceImpl extends ServiceImpl<OpportunityMapper, Opportunity> implements IOpportunityService {
    
    @Override
    public List<OpportunityResponse> getAllOpportunities() {
        return list().stream()
                .map(OpportunityResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public OpportunityResponse getOpportunityById(Long id) {
        Opportunity opportunity = getById(id);
        if (opportunity == null) {
            throw new RuntimeException("Opportunity not found with id: " + id);
        }
        return OpportunityResponse.fromEntity(opportunity);
    }
    
    @Override
    public OpportunityResponse createOpportunity(OpportunityRequest request) {
        Opportunity opportunity = new Opportunity();
        mapRequestToEntity(request, opportunity);
        
        save(opportunity);
        log.info("Created opportunity with id: {}", opportunity.getId());
        return OpportunityResponse.fromEntity(opportunity);
    }
    
    @Override
    public OpportunityResponse updateOpportunity(Long id, OpportunityRequest request) {
        Opportunity opportunity = getById(id);
        if (opportunity == null) {
            throw new RuntimeException("Opportunity not found with id: " + id);
        }
        
        mapRequestToEntity(request, opportunity);
        
        updateById(opportunity);
        log.info("Updated opportunity with id: {}", opportunity.getId());
        return OpportunityResponse.fromEntity(opportunity);
    }
    
    @Override
    public void deleteOpportunity(Long id) {
        if (!removeById(id)) {
            throw new RuntimeException("Opportunity not found with id: " + id);
        }
        log.info("Deleted opportunity with id: {}", id);
    }
    
    @Override
    public List<OpportunityResponse> getOpportunitiesByCustomerId(Long customerId) {
        return baseMapper.findByCustomerId(customerId).stream()
                .map(OpportunityResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    private void mapRequestToEntity(OpportunityRequest request, Opportunity entity) {
        entity.setCustomerId(request.getCustomerId());
        entity.setTitle(request.getTitle());
        entity.setAmount(request.getAmount());
        entity.setStage(request.getStage());
        entity.setProbability(request.getProbability());
        entity.setExpectedCloseDate(request.getExpectedCloseDate());
        entity.setRemark(request.getRemark());
    }
}
