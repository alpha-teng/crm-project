package com.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.dto.request.FollowUpRequest;
import com.crm.dto.response.FollowUpResponse;
import com.crm.entity.FollowUp;
import com.crm.mapper.FollowUpMapper;
import com.crm.service.IFollowUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowUpServiceImpl extends ServiceImpl<FollowUpMapper, FollowUp> implements IFollowUpService {
    
    @Override
    public List<FollowUpResponse> getAllFollowUps() {
        return list().stream()
                .map(FollowUpResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public FollowUpResponse getFollowUpById(Long id) {
        FollowUp followUp = getById(id);
        if (followUp == null) {
            throw new RuntimeException("FollowUp not found with id: " + id);
        }
        return FollowUpResponse.fromEntity(followUp);
    }
    
    @Override
    public FollowUpResponse createFollowUp(FollowUpRequest request) {
        FollowUp followUp = new FollowUp();
        mapRequestToEntity(request, followUp);
        
        save(followUp);
        log.info("Created follow-up with id: {}", followUp.getId());
        return FollowUpResponse.fromEntity(followUp);
    }
    
    @Override
    public FollowUpResponse updateFollowUp(Long id, FollowUpRequest request) {
        FollowUp followUp = getById(id);
        if (followUp == null) {
            throw new RuntimeException("FollowUp not found with id: " + id);
        }
        
        mapRequestToEntity(request, followUp);
        
        updateById(followUp);
        log.info("Updated follow-up with id: {}", followUp.getId());
        return FollowUpResponse.fromEntity(followUp);
    }
    
    @Override
    public void deleteFollowUp(Long id) {
        if (!removeById(id)) {
            throw new RuntimeException("FollowUp not found with id: " + id);
        }
        log.info("Deleted follow-up with id: {}", id);
    }
    
    @Override
    public List<FollowUpResponse> getFollowUpsByCustomerId(Long customerId) {
        return baseMapper.findByCustomerId(customerId).stream()
                .map(FollowUpResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    private void mapRequestToEntity(FollowUpRequest request, FollowUp entity) {
        entity.setCustomerId(request.getCustomerId());
        entity.setContent(request.getContent());
        entity.setType(request.getType());
        entity.setNextFollowUpDate(request.getNextFollowUpDate());
    }
}
