package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.dto.request.DealRequest;
import com.crm.dto.response.DealResponse;
import com.crm.entity.Deal;

import java.util.List;

public interface IDealService extends IService<Deal> {
    
    List<DealResponse> getAllDeals();
    
    DealResponse getDealById(Long id);
    
    DealResponse createDeal(DealRequest request);
    
    DealResponse updateDeal(Long id, DealRequest request);
    
    void deleteDeal(Long id);
    
    List<DealResponse> getDealsByCustomerId(Long customerId);
}
