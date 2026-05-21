package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.dto.request.KnowledgeRequest;
import com.crm.dto.response.KnowledgeResponse;
import com.crm.entity.KnowledgeDoc;

import java.util.List;

public interface IKnowledgeService extends IService<KnowledgeDoc> {
    
    List<KnowledgeResponse> getAllDocs();
    
    KnowledgeResponse getDocById(Long id);
    
    KnowledgeResponse createDoc(KnowledgeRequest request);
    
    void deleteDoc(Long id);
    
    List<KnowledgeResponse> getDocsByCustomerId(Long customerId);
    
    List<KnowledgeDoc> searchSimilar(String query, int limit);
}
