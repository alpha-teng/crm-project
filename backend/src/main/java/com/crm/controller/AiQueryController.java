package com.crm.controller;

import com.crm.dto.request.AiQueryRequest;
import com.crm.dto.response.AiQueryResponse;
import com.crm.dto.response.KnowledgeResponse;
import com.crm.entity.KnowledgeDoc;
import com.crm.service.IKnowledgeService;
import com.crm.service.ISystemConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ISystemConfigService systemConfigService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    private String getChatUrl() {
        return systemConfigService.getConfig("chat.url",
            "https://api.siliconflow.cn/v1/chat/completions");
    }
    
    private String getChatApiKey() {
        return systemConfigService.getConfig("chat.apiKey",
            "sk-qzuyohpgpcburordewzjsiwwiqrxhlabxxzsjgmlneajfeae");
    }
    
    private String getChatModel() {
        return systemConfigService.getConfig("chat.model",
            "Qwen/Qwen2.5-7B-Instruct");
    }
    
    private double getTemperature() {
        return Double.parseDouble(systemConfigService.getConfig("chat.temperature", "0.7"));
    }
    
    private int getMaxTokens() {
        return Integer.parseInt(systemConfigService.getConfig("chat.maxTokens", "1000"));
    }
    
    private int getTopK() {
        return Integer.parseInt(systemConfigService.getConfig("search.topK", "5"));
    }
    
    @PostMapping("/ask")
    public ResponseEntity<AiQueryResponse> ask(@RequestBody AiQueryRequest request) {
        try {
            int topK = getTopK();
            List<KnowledgeDoc> relatedDocs = knowledgeService.searchSimilar(request.getQuestion(), topK);
            
            StringBuilder context = new StringBuilder();
            for (KnowledgeDoc doc : relatedDocs) {
                context.append("- ").append(doc.getContent()).append("\n");
            }
            
            String answer = generateAnswer(request.getQuestion(), context.toString());
            
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
        String url = getChatUrl();
        String apiKey = getChatApiKey();
        String model = getChatModel();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个CRM助手。根据提供的客户资料和问题，用中文给出准确、简洁的回答。");
        messages.add(systemMessage);
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", String.format(
            "上下文信息:\n%s\n\n用户问题: %s\n\n请基于上下文信息回答问题。如果上下文中没有相关信息，请明确说明。",
            context, question));
        messages.add(userMessage);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("temperature", getTemperature());
        requestBody.put("max_tokens", getMaxTokens());
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to call chat API: " + response.getBody());
        }
        
        JsonNode root = objectMapper.readTree(response.getBody());
        String answer = root.path("choices").get(0).path("message").path("content").asText();
        
        log.info("Generated answer for question: {} using model {}", question, model);
        return answer;
    }
}
