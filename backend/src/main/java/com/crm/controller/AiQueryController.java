package com.crm.controller;

import com.crm.dto.request.AiQueryRequest;
import com.crm.dto.response.AiQueryResponse;
import com.crm.dto.response.KnowledgeResponse;
import com.crm.entity.KnowledgeDoc;
import com.crm.service.IKnowledgeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai-query")
@RequiredArgsConstructor
@Slf4j
public class AiQueryController {
    
    private final IKnowledgeService knowledgeService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${app.ai.chat.url}")
    private String chatUrl;
    
    @Value("${app.ai.chat.api-key}")
    private String chatApiKey;
    
    @Value("${app.ai.chat.model}")
    private String chatModel;
    
    @PostMapping("/ask")
    public ResponseEntity<AiQueryResponse> ask(@RequestBody AiQueryRequest request) {
        try {
            // 1. Search for similar documents using vector similarity
            List<KnowledgeDoc> relatedDocs = knowledgeService.searchSimilar(request.getQuestion(), 5);
            
            // 2. Build context from related documents
            StringBuilder context = new StringBuilder();
            for (KnowledgeDoc doc : relatedDocs) {
                context.append("- ").append(doc.getContent()).append("\n");
            }
            
            // 3. Call chat API to generate answer
            String answer = generateAnswer(request.getQuestion(), context.toString());
            
            // 4. Build response
            AiQueryResponse response = new AiQueryResponse();
            response.setAnswer(answer);
            response.setRelatedDocs(relatedDocs.stream()
                    .map(KnowledgeResponse::fromEntity)
                    .collect(Collectors.toList()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to process AI query", e);
            throw new RuntimeException("Failed to process query: " + e.getMessage(), e);
        }
    }
    
    private String generateAnswer(String question, String context) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(chatApiKey);
        
        // Build messages for chat API
        List<Map<String, String>> messages = new ArrayList<>();
        
        // System message
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个CRM助手。根据提供的客户资料和问题，用中文给出准确、简洁的回答。");
        messages.add(systemMessage);
        
        // User message with context
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", String.format(
            "上下文信息:\n%s\n\n用户问题: %s\n\n请基于上下文信息回答问题。如果上下文中没有相关信息，请明确说明。",
            context, question));
        messages.add(userMessage);
        
        // Build request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", chatModel);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(chatUrl, request, String.class);
        
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to call chat API: " + response.getBody());
        }
        
        JsonNode root = objectMapper.readTree(response.getBody());
        String answer = root.path("choices").get(0).path("message").path("content").asText();
        
        log.info("Generated answer for question: {}", question);
        return answer;
    }
}
