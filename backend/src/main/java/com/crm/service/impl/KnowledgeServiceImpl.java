package com.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.dto.request.KnowledgeRequest;
import com.crm.dto.response.KnowledgeResponse;
import com.crm.entity.KnowledgeDoc;
import com.crm.mapper.KnowledgeDocMapper;
import com.crm.service.IKnowledgeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeDocMapper, KnowledgeDoc> implements IKnowledgeService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${app.ai.embedding.url}")
    private String embeddingUrl;
    
    @Value("${app.ai.embedding.api-key}")
    private String embeddingApiKey;
    
    @Value("${app.ai.embedding.model}")
    private String embeddingModel;
    
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
            // Generate embedding
            String embedding = generateEmbedding(request.getContent());
            
            // Create doc and save with embedding
            KnowledgeDoc doc = new KnowledgeDoc();
            doc.setTitle(request.getTitle());
            doc.setContent(request.getContent());
            doc.setCategory(request.getCategory());
            doc.setCustomerId(request.getCustomerId());
            doc.setEmbedding(embedding);
            
            // Use mapper's custom insert method
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
            // Generate embedding for the query
            String queryEmbedding = generateEmbedding(query);
            
            // Search using mapper's custom method
            return baseMapper.searchByVectorSimilarity(queryEmbedding, limit);
            
        } catch (Exception e) {
            log.error("Failed to search similar docs", e);
            throw new RuntimeException("Failed to search: " + e.getMessage(), e);
        }
    }
    
    private String generateEmbedding(String text) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(embeddingApiKey);
        
        java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("model", embeddingModel);
        requestBody.put("input", text);
        
        HttpEntity<java.util.Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(embeddingUrl, request, String.class);
        
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to generate embedding: " + response.getBody());
        }
        
        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode embeddingNode = root.path("data").get(0).path("embedding");
        
        // Convert to pgvector format: [v1,v2,v3,...]
        String embeddingStr = "[" + embeddingNode.iterator().next().asText();
        // Actually, let me properly iterate
        StringBuilder sb = new StringBuilder("[");
        for (JsonNode node : embeddingNode) {
            if (sb.length() > 1) {
                sb.append(",");
            }
            sb.append(node.asDouble());
        }
        sb.append("]");
        
        log.info("Generated embedding with {} dimensions", embeddingNode.size());
        return sb.toString();
    }
}
