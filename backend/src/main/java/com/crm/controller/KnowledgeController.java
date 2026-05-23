package com.crm.controller;

import com.crm.dto.request.KnowledgeRequest;
import com.crm.dto.request.SearchRequest;
import com.crm.dto.response.KnowledgeResponse;
import com.crm.entity.KnowledgeDoc;
import com.crm.service.IFileUploadService;
import com.crm.service.IKnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
@Slf4j
public class KnowledgeController {
    
    private final IKnowledgeService knowledgeService;
    private final IFileUploadService fileUploadService;
    
    @GetMapping
    public ResponseEntity<List<KnowledgeResponse>> getAllDocs() {
        return ResponseEntity.ok(knowledgeService.getAllDocs());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeResponse> getDocById(@PathVariable Long id) {
        return ResponseEntity.ok(knowledgeService.getDocById(id));
    }
    
    @PostMapping("/add")
    public ResponseEntity<KnowledgeResponse> addDoc(@RequestBody KnowledgeRequest request) {
        KnowledgeResponse response = knowledgeService.createDoc(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // 文件上传 → 自动提取文本 + 自动向量化入库
    @PostMapping("/upload")
    public ResponseEntity<KnowledgeResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "customerId", required = false) Long customerId) {
        try {
            String filename = file.getOriginalFilename();
            if (filename == null || filename.isBlank()) {
                throw new IllegalArgumentException("文件名不能为空");
            }
            long maxSize = 10 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                throw new IllegalArgumentException("文件大小不能超过 10MB");
            }
            KnowledgeResponse response = fileUploadService.uploadAndIndex(
                    filename, file.getBytes(), customerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("文件处理失败: " + e.getMessage(), e);
        }
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
    public ResponseEntity<List<KnowledgeResponse>> searchSimilarGet(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        List<KnowledgeDoc> docs = knowledgeService.searchSimilar(query, limit);
        List<KnowledgeResponse> responses = docs.stream()
                .map(KnowledgeResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping("/search")
    public ResponseEntity<List<KnowledgeResponse>> searchSimilar(
            @RequestBody SearchRequest request) {
        List<KnowledgeDoc> docs = knowledgeService.searchSimilar(request.getQuery(), request.getLimit());
        List<KnowledgeResponse> responses = docs.stream()
                .map(KnowledgeResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
