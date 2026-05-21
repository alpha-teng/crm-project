package com.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.dto.request.DealRequest;
import com.crm.dto.response.DealResponse;
import com.crm.entity.Deal;
import com.crm.mapper.DealMapper;
import com.crm.service.IDealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealServiceImpl extends ServiceImpl<DealMapper, Deal> implements IDealService {
    
    @Override
    public List<DealResponse> getAllDeals() {
        return list().stream()
                .map(DealResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public DealResponse getDealById(Long id) {
        Deal deal = getById(id);
        if (deal == null) {
            throw new RuntimeException("Deal not found with id: " + id);
        }
        return DealResponse.fromEntity(deal);
    }
    
    @Override
    public DealResponse createDeal(DealRequest request) {
        Deal deal = new Deal();
        mapRequestToEntity(request, deal);
        
        save(deal);
        log.info("Created deal with id: {}", deal.getId());
        return DealResponse.fromEntity(deal);
    }
    
    @Override
    public DealResponse updateDeal(Long id, DealRequest request) {
        Deal deal = getById(id);
        if (deal == null) {
            throw new RuntimeException("Deal not found with id: " + id);
        }
        
        mapRequestToEntity(request, deal);
        
        updateById(deal);
        log.info("Updated deal with id: {}", deal.getId());
        return DealResponse.fromEntity(deal);
    }
    
    @Override
    public void deleteDeal(Long id) {
        if (!removeById(id)) {
            throw new RuntimeException("Deal not found with id: " + id);
        }
        log.info("Deleted deal with id: {}", id);
    }
    
    @Override
    public List<DealResponse> getDealsByCustomerId(Long customerId) {
        return baseMapper.findByCustomerId(customerId).stream()
                .map(DealResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    private void mapRequestToEntity(DealRequest request, Deal entity) {
        entity.setCustomerId(request.getCustomerId());
        entity.setOpportunityId(request.getOpportunityId());
        entity.setTitle(request.getTitle());
        entity.setAmount(request.getAmount());
        entity.setContractDate(request.getContractDate());
        entity.setStatus(request.getStatus());
        entity.setRemark(request.getRemark());
    }
}
