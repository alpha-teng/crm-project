package com.crm.controller;

import com.crm.dto.request.KnowledgeRequest;
import com.crm.dto.response.KnowledgeResponse;
import com.crm.entity.KnowledgeDoc;
import com.crm.service.IKnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
@Slf4j
public class KnowledgeController {
    
    private final IKnowledgeService knowledgeService;
    
    @GetMapping
    public ResponseEntity<List<KnowledgeResponse>> getAllDocs() {
        return ResponseEntity.ok(knowledgeService.getAllDocs());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeResponse> getDocById(@PathVariable Long id) {
        return ResponseEntity.ok(knowledgeService.getDocById(id));
    }
    
    @PostMapping
    public ResponseEntity<KnowledgeResponse> createDoc(@RequestBody KnowledgeRequest request) {
        KnowledgeResponse response = knowledgeService.createDoc(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoc(@PathVariable Long id) {
        knowledgeService.deleteDoc(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<KnowledgeResponse>> getDocsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(knowledgeService.getDocsByCustomerId(customerId));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<KnowledgeResponse>> searchSimilar(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        List<KnowledgeDoc> docs = knowledgeService.searchSimilar(query, limit);
        List<KnowledgeResponse> responses = docs.stream()
                .map(KnowledgeResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
