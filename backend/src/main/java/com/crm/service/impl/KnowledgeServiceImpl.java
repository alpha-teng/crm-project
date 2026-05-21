package com.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.dto.request.KnowledgeRequest;
import com.crm.dto.response.KnowledgeResponse;
import com.crm.entity.KnowledgeDoc;
import com.crm.mapper.KnowledgeDocMapper;
import com.crm.service.IKnowledgeService;
import com.crm.service.ISystemConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeDocMapper, KnowledgeDoc> implements IKnowledgeService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ISystemConfigService systemConfigService;
    
    // 从数据库读取配置
    private String getEmbeddingUrl() {
        return systemConfigService.getConfig("embedding.url",
            "https://api.siliconflow.cn/v1/embeddings");
    }
    
    private String getEmbeddingApiKey() {
        return systemConfigService.getConfig("embedding.apiKey",
            "sk-qzuyohpgpcburordewzjsiwwiqrxhlabxxzsjgmlneajfeae");
    }
    
    private String getEmbeddingModel() {
        return systemConfigService.getConfig("embedding.model",
            "BAAI/bge-large-zh-v1.5");
    }
    
    @Override
    public List<KnowledgeResponse> getAllDocs() {
        return list().stream()
                .map(KnowledgeResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public KnowledgeResponse getDocById(Long id) {
        KnowledgeDoc doc = getById(id);
        if (doc == null) {
            throw new RuntimeException("Knowledge doc not found with id: " + id);
        }
        return KnowledgeResponse.fromEntity(doc);
    }
    
    @Override
    public KnowledgeResponse createDoc(KnowledgeRequest request) {
        try {
            String embedding = generateEmbedding(request.getContent());
            
            KnowledgeDoc doc = new KnowledgeDoc();
            doc.setTitle(request.getTitle());
            doc.setContent(request.getContent());
            doc.setCategory(request.getCategory());
            doc.setCustomerId(request.getCustomerId());
            doc.setEmbedding(embedding);
            
            baseMapper.insertWithEmbedding(doc);
            
            log.info("Created knowledge doc with id: {}", doc.getId());
            return KnowledgeResponse.fromEntity(doc);
            
        } catch (Exception e) {
            log.error("Failed to create knowledge doc", e);
            throw new RuntimeException("Failed to create knowledge doc: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteDoc(Long id) {
        if (!removeById(id)) {
            throw new RuntimeException("Knowledge doc not found with id: " + id);
        }
        log.info("Deleted knowledge doc with id: {}", id);
    }
    
    @Override
    public List<KnowledgeResponse> getDocsByCustomerId(Long customerId) {
        return baseMapper.findByCustomerId(customerId).stream()
                .map(KnowledgeResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeDoc> searchSimilar(String query, int limit) {
        try {
            String queryEmbedding = generateEmbedding(query);
            int effectiveLimit = Integer.parseInt(
                systemConfigService.getConfig("embedding.maxResults", "10"));
            return baseMapper.searchByVectorSimilarity(queryEmbedding,
                (limit > 0 && limit < effectiveLimit) ? limit : effectiveLimit);
            
        } catch (Exception e) {
            log.error("Failed to search similar docs", e);
            throw new RuntimeException("Failed to search: " + e.getMessage(), e);
        }
    }
    
    private String generateEmbedding(String text) throws Exception {
        String url = getEmbeddingUrl();
        String apiKey = getEmbeddingApiKey();
        String model = getEmbeddingModel();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        
        java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("model", model);
        requestBody.put("input", text);
        
        HttpEntity<java.util.Map<String, Object>> request = 
            new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        if (response.getStatusCode() != org.springframework.http.HttpStatus.OK) {
            throw new RuntimeException("Failed to generate embedding: " + response.getBody());
        }
        
        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode embeddingNode = root.path("data").get(0).path("embedding");
        
        StringBuilder sb = new StringBuilder("[");
        for (JsonNode node : embeddingNode) {
            if (sb.length() > 1) sb.append(",");
            sb.append(node.asDouble());
        }
        sb.append("]");
        
        log.info("Generated embedding with {} dimensions using model {}", embeddingNode.size(), model);
        return sb.toString();
    }
}
