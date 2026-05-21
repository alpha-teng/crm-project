package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.dto.request.FollowUpRequest;
import com.crm.dto.response.FollowUpResponse;
import com.crm.entity.FollowUp;

import java.util.List;

public interface IFollowUpService extends IService<FollowUp> {
    
    List<FollowUpResponse> getAllFollowUps();
    
    FollowUpResponse getFollowUpById(Long id);
    
    FollowUpResponse createFollowUp(FollowUpRequest request);
    
    FollowUpResponse updateFollowUp(Long id, FollowUpRequest request);
    
    void deleteFollowUp(Long id);
    
    List<FollowUpResponse> getFollowUpsByCustomerId(Long customerId);
}
